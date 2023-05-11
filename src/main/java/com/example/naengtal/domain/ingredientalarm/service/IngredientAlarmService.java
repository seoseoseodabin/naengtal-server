package com.example.naengtal.domain.ingredientalarm.service;

import com.example.naengtal.domain.ingredientalarm.dao.IngredientAlarmRepository;
import com.example.naengtal.domain.ingredientalarm.dto.IngredientAlarmResponseDto;
import com.example.naengtal.domain.ingredientalarm.entity.IngredientAlarm;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.naengtal.domain.ingredientalarm.exception.IngredientAlarmErrorCode.INGREDIENT_ALARM_NOT_FOUND;
import static com.example.naengtal.global.error.CommonErrorCode.FORBIDDEN;

@Service
@Transactional
@RequiredArgsConstructor
public class IngredientAlarmService {

    private final IngredientAlarmRepository ingredientAlarmRepository;

    public List<IngredientAlarmResponseDto> getAlarm(Member member) {
        List<IngredientAlarm> alarms = ingredientAlarmRepository.findByMemberOrderByCreatedAtDesc(member);

        return alarms.stream()
                .map(alarm -> IngredientAlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .content(alarm.getText())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteAlarm(Member member, int id) {
        IngredientAlarm alarm = ingredientAlarmRepository.findById(id).orElseThrow(() -> new RestApiException(INGREDIENT_ALARM_NOT_FOUND));

        // 본인 알람이 아닐 때
        if (!member.equals(alarm.getMember())) {
            throw new RestApiException(FORBIDDEN);
        }

        ingredientAlarmRepository.delete(alarm);
    }
}
