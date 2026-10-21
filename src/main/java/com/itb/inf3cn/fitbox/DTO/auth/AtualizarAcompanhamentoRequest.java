package com.itb.inf3cn.fitbox.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarAcompanhamentoRequest {
    private String status;
    private Integer minutosEstimados;
}
