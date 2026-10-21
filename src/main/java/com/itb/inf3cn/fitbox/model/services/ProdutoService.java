package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.DTO.ProdutoRequest;
import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Categoria;
import com.itb.inf3cn.fitbox.model.entity.Produto;
import com.itb.inf3cn.fitbox.model.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaService categoriaService;
    public ProdutoService(ProdutoRepository produtoRepository, CategoriaService categoriaService) {
        this.produtoRepository = produtoRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional(readOnly = true)
    public Produto findById(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new NotFound("Produto não encontrado com id " + id));
    }
    @Transactional(readOnly = true)
    public List<Produto> findAll() { return produtoRepository.findAll(); }

    @Transactional
    public Produto save(ProdutoRequest request) {
        Produto produto = new Produto();
        aplicarRequest(produto, request);
        return produtoRepository.save(produto);
    }
    @Transactional
    public Produto update(Long id, ProdutoRequest request) {
        Produto produto = findById(id);
        aplicarRequest(produto, request);
        return produtoRepository.save(produto);
    }
    private void aplicarRequest(Produto produto, ProdutoRequest request) {
        produto.setNome(request.getNome());
        produto.setTipo(request.getTipo());
        produto.setDescricao(request.getDescricao());
        produto.setImagem(request.getImagem());
        produto.setValorCompra(request.getValorCompra() == null ? 0.0 : request.getValorCompra());
        produto.setValorVenda(request.getValorVenda() == null ? 0.0 : request.getValorVenda());
        produto.setQuantidadeEstoque(request.getQuantidadeEstoque() == null ? 0 : request.getQuantidadeEstoque());
        produto.setCodStatus(request.getCodStatus() == null || request.getCodStatus());

        Set<Long> ids = new LinkedHashSet<>();
        if (request.getCategoriaIds() != null) ids.addAll(request.getCategoriaIds());
        if (ids.isEmpty() && request.getCategoriaId() != null) ids.add(request.getCategoriaId());
        List<Categoria> categorias = ids.stream().map(categoriaService::findById).collect(Collectors.toCollection(ArrayList::new));
        produto.setCategorias(categorias);
        produto.setCategoria(categorias.isEmpty() ? null : categorias.get(0));
    }
    @Transactional
    public void delete(Long id) {
        Produto produto = findById(id);
        produtoRepository.delete(produto);
    }
}
