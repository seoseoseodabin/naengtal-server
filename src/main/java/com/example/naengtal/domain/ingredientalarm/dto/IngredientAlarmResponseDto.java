package com.example.naengtal.domain.ingredientalarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class IngredientAlarmResponseDto {

    private int alarmId;

    private String text;
}
