package com.example.naengtal.domain.member.controller;

import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.dto.SignUpResponseDto;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.domain.member.service.AccountService;
import com.example.naengtal.global.common.annotation.LoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("delete")
    public ResponseEntity<String> withdrawal(@LoggedInUser Member member) {
        accountService.deleteMember(member);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }
}
