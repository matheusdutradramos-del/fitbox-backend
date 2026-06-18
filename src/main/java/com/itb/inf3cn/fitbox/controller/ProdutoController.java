package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.model.entity.Categoria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.services.CategoriaService;
import com.itb.inf3cn.fitbox.model.services.ProdutoService;
import com.itb.inf3cn.fitbox.dto.produto.ProdutoRequest;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public ProdutoController(ProdutoService produtoService, CategoriaService categoriaService) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Object> saveProduto(@RequestBody ProdutoRequest produtoRequest) {
        Produto produto = criarProduto(produtoRequest);

        Produto produtoSalvo = produtoService.save(produto);

        return ResponseEntity.ok(produtoSalvo);
    }

    private Produto criarProduto (ProdutoRequest produtoRequest) {

        Produto produto = new Produto();

        produto.setNome(produtoRequest.getNome());
        produto.setDescricao(produtoRequest.getDescricao());
        produto.setTipo(produtoRequest.getTipo());
        produto.setValorVenda(produtoRequest.getValorVenda());
        produto.setValorCompra(produtoRequest.getValorCompra());
        produto.setQuantidadeEstoque(produtoRequest.getQuantidadeEstoque());

        Categoria categoria = new Categoria();
        categoria.setId(produtoRequest.getCategoriaId());

        produto.setCategoria(categoria);

        return produto;
    }

}
