package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.alarm.entity.Alarm;
import com.example.naengtal.domain.alarm.entity.AlarmType;
import com.example.naengtal.domain.alarm.repository.AlarmRepository;
import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.repository.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.naengtal.domain.alarm.exception.AlarmErrorCode.ALARM_NOT_FOUND;
import static com.example.naengtal.domain.alarm.exception.AlarmErrorCode.NOT_OWN_ALARM;
import static com.example.naengtal.domain.member.exception.MemberErrorCode.CANNOT_INVITE_SELF;
import static com.example.naengtal.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberInvitationService {

    private final MemberRepository memberRepository;

    private final AlarmRepository alarmRepository;

    private final FridgeRepository fridgeRepository;

    public void invite(Member inviter, String inviteeId) {
        Member invitee = memberRepository.findById(inviteeId)
                .orElseThrow(() -> new RestApiException(MEMBER_NOT_FOUND));

        if (inviter.getId().equals(inviteeId))
            throw new RestApiException(CANNOT_INVITE_SELF);

        Alarm alarm = Alarm.builder()
                .member(invitee)
                .inviter(inviter)
                .text(inviter.getName() + " 님이 냉장고 초대 요청을 보냈습니다.")
                .type(AlarmType.INVITATION)
                .build();

        alarmRepository.save(alarm);
    }

    public void accept(Member invitee, int alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RestApiException(ALARM_NOT_FOUND));

        if (!alarm.getMember().equals(invitee))
            throw new RestApiException(NOT_OWN_ALARM);

        Fridge deleteFridge = invitee.getFridge();

        invitee.setFridge(alarm.getInviter().getFridge());

        if (deleteFridge.getSharedMembers().size() == 1)
            fridgeRepository.delete(deleteFridge);

        alarmRepository.delete(alarm);
    }
}
