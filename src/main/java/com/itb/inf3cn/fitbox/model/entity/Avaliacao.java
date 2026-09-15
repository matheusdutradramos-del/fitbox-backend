package com.itb.inf3cn.fitbox.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Avaliacao")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Integer nota;

    @Column(nullable = true, length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime dataAvaliacao;

    @Column(nullable = false)
    private boolean respondido;

    @Column(nullable = true, length = 500)
    private String respostaAdmin;

    // Relacionamento "Associação"
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id", nullable = false)
    private Cliente cliente;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", referencedColumnName = "id", nullable = false)
    private Produto produto;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id", referencedColumnName = "id", nullable = true)
    private Pedido pedido;

    private boolean codStatus;
}
