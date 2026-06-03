package com.strnd.api.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "고객 명을 입력해 주세요.")
    private String customerName; // 고객명(필수)
    @NotBlank(message = "연락처를 입력해 주세요.")
    private String phone;        // 연락처(필수)
    @NotBlank(message = "성별을 입력해 주세요.")
    private String gender;       // 성별(MALE/FEMALE/OTHER, 필수)
    private String memo;         // 메모(선택)
}