package com.itb.inf3cn.fitbox.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoRequest {

    private String nome;

    private Integer quantidade;

    private BigDecimal precoVenda;

}