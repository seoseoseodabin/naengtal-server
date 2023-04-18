package com.example.naengtal.domain.member.controller;

import com.example.naengtal.domain.member.dto.MemberResponseDto;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.domain.member.service.MemberSearchService;
import com.example.naengtal.global.common.annotation.LoggedInUser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FridgeShareController {

    private final MemberSearchService memberSearchService;

    @GetMapping("search/{name_or_id}")
    public ResponseEntity<List<MemberResponseDto>> search(@Parameter(hidden = true) @LoggedInUser Member inviter,
                                                          @PathVariable("name_or_id") String nameOrId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberSearchService.search(inviter, nameOrId));

    }
}
