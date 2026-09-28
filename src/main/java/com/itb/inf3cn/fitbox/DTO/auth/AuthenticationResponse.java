package com.itb.inf3cn.fitbox.DTO.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Resposta com os tokens gerados no login.
// access_token  -> vai no header Authorization: Bearer <token> das requisições protegidas
// refresh_token -> usado para pedir um novo access_token quando ele expirar
@Getter
@AllArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;
}
