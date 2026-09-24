package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Categoria;
import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaService categoriaService;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            CategoriaService categoriaService) {

        this.produtoRepository = produtoRepository;
        this.categoriaService = categoriaService;
    }

    public Produto findById(Long id) {

        return produtoRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Produto não encontrado com id " + id));
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    @Transactional
    public Produto save(Produto produto) {

        if (produto.getCodStatus() == null) {
            produto.setCodStatus(true);
        }

        if (produto.getCategoria() != null) {

            Categoria categoria =
                    categoriaService.findById(produto.getCategoria().getId());

            produto.setCategoria(categoria);
        }

        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto update(Long id, Produto produtoAtualizado) {

        Produto produto = findById(id);

        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setTipo(produtoAtualizado.getTipo());
        produto.setImagem(produtoAtualizado.getImagem());
        produto.setValorCompra(produtoAtualizado.getValorCompra());
        produto.setValorVenda(produtoAtualizado.getValorVenda());
        produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
        produto.setCodStatus(produtoAtualizado.getCodStatus());
        produto.setProteina(produtoAtualizado.getProteina());
        produto.setCalorias(produtoAtualizado.getCalorias());
        produto.setCarboidratos(produtoAtualizado.getCarboidratos());

        if (produtoAtualizado.getCategoria() != null) {

            Categoria categoria =
                    categoriaService.findById(
                            produtoAtualizado.getCategoria().getId());

            produto.setCategoria(categoria);
        } else {
            produto.setCategoria(null);
        }

        return produtoRepository.save(produto);
    }

    @Transactional
    public void delete(Long id) {

        Produto produto = findById(id);

        produtoRepository.delete(produto);
    }
}