package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.exceptions.BadRequest;
import com.itb.inf3cn.fitbox.model.entity.Categoria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.services.CategoriaService;
import com.itb.inf3cn.fitbox.model.services.ProdutoService;
import com.itb.inf3cn.fitbox.DTO.ProdutoRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<Produto> saveProduto(@RequestBody ProdutoRequest produtoRequest) {
        Produto produto = criarProduto(produtoRequest);

        if (produtoRequest.getCategoriaId() != null) {
            try {
                Categoria categoria = categoriaService.findById(produtoRequest.getCategoriaId());
            }catch (Exception e){
                throw new BadRequest("Não foi encontrado a categoria como o id"+ produtoRequest.getCategoriaId());
            }
        }
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/api/v1/produtos").toUriString());
        return ResponseEntity.created(uri).body(produtoService.save(produto));
    }

    private Produto criarProduto (ProdutoRequest produtoRequest) {

        Produto produto = new Produto();

        produto.setNome(produtoRequest.getNome());
        produto.setDescricao(produtoRequest.getDescricao());
        produto.setTipo(produtoRequest.getTipo());
        produto.setValorVenda(produtoRequest.getValorVenda());
        produto.setValorCompra(produtoRequest.getValorCompra());
        produto.setQuantidadeEstoque(produtoRequest.getQuantidadeEstoque());

        //Categoria categoria = new Categoria();
        //categoria.setId(produtoRequest.getCategoriaId());

        //produto.setCategoria(categoria);

        return produto;
    }
    @GetMapping
    public ResponseEntity<List<Produto>> buscarTodos() {

        return ResponseEntity.ok(produtoService.findAll());

    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.findById(id));
    }
}
