package com.example.naengtal.domain.member.service;


import com.example.naengtal.domain.alarm.dao.AlarmRepository;
import com.example.naengtal.domain.alarm.entity.Alarm;
import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.dao.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.service.FcmService;
import com.example.naengtal.global.error.RestApiException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.naengtal.domain.alarm.exception.AlarmErrorCode.ALARM_NOT_FOUND;
import static com.example.naengtal.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MemberInvitationServiceTest {

    @InjectMocks
    private MemberInvitationService memberInvitationService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FridgeRepository fridgeRepository;

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    @Mock
    private FcmService fcmService;

    @Test
    @DisplayName("냉장고 공유 초대 성공")
    void invite_success() {
        // given
        Fridge fridge1 = new Fridge(1);
        Member invitee = Member.builder()
                .name("invitee")
                .id("invitee")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        List<String> tokenList = new ArrayList<>();

        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(invitee));
        given(redisTemplate.opsForList()).willReturn(listOperations);
        given(listOperations.range(any(String.class), anyLong(), anyLong())).willReturn(tokenList);

        // when
        Fridge fridge2 = new Fridge(2);
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge2)
                .build();
        String inviteeId = "invitee";

        memberInvitationService.invite(inviter, inviteeId);

        // then
        then(alarmRepository).should(times(1)).save(any(Alarm.class));
        then(fcmService).should(times(1)).sendByTokenList(any(), any());
    }

    @Test
    @DisplayName("invitee에 해당하는 멤버가 없는 경우 초대 실패")
    void invite_fail_MEMBER_NOT_FOUND() {
        // given
        given(memberRepository.findById(any(String.class))).willThrow(new RestApiException(MEMBER_NOT_FOUND));

        // when
        Fridge fridge = new Fridge(1);
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        String inviteeId = "invitee";

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                memberInvitationService.invite(inviter, inviteeId)
        );
        MatcherAssert.assertThat(exception.getErrorCode().getHttpStatus(), is(HttpStatus.NOT_FOUND));
        MatcherAssert.assertThat(exception.getErrorCode().name(), is("MEMBER_NOT_FOUND"));
    }

    @Test
    @DisplayName("invitee가 본인인 경우 초대 실패")
    void invite_fail_CANNOT_INVITE_SELF() {
        // given
        Fridge fridge = new Fridge(1);
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(inviter));

        // when
        String inviteeId = "inviter";

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                memberInvitationService.invite(inviter, inviteeId)
        );
        MatcherAssert.assertThat(exception.getErrorCode().getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        MatcherAssert.assertThat(exception.getErrorCode().name(), is("CANNOT_INVITE_SELF"));
    }

    @Test
    @DisplayName("이미 냉장고 공유 중인 경우 초대 실패")
    void invite_fail_ALREADY_SHARING() {
        // given
        Fridge fridge = new Fridge(1);
        Member invitee = Member.builder()
                .name("invitee")
                .id("invitee")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(invitee));

        // when
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        String inviteeId = "invitee";

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                memberInvitationService.invite(inviter, inviteeId)
        );
        MatcherAssert.assertThat(exception.getErrorCode().getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        MatcherAssert.assertThat(exception.getErrorCode().name(), is("ALREADY_SHARING"));
    }

    @Test
    @DisplayName("기존 냉장고에 두 명 이상 있을 때 냉장고 초대 수락 성공")
    void accept_success_when_not_alone() {
        // given
        Fridge fridge1 = new Fridge(1);
        Fridge fridge2 = new Fridge(2);
        Member invitee = Member.builder()
                .name("invitee")
                .id("invitee")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        Member member = Member.builder()
                .name("test")
                .id("test")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge2)
                .build();
        Alarm alarm = Alarm.builder()
                .member(invitee)
                .inviter(inviter)
                .text(inviter.getName() + " 님이 냉장고 초대 요청을 보냈습니다.")
                .build();

        given(alarmRepository.findById(anyInt())).willReturn(Optional.of(alarm));

        // when
        int alarmId = 1;
        memberInvitationService.accept(invitee, alarmId);

        // then
        MatcherAssert.assertThat(invitee.getFridge(), is(inviter.getFridge()));
        MatcherAssert.assertThat(member.getFridge(), is(fridge1));
        then(alarmRepository).should(times(1)).delete(any(Alarm.class));
    }

    @Test
    @DisplayName("기존 냉장고에 혼자 있을 때 냉장고 초대 수락 성공")
    void accept_success_when_alone() {
        // given
        Fridge fridge1 = new Fridge(1);
        Fridge fridge2 = new Fridge(2);
        Member invitee = Member.builder()
                .name("invitee")
                .id("invitee")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge2)
                .build();
        Alarm alarm = Alarm.builder()
                .member(invitee)
                .inviter(inviter)
                .text(inviter.getName() + " 님이 냉장고 초대 요청을 보냈습니다.")
                .build();

        given(alarmRepository.findById(anyInt())).willReturn(Optional.of(alarm));

        // when
        int alarmId = 1;
        memberInvitationService.accept(invitee, alarmId);

        // then
        MatcherAssert.assertThat(invitee.getFridge(), is(inviter.getFridge()));
        then(alarmRepository).should(times(1)).delete(any(Alarm.class));
    }

    @Test
    @DisplayName("alarmId에 해당하는 알람을 못 찾아서 수락 실패")
    void accept_fail_ALARM_NOT_FOUND() {
        // given
        given(alarmRepository.findById(anyInt())).willThrow(new RestApiException(ALARM_NOT_FOUND));

        // when
        Fridge fridge = new Fridge(1);
        Member invitee = Member.builder()
                .name("invitee")
                .id("invitee")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        int alarmId = 1;

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                memberInvitationService.accept(invitee, alarmId)
        );
        MatcherAssert.assertThat(exception.getErrorCode().getHttpStatus(), is(HttpStatus.NOT_FOUND));
        MatcherAssert.assertThat(exception.getErrorCode().name(), is("ALARM_NOT_FOUND"));
    }

    @Test
    @DisplayName("invitee에 속한 알람이 아니어서 수락 실패")
    void accept_fail_NOT_OWN_ALARM() {
        // given
        Fridge fridge1 = new Fridge(1);
        Fridge fridge2 = new Fridge(2);
        Member invitee1 = Member.builder()
                .name("invitee1")
                .id("invitee1")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        Member invitee2 = Member.builder()
                .name("invitee2")
                .id("invitee2")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge2)
                .build();
        Alarm alarm = Alarm.builder()
                .member(invitee2)
                .inviter(inviter)
                .text(inviter.getName() + " 님이 냉장고 초대 요청을 보냈습니다.")
                .build();

        given(alarmRepository.findById(anyInt())).willReturn(Optional.of(alarm));

        // when
        int alarmId = 1;

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                memberInvitationService.accept(invitee1, alarmId)
        );
        MatcherAssert.assertThat(exception.getErrorCode().getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        MatcherAssert.assertThat(exception.getErrorCode().name(), is("NOT_OWN_ALARM"));
    }

    @Test
    @DisplayName("본인만 남았을 때 냉장고 나가기")
    void leave_fridge_when_alone() {
        Fridge fridge = new Fridge(1);
        Member member = Member.builder()
                .name("test")
                .id("test")
                .password("encodedPassword")
                .fridge(fridge)
                .build();

        memberInvitationService.leaveFridge(member);

        assertThat(member.getFridge().getId()).isNotEqualTo(1);
    }

    @Test
    @DisplayName("여러명 있을 때 냉장고 나가기")
    void leave_fridge() {
        Fridge fridge = new Fridge(1);
        Member member1 = Member.builder()
                .name("test1")
                .id("test1")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        Member member2 = Member.builder()
                .name("test2")
                .id("test2")
                .password("encodedPassword")
                .fridge(fridge)
                .build();

        memberInvitationService.leaveFridge(member1);

        assertThat(member1.getFridge().getId()).isNotEqualTo(1);
        assertThat(member2.getFridge().getId()).isEqualTo(1);
    }

}