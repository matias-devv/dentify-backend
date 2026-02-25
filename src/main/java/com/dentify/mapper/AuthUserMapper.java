package com.dentify.security.mapper;

import com.dentify.security.dto.request.RegisterUserRequest;
import com.dentify.security.model.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class AuthUserMapper {

    private final PasswordEncoder passwordEncoder;

    public AuthUser setAttributesToAuthUser(RegisterUserRequest request) {
        AuthUser authUser = new AuthUser();
        authUser.setUsername(request.name());
        authUser.setPassword( passwordEncoder.encode(request.password()) );
        authUser.setEnabled(true);
        authUser.setAccountNotExpired(true);
        authUser.setAccountNotLocked(true);
        authUser.setCredentialNotExpired(true);
        return authUser;
    }
}
