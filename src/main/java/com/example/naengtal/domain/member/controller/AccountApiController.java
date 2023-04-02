package com.example.naengtal.domain.member.controller;

import com.example.naengtal.domain.member.dto.SignUpDto;
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
    public ResponseEntity<String> signUp(@Validated @RequestBody SignUpDto signUpDto) {
        accountService.saveMember(signUpDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }
}
