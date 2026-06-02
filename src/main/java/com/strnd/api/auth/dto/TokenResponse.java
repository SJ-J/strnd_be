package com.strnd.api.auth.dto;

import lombok.Getter;

@Getter
public class TokenResponse {

    private final String accessToken;
    private final Long designerId;
    private final String designerName;

    public TokenResponse(String accessToken, Long designerId, String designerName) {
        this.accessToken = accessToken;
        this.designerId = designerId;
        this.designerName = designerName;
    }
}