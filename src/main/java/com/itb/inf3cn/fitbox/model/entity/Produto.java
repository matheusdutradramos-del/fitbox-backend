package com.itb.inf3cn.fitbox.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Produto")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(length = 80, nullable = false)
    private String nome;
    @Column(length = 50, nullable = false)
    private String tipo;
    @Column(length = 200, nullable = false)
    private String descricao;
    @Column(nullable = false)
    private BigDecimal valorCompra;
    @Column(nullable = false)
    private BigDecimal valorVenda;

    // Relacionamento "Associação"
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id", nullable = false)
    private Categoria categoria;

    private boolean codStatus;

}
