package com.itb.inf3cn.fitbox.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoRequest {

    private Integer nota;

    private String comentario;

    private Long clienteId;

    private Long produtoId;

    private Long pedidoId;

}
