package com.strnd.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(min = 1, max = 50, message = "이름은 1~50자 이내로 입력해 주세요.")
    private String name;

    @NotBlank(message = "연락처를 입력해 주세요.")
    @Pattern(regexp = "^\\d{10,11}$", message = "연락처는 10~11자리 숫자로 입력해 주세요.")
    private String phone;

    @NotBlank(message = "PIN 코드를 입력해 주세요.")
    @Pattern(regexp = "^\\d{4}$", message = "PIN 코드는 4자리 숫자로 입력해 주세요.")
    private String pinCode;

    @NotBlank(message = "PIN 확인을 입력해 주세요.")
    private String pinConfirm;
}