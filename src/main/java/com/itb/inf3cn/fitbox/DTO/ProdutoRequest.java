package com.itb.inf3cn.fitbox.DTO;

import lombok.*;

@Getter
@Setter
public class ProdutoRequest {

    private String nome;
    private String tipo;
    private String descricao;
    private String imagem;
    private double valorCompra;
    private double valorVenda;
    private Long categoriaId;
    private boolean codStatus;
    private int quantidadeEstoque;

    private String proteina;
    private String calorias;
    private String carboidratos;

}