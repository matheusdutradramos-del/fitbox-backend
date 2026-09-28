package com.itb.inf3cn.fitbox.DTO.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.itb.inf3cn.fitbox.model.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Resposta do login do cliente: mantém exatamente os mesmos campos do Cliente
// que o front já usa (@JsonUnwrapped "espalha" o cliente no JSON) e acrescenta
// os dois tokens no final.
@Getter
@AllArgsConstructor
public class LoginClienteResponse {

    @JsonUnwrapped
    private final Cliente cliente;

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;
}