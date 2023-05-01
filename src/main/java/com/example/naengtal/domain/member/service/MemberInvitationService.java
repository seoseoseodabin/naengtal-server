package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.alarm.dto.FcmInvitationDto;
import com.example.naengtal.domain.alarm.entity.Alarm;
import com.example.naengtal.domain.alarm.dao.AlarmRepository;
import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.dao.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.service.FcmService;
import com.example.naengtal.global.common.service.FcmType;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.naengtal.domain.alarm.exception.AlarmErrorCode.ALARM_NOT_FOUND;
import static com.example.naengtal.domain.alarm.exception.AlarmErrorCode.NOT_OWN_ALARM;
import static com.example.naengtal.domain.member.exception.MemberErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberInvitationService {

    private final FcmService fcmService;

    private final MemberRepository memberRepository;

    private final AlarmRepository alarmRepository;

    private final FridgeRepository fridgeRepository;

    private final RedisTemplate<String, String> redisTemplate;

    public void invite(Member inviter, String inviteeId) {
        Member invitee = memberRepository.findById(inviteeId)
                .orElseThrow(() -> new RestApiException(MEMBER_NOT_FOUND));

        if (inviter.getId().equals(inviteeId))
            throw new RestApiException(CANNOT_INVITE_SELF);

        if (inviter.getFridge() == invitee.getFridge())
            throw new RestApiException(ALREADY_SHARING);

        // DB에 알림 저장
        Alarm alarm = Alarm.builder()
                .member(invitee)
                .inviter(inviter)
                .text(inviter.getName() + " 님이 냉장고 초대 요청을 보냈습니다.")
                .build();
        alarmRepository.save(alarm);

        // 푸시 알림 보내기
        List<String> tokenList = redisTemplate.opsForList().range(inviteeId, 0, -1);

        fcmService.sendByTokenList(tokenList, FcmInvitationDto.builder()
                .title("냉장고 공유 초대 요청")
                .body(inviter.getName() + " 님이 냉장고 초대 요청을 보냈습니다.")
                .type(FcmType.INVITATION)
                .build());
    }

    public void accept(Member invitee, int alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RestApiException(ALARM_NOT_FOUND));

        if (!alarm.getMember().equals(invitee))
            throw new RestApiException(NOT_OWN_ALARM);

        // 기존 냉장고 삭제하고 초대한 사람의 냉장고 사용하기
        Fridge deleteFridge = invitee.getFridge();

        invitee.setFridge(alarm.getInviter().getFridge());

        if (deleteFridge.getSharedMembers().size() == 1)
            fridgeRepository.delete(deleteFridge);

        // 해당 알림 삭제
        alarmRepository.delete(alarm);
    }

    public void leaveFridge(Member member) {
        Fridge preFridge = member.getFridge();

        // 냉장고의 마지막 남은 사용자일 때 -> 기존 냉장고 삭제
        if (preFridge.getSharedMembers().size() == 1) {
            fridgeRepository.delete(preFridge);
        }

        Fridge newFridge = new Fridge();
        fridgeRepository.save(newFridge);

        member.setFridge(newFridge);
    }
}
