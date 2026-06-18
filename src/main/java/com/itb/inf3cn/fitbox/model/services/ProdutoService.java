package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.model.entity.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.repository.ProdutoRepository;
import com.itb.inf3cn.fitbox.exceptions.*;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository ProdutoRepository;
    private final CategoriaService categoriaService;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaService categoriaService) {
        this.ProdutoRepository = produtoRepository;
        this.categoriaService = categoriaService;
    }

    public Produto findById(Long id) {

        try {
            return ProdutoRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFound("Produto não encontrado com o id" + id);
        }
    }

    @Transactional
    public Produto save(Produto produto) {
        produto.setCodStatus(true);

        if(produto.getCategoria() != null) {

            Categoria categoria = categoriaService.findById(produto.getCategoria().getId());
            if(categoria == null) {
                throw new BadRequest("Não foi encontrado a categoria com o id " + produto.getCategoria().getId());
            }
        }

        return ProdutoRepository.save(produto);
    }

    public List<Produto> findAll() {
        return ProdutoRepository.findAll();
    }


}