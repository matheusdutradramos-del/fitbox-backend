package com.itb.inf3cn.fitbox.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Produto")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 45)
    private String nome;

    @Column(nullable = true, length = 255)
    private String descricao;

    @Column(nullable = false, columnDefinition = "DECIMAL(5,2)")
    private Double valorVenda;

    @Column(nullable = true, columnDefinition = "DECIMAL(5,2)")
    private Double valorCompra;

    @Column(nullable = true, length = 20)
    private String tipo;

    @JsonIgnore
    private Integer quantidadeEstoque;

    private Boolean codStatus;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "categoria_id",
            referencedColumnName = "id",
            nullable = true
    )
    private Categoria categoria;
}