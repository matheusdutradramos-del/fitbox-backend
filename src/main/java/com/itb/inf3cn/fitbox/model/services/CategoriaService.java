package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.model.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itb.inf3cn.fitbox.model.entity.Categoria;
import com.itb.inf3cn.fitbox.exceptions.*;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria findById(Long id) {

        try {
            return categoriaRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFound("Categoria não encontrado com o id" + id);
        }
    }

    @Transactional
    public Categoria save(Categoria categoria) {

        categoria.setCodStatus(true);
        return categoriaRepository.save(categoria);

    }
}