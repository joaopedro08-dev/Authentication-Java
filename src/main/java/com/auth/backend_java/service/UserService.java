package com.auth.backend_java.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth.backend_java.dto.LoginUserInput;
import com.auth.backend_java.repository.UserRepository;
import com.auth.backend_java.dto.Response;
import com.auth.backend_java.dto.ResponseWithToken;
import com.auth.backend_java.dto.RegisterUserInput;
import com.auth.backend_java.model.BlacklistToken;
import com.auth.backend_java.repository.BlacklistRepository;
import com.auth.backend_java.model.UserModel;
import com.auth.backend_java.model.UserRole;
import com.auth.backend_java.validations.UserValidation;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserValidation userValidation;
    @Autowired
    private BlacklistRepository blacklistRepository;
    @Autowired
    private RateLimitService rateLimitService;

    public ResponseWithToken signIn(LoginUserInput input) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String remoteAddr = attrs.getRequest().getRemoteAddr();
            if (!rateLimitService.resolveBucket(remoteAddr).tryConsume(1)) {
                return new ResponseWithToken("Muitas tentativas de login. Tente novamente em 5 minutos.", false,
                        Optional.empty());
            }
        }

        ResponseWithToken error = userValidation.validateLogin(input);
        if (error != null)
            return error;

        return userRepository.findByEmail(input.email())
                .map(user -> {
                    if (passwordEncoder.matches(input.password(), user.getPassword())) {
                        String token = tokenService.generateToken(user);
                        user.setStatusSystem(true);
                        user.setLastLogin(getCurrentDateTime());
                        userRepository.save(user);
                        return new ResponseWithToken("Login realizado com sucesso!", true, Optional.of(token));
                    }
                    return new ResponseWithToken("Senha incorreta.", false, Optional.empty());
                })
                .orElse(new ResponseWithToken("Usuário não encontrado.", false, Optional.empty()));
    }

    public Response signUp(RegisterUserInput userDto) {
        Response validationError = userValidation.validateRegistration(userDto);
        if (validationError != null)
            return validationError;

        if (userRepository.findByEmail(userDto.email()).isPresent()) {
            return new Response("Este E-mail já está cadastrado.", false);
        }

        UserModel newUser = new UserModel();
        newUser.setName(userDto.name());
        newUser.setEmail(userDto.email());
        newUser.setPassword(passwordEncoder.encode(userDto.password()));
        newUser.setRole(UserRole.USER);
        newUser.setStatusSystem(false);
        newUser.setCreatedAt(getCurrentDateTime());
        newUser.setUpdatedAt(getCurrentDateTime());

        userRepository.save(newUser);
        return new Response("Usuário cadastrado com sucesso!", true);
    }

    public ResponseWithToken logout(String email, String token) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    BlacklistToken blacklisted = new BlacklistToken();
                    blacklisted.setToken(token);

                    LocalDateTime expiry = tokenService.getDecodedToken(token)
                            .map(decoded -> decoded.getExpiresAt().toInstant().atZone(ZoneId.of("America/Sao_Paulo"))
                                    .toLocalDateTime())
                            .orElse(LocalDateTime.now().plusMinutes(15));

                    blacklisted.setExpiresAt(expiry);
                    blacklistRepository.save(blacklisted);

                    user.setStatusSystem(false);
                    userRepository.save(user);

                    return new ResponseWithToken("Logout realizado com sucesso!", true, Optional.empty());
                })
                .orElse(new ResponseWithToken("Usuário não encontrado.", false, Optional.empty()));
    }

    public UserModel getUserInfo(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!user.isStatusSystem()) {
            throw new RuntimeException("Usuário está inativo. Faça login novamente.");
        }

        return user;
    }

    private LocalDateTime getCurrentDateTime() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
    }
}