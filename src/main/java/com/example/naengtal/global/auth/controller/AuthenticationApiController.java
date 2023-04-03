package com.example.naengtal.global.auth.controller;

import com.example.naengtal.global.auth.dto.SignInRequestDto;
import com.example.naengtal.global.auth.dto.TokenDto;
import com.example.naengtal.global.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("auth/")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @PostMapping("signin")
    private ResponseEntity<TokenDto> signIn(@Validated @RequestBody SignInRequestDto signInRequestDto) {
        TokenDto tokenDto = authenticationService.signIn(signInRequestDto.getId(), signInRequestDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK)
                .body(tokenDto);
    }
}
