package com.example.naengtal.domain.alarm.dto;

import com.example.naengtal.global.common.service.FcmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FcmNotificationDto {

    private String title;
    private String body;
    private FcmType type;
}
