package com.itb.inf3cn.fitbox.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Categoria")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(length = 100, nullable = false)
    private String nome;
    @Column(length = 150, nullable = false)
    private String descricao;

    // Relacionamento "Associação"
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id", nullable = false)
    private Produto produto;

    private boolean codStatus;

}
