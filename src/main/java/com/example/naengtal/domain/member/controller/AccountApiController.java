package com.example.naengtal.domain.member.controller;

import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.dto.SignUpResponseDto;
import com.example.naengtal.domain.member.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account/")
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;

    @PostMapping("signup")
    public ResponseEntity<SignUpResponseDto> signUp(@Validated @RequestBody SignUpRequestDto signUpRequestDto) {
        SignUpResponseDto dto = accountService.saveMember(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
