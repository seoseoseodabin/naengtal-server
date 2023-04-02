package com.example.naengtal.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignUpDto {

    @NotBlank(message = "NAME_IS_MANDATORY")
    @Size(min = 1, max = 10)
    String name;

    @NotBlank(message = "ID_IS_MANDATORY")
    @Size(min = 1, max = 15)
    String id;

    @NotBlank(message = "PASSWORD_IS_MANDATORY")
    String password;

    @NotBlank(message = "CONFIRM_PASSWORD_IS_MANDATORY")
    String confirmPassword;
}