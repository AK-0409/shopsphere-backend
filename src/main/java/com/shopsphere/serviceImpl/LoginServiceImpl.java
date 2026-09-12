package com.shopsphere.serviceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.LoginRequest;
import com.shopsphere.dto.LoginResponse;
import com.shopsphere.exception.InvalidCredentialsException;
import com.shopsphere.security.JwtService;
import com.shopsphere.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUserEmail(),
                                    request.getUserPassword()
                            )
                    );

            String token = jwtService.generateToken(
                    authentication.getName()
            );

            return new LoginResponse(
                    "Login successful",
                    token
            );

        } catch (AuthenticationException e) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }
    }
}