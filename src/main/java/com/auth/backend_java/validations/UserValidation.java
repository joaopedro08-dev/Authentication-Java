package com.auth.backend_java.validations;

import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

import com.auth.backend_java.dto.LoginUserInput;
import com.auth.backend_java.dto.RegisterUserInput;
import com.auth.backend_java.dto.Response;
import com.auth.backend_java.dto.ResponseWithToken;

@Component
public class UserValidation {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,254}$)(?=.{1,64}@)[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ\\s]{3,100}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public Response validateRegistration(RegisterUserInput input) {
        if (input == null) {
            return new Response("Dados não informados.", false);
        }

        String name = trimOrNull(input.name());
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            return new Response("Nome inválido: use apenas letras e espaços (3 a 100 caracteres).", false);
        }

        String email = trimOrNull(input.email());
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return new Response("E-mail em formato inválido.", false);
        }

        String password = input.password();
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            return new Response("Mínimo 8 caracteres, com letra maiúscula, minúscula, número e caractere especial (@$!%*?&).", false);
        }

        if (!password.equals(input.confirmPassword())) {
            return new Response("As senhas não conferem.", false);
        }

        return null;
    }

    public ResponseWithToken validateLogin(LoginUserInput input) {
        if (isBlank(input.email())) {
            return new ResponseWithToken("O e-mail é obrigatório.", false, Optional.empty());
        }

        if (isBlank(input.password())) {
            return new ResponseWithToken("A senha é obrigatória.", false, Optional.empty());
        }

        String email = input.email().trim();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ResponseWithToken("Formato de e-mail inválido.", false, Optional.empty());
        }

        return null;
    }

    private String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}