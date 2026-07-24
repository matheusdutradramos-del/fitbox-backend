package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Categoria;
import com.itb.inf3cn.fitbox.model.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Categoria não encontrada com id " + id));
    }

    @Transactional
    public Categoria save(Categoria categoria) {
        categoria.setCodStatus(true);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria update(Long id, Categoria categoriaAtualizada) {

        Categoria categoria = findById(id);

        categoria.setNome(categoriaAtualizada.getNome());
        categoria.setDescricao(categoriaAtualizada.getDescricao());
        categoria.setCodStatus(categoriaAtualizada.isCodStatus());

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void delete(Long id) {

        Categoria categoria = findById(id);

        categoriaRepository.delete(categoria);
    }
}