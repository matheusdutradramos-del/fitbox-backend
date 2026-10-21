package com.itb.inf3cn.fitbox.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 45)
    private String nome;
    @Column(length = 255)
    private String descricao;
    @Column(nullable = false, columnDefinition = "numeric(7,2)")
    private Double valorVenda;

    @Column(columnDefinition = "numeric(7,2)")
    private Double valorCompra;
    @Column(length = 20)
    private String tipo;
    private Integer quantidadeEstoque;
    private Boolean codStatus;

    // A URL fica salva no banco para a imagem continuar aparecendo após atualizar a página.
    @Column(name = "imagem", length = 1000)
    private String imagem;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "produto_categoria",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    @JsonIgnoreProperties({"produtos"})
    private List<Categoria> categorias = new ArrayList<>();

    // Compatibilidade com telas antigas que esperam produto.categoria.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"produtos"})
    private Categoria categoria;
}
