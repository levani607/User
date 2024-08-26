package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.model.request.LoginRequest;
import org.example.user.model.response.CertificateResponse;
import org.example.user.model.response.TokenResponse;
import org.example.user.security.TokenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final TokenService tokenService;


    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        return tokenService.login(loginRequest);
    }

    @GetMapping("/certs")
    public CertificateResponse getCerts(){
        return tokenService.getCerts();
    }
}
