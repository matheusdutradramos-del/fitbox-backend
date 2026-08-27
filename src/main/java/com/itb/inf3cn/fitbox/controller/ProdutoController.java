package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.DTO.ProdutoRequest;
import com.itb.inf3cn.fitbox.exceptions.BadRequest;
import com.itb.inf3cn.fitbox.model.entity.Categoria;
import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.services.CategoriaService;
import com.itb.inf3cn.fitbox.model.services.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public ProdutoController(
            ProdutoService produtoService,
            CategoriaService categoriaService) {

        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    // ==========================================
    // CADASTRAR PRODUTO
    // ==========================================

    @PostMapping
    public ResponseEntity<Produto> saveProduto(
            @RequestBody ProdutoRequest produtoRequest) {

        Produto produto = criarProduto(produtoRequest);

        // ==========================================
        // CATEGORIA
        // ==========================================

        if (produtoRequest.getCategoriaId() != null) {

            try {

                Categoria categoria =
                        categoriaService.findById(
                                produtoRequest.getCategoriaId()
                        );

                produto.setCategoria(categoria);

            } catch (Exception e) {

                throw new BadRequest(
                        "Não foi encontrada a categoria com o id "
                                + produtoRequest.getCategoriaId()
                );
            }
        }

        Produto salvo = produtoService.save(produto);

        return ResponseEntity
                .created(URI.create("/api/v1/produtos/" + salvo.getId()))
                .body(salvo);
    }

    // ==========================================
    // CRIAR PRODUTO
    // ==========================================

    private Produto criarProduto(
            ProdutoRequest produtoRequest) {

        Produto produto = new Produto();

        produto.setNome(produtoRequest.getNome());
        produto.setDescricao(produtoRequest.getDescricao());
        produto.setTipo(produtoRequest.getTipo());
        produto.setImagem(produtoRequest.getImagem());

        produto.setValorVenda(
                produtoRequest.getValorVenda()
        );

        produto.setValorCompra(
                produtoRequest.getValorCompra()
        );

        produto.setQuantidadeEstoque(
                produtoRequest.getQuantidadeEstoque()
        );

        produto.setCodStatus(
                produtoRequest.isCodStatus()
        );

        return produto;
    }

    // ==========================================
    // LISTAR TODOS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Produto>> buscarTodos() {

        return ResponseEntity.ok(
                produtoService.findAll()
        );
    }

    // ==========================================
    // BUSCAR POR ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                produtoService.findById(id)
        );
    }
}