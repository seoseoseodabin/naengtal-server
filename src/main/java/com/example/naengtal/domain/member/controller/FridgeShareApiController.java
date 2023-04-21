package com.example.naengtal.domain.member.controller;

import com.example.naengtal.domain.member.dto.MemberResponseDto;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.domain.member.service.MemberInvitationService;
import com.example.naengtal.domain.member.service.MemberSearchService;
import com.example.naengtal.global.common.annotation.LoggedInUser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FridgeShareApiController {

    private final MemberSearchService memberSearchService;

    private final MemberInvitationService memberInvitationService;

    @GetMapping("search/{name_or_id}")
    public ResponseEntity<List<MemberResponseDto>> search(@Parameter(hidden = true) @LoggedInUser Member inviter,
                                                          @PathVariable("name_or_id") String nameOrId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberSearchService.search(inviter, nameOrId));
    }

    @GetMapping("get/sharedmembers")
    public ResponseEntity<List<MemberResponseDto>> getSharedMembers(@Parameter(hidden = true) @LoggedInUser Member member) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberSearchService.searchSharedMembers(member));
    }

    @GetMapping("invite/{member_id}")
    public ResponseEntity<String> invite(@Parameter(hidden = true) @LoggedInUser Member inviter,
                                         @PathVariable("member_id") String inviteeId) {
        memberInvitationService.invite(inviter, inviteeId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }

    @GetMapping("accept/{alarm_id}")
    public ResponseEntity<String> accept(@Parameter(hidden = true) @LoggedInUser Member invitee,
                                         @PathVariable("alarm_id") int alarmId) {
        memberInvitationService.accept(invitee, alarmId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }

    @PostMapping("leave/fridge")
    public ResponseEntity<String> leaveFridge(@Parameter(hidden = true) @LoggedInUser Member member) {
        memberInvitationService.leaveFridge(member);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }
}
