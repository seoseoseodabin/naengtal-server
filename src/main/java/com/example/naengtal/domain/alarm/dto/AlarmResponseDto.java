package com.example.naengtal.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmResponseDto {

    private int alarmId;

    private String content;
}
