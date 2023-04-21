package com.example.naengtal.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberInfo {

    private String id;

    private String name;

    private int fridgeId;
}
