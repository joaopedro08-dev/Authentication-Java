package com.auth.backend_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;

import com.auth.backend_java.dto.*;
import com.auth.backend_java.model.UserModel;
import com.auth.backend_java.model.RefreshToken;
import com.auth.backend_java.service.UserService;
import com.auth.backend_java.config.SecurityFilter;
import com.auth.backend_java.service.TokenService;
import com.auth.backend_java.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired private UserService userService;
    @Autowired private TokenService tokenService;
    @Autowired private SecurityFilter securityFilter;
    @Autowired private RefreshTokenService refreshTokenService;

    @Value("${api.security.cookie-secure:false}")
    private boolean isSecure;

    private static final int TOKEN_ACCESS_SECONDS = 900;      
    private static final int REFRESH_ACCESS_SECONDS = 604800; 
    private static final String SAME_SITE_POLICY = "Strict";

    @MutationMapping
    public Response signUp(@Argument RegisterUserInput input) {
        return userService.signUp(input);
    }

    @MutationMapping
    public ResponseWithToken signIn(@Argument LoginUserInput input, Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserModel) {
            return new ResponseWithToken("Você já possui uma sessão ativa.", false, Optional.empty());
        }

        ResponseWithToken res = userService.signIn(input);

        if (res.success() && res.token().isPresent()) {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletResponse response = attrs.getResponse();
                UserModel user = userService.getUserInfo(input.email());

                if (user != null && response != null) {
                    ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", res.token().get())
                            .httpOnly(true)
                            .secure(isSecure)
                            .path("/")
                            .maxAge(TOKEN_ACCESS_SECONDS)
                            .sameSite(SAME_SITE_POLICY)
                            .build();

                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                            .httpOnly(true)
                            .secure(isSecure)
                            .path("/")
                            .maxAge(REFRESH_ACCESS_SECONDS)
                            .sameSite(SAME_SITE_POLICY)
                            .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                }
            }
        }
        return res;
    }

    @MutationMapping
    public ResponseWithToken logout(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserModel user)) {
            return new ResponseWithToken("Usuário já está deslogado ou sessão inválida.", false, Optional.empty());
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            HttpServletResponse response = attrs.getResponse();

            String token = securityFilter.recoverToken(request);
            userService.logout(user.getEmail(), token);

            String refreshTokenUuid = null;
            if (request.getCookies() != null) {
                for (var c : request.getCookies()) {
                    if ("refreshToken".equals(c.getName()))
                        refreshTokenUuid = c.getValue();
                }
            }
            if (refreshTokenUuid != null) {
                refreshTokenService.deleteByToken(refreshTokenUuid);
            }

            ResponseCookie cleanJwt = ResponseCookie.from("jwtToken", "")
                    .path("/").maxAge(0).secure(isSecure).httpOnly(true).build();
            ResponseCookie cleanRefresh = ResponseCookie.from("refreshToken", "")
                    .path("/").maxAge(0).secure(isSecure).httpOnly(true).build();

            response.addHeader(HttpHeaders.SET_COOKIE, cleanJwt.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, cleanRefresh.toString());
        }

        return new ResponseWithToken("Logout realizado com sucesso!", true, Optional.empty());
    }

    @MutationMapping
    public ResponseWithToken refreshToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return new ResponseWithToken("Erro de contexto.", false, Optional.empty());

        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        String refreshTokenUuid = null;
        if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("refreshToken".equals(c.getName())) refreshTokenUuid = c.getValue();
            }
        }

        if (refreshTokenUuid == null)
            return new ResponseWithToken("Refresh token ausente.", false, Optional.empty());
        
        return refreshTokenService.findByToken(refreshTokenUuid)
                .map(refreshTokenService::verifyExpiration)
                .map(oldToken -> {
                    UserModel user = oldToken.getUser();
                                        
                    refreshTokenService.deleteByToken(oldToken.getToken());

                    String newAccessToken = tokenService.generateToken(user);

                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

                    ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", newAccessToken)
                            .httpOnly(true).secure(isSecure).path("/").maxAge(TOKEN_ACCESS_SECONDS)
                            .sameSite(SAME_SITE_POLICY).build();

                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                            .httpOnly(true).secure(isSecure).path("/").maxAge(REFRESH_ACCESS_SECONDS)
                            .sameSite(SAME_SITE_POLICY).build();

                    response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

                    return new ResponseWithToken("Tokens renovados com sucesso!", true, Optional.of(newAccessToken));
                })
                .orElse(new ResponseWithToken("Refresh token inválido ou já utilizado.", false, Optional.empty()));
    }

    @QueryMapping
    public UserModel getUserInfo(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserModel user))
            return null;
        return userService.getUserInfo(user.getEmail());
    }
}