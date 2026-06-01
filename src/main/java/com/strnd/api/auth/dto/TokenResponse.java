package com.strnd.api.auth.dto;

import lombok.Getter;

@Getter
public class TokenResponse {

    private final String accessToken;
    private final String tokenType = "Bearer";
    private final String designerName;

    public TokenResponse(String accessToken, String designerName) {
        this.accessToken = accessToken;
        this.designerName = designerName;
    }
}
