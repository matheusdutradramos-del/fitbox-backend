package com.itb.inf3cn.fitbox.model.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Column(length = 10, nullable = false)
    private String numeroPedido;
    @Column(nullable = false)
    private LocalDateTime dataHoraPedido;
    @Column(nullable = true)
    private LocalDateTime dataHoraEntrega;
    @Column(columnDefinition = "DECIMAL(5,2)", nullable = false)
    private BigDecimal valorTotal;
    @Column(length = 20, nullable = false)
    private String status;

    // Relacionamento "Associação"
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id", nullable = false)
    private Cliente cliente;

    private boolean codStatus;
}
