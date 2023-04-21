package com.example.naengtal.domain.member.controller;

import com.example.naengtal.domain.member.dto.MemberInfo;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.domain.member.service.AccountService;
import com.example.naengtal.global.common.annotation.LoggedInUser;
import io.swagger.v3.oas.annotations.Parameter;
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
    public ResponseEntity<MemberInfo> signUp(@Validated @RequestBody SignUpRequestDto signUpRequestDto) {
        MemberInfo dto = accountService.saveMember(signUpRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> withdrawal(@Parameter(hidden = true) @LoggedInUser Member member) {
        accountService.deleteMember(member);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }

    @PostMapping("edit")
    public ResponseEntity<String> editName(@Parameter(hidden = true) @LoggedInUser Member member,
                                           @RequestParam(value = "name") String name) {
        accountService.editName(member, name);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }

    @GetMapping("info")
    public ResponseEntity<MemberInfo> getInfo(@Parameter(hidden = true) @LoggedInUser Member member) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getInfo(member));
    }
}
