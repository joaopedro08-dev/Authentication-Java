package com.auth.backend_java.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.backend_java.service.TokenService;
import com.auth.backend_java.repository.BlacklistRepository;
import com.auth.backend_java.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            if (blacklistRepository.existsByToken(token)) {
                sendErrorResponse(response, "Token revogado. Por favor, faça login novamente.");
                return;
            }

            tokenService.getDecodedToken(token).ifPresent(decodedJWT -> {
                String email = decodedJWT.getSubject();
                String roleStr = decodedJWT.getClaim("role").asString();

                if (email != null && roleStr != null) {
                    userRepository.findByEmail(email).ifPresent(usuario -> {
                        if (usuario.isEnabled()) {
                            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleStr));
                            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    });
                }
            });
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"errors\": [{\"message\": \"" + message + "\"}]}");
    }

    public String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "").trim();
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}