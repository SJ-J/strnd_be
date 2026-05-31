package com.strnd.api.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class User {

    private Long id;
    private String username;
    private String password;
    private String role;  // ROLE_USER, ROLE_DESIGNER
    private LocalDateTime createdAt;
}
