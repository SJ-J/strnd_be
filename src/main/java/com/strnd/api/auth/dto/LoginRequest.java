package com.strnd.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    private String pin;
}
