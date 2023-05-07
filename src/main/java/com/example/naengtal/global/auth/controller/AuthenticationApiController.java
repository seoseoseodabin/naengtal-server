package com.example.naengtal.global.auth.controller;

import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.auth.dto.SignInRequestDto;
import com.example.naengtal.global.auth.dto.TokenDto;
import com.example.naengtal.global.auth.service.AuthenticationService;
import com.example.naengtal.global.common.annotation.LoggedInUser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.naengtal.global.auth.jwt.JwtAuthenticationFilter.BEARER_PREFIX;


@RestController
@RequiredArgsConstructor
@RequestMapping("auth/")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @PostMapping("signin")
    private ResponseEntity<TokenDto> signIn(@Validated @RequestBody SignInRequestDto signInRequestDto) {
        TokenDto tokenDto = authenticationService.signIn(
                signInRequestDto.getId(), signInRequestDto.getPassword(), signInRequestDto.getFcmToken());

        return ResponseEntity.status(HttpStatus.OK)
                .body(tokenDto);
    }

    @PostMapping("signout")
    private ResponseEntity<String> signOut(@Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                           @Parameter(hidden = true) @LoggedInUser Member member,
                                           @RequestParam("fcmtoken") String fcmToken) {
        authenticationService.signOut(authorization.substring(BEARER_PREFIX.length()), member, fcmToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body("signout success!");
    }
}
