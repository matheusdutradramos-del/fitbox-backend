package com.itb.inf3cn.fitbox.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    private Long id;

    private String numeroPedido;

    private String usuario;

    private LocalDateTime data;

    private BigDecimal total;

    private String status;

    private String pagamento;

    private List<ItemPedidoRequest> itens;

}