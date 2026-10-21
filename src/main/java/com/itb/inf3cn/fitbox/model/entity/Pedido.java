package com.itb.inf3cn.fitbox.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Pedido")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 20, nullable = false)
    private String numeroPedido;

    @Column(nullable = false)
    private LocalDateTime dataHoraPedido;

    @Column(nullable = true)
    private LocalDateTime dataHoraEntrega;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Column(length = 30, nullable = false)
    private String status;

    @Column(length = 40)
    private String pagamento;

    @Column(length = 500)
    private String enderecoEntrega;

    @Column(nullable = false)
    private Integer minutosEstimados = 30;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ItemPedido> itens;

    private boolean codStatus;
}
