package com.example.naengtal.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignUpRequestDto {

    @NotBlank(message = "NAME_IS_MANDATORY")
    @Size(min = 1, max = 10)
    private String name;

    @NotBlank(message = "ID_IS_MANDATORY")
    @Size(min = 1, max = 15)
    private String id;

    @NotBlank(message = "PASSWORD_IS_MANDATORY")
    private String password;

    @NotBlank(message = "CONFIRM_PASSWORD_IS_MANDATORY")
    private String confirmPassword;
}
