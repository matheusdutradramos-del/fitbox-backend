package com.itb.inf3cn.fitbox.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ProdutoRequest {
    private String nome;
    private String tipo;
    private String descricao;
    private String imagem;
    private Double valorCompra;
    private Double valorVenda;
    private Long categoriaId; // compatibilidade com a tela antiga
    private List<Long> categoriaIds = new ArrayList<>();
    private Boolean codStatus = true;
    private Integer quantidadeEstoque;
    private String proteina;
    private String calorias;
    private String carboidratos;
}
