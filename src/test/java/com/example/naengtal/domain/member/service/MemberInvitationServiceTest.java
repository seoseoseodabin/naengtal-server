package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.alarm.repository.AlarmRepository;
import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.repository.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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