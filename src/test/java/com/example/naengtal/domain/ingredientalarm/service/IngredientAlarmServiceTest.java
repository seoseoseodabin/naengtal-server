package com.example.naengtal.domain.ingredientalarm.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredientalarm.dao.IngredientAlarmRepository;
import com.example.naengtal.domain.ingredientalarm.dto.IngredientAlarmResponseDto;
import com.example.naengtal.domain.ingredientalarm.entity.IngredientAlarm;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.error.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.naengtal.domain.ingredientalarm.exception.IngredientAlarmErrorCode.INGREDIENT_ALARM_NOT_FOUND;
import static com.example.naengtal.global.error.CommonErrorCode.FORBIDDEN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IngredientAlarmServiceTest {

    @InjectMocks
    private IngredientAlarmService ingredientAlarmService;

    @Mock
    private IngredientAlarmRepository ingredientAlarmRepository;

    private Member member;

    private IngredientAlarm alarm;

    @BeforeEach
    void before() {
        Fridge fridge = new Fridge(1);
        member = Member.builder()
                .id("test")
                .password("test1234")
                .fridge(fridge)
                .build();

        alarm = IngredientAlarm.builder()
                .id(1)
                .member(member)
                .text("~의 유통기한이 만료되었어요.")
                .build();
    }

    @Test
    @DisplayName("유통기한 알람 조회 성공")
    public void get_alarm_success() {
        given(ingredientAlarmRepository.findByMemberOrderByCreatedAtDesc(any(Member.class))).willReturn(List.of(alarm));

        List<IngredientAlarmResponseDto> alarms = ingredientAlarmService.getAlarm(member);

        assertThat(alarms.get(0).getText()).isEqualTo("~의 유통기한이 만료되었어요.");
    }


    @Test
    @DisplayName("유통기한 알람 삭제 성공")
    void delete_alarm_success() {
        given(ingredientAlarmRepository.findById(any(Integer.class))).willReturn(Optional.of(alarm));

        ingredientAlarmService.deleteAlarm(member, 1);
    }

    @Test
    @DisplayName("본인 알람이 아닐 시 삭제 실패")
    void delete_alarm_fail_when_not_mine() {
        Fridge fridge = new Fridge(1);
        Member member = Member.builder()
                .id("test2")
                .password("test1234")
                .fridge(fridge)
                .build();
        given(ingredientAlarmRepository.findById(any(Integer.class))).willReturn(Optional.of(alarm));

        RestApiException exception = assertThrows(RestApiException.class, () ->
                ingredientAlarmService.deleteAlarm(member, 1));

        assertThat(exception.getErrorCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않는 알림일 시 삭제 실패")
    void delete_alarm_fail_when_not_exist(){
        Fridge fridge = new Fridge(1);
        Member member = Member.builder()
                .id("test1")
                .password("test1234")
                .fridge(fridge)
                .build();
        given(ingredientAlarmRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        RestApiException exception = assertThrows(RestApiException.class, () ->
                ingredientAlarmService.deleteAlarm(member, 1));

        assertThat(exception.getErrorCode()).isEqualTo(INGREDIENT_ALARM_NOT_FOUND);
    }
}