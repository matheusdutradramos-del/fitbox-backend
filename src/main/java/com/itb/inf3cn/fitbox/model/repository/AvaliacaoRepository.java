package com.itb.inf3cn.fitbox.model.repository;

import com.itb.inf3cn.fitbox.model.entity.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Page<Avaliacao> findAllByOrderByDataAvaliacaoDesc(Pageable pageable);

    Page<Avaliacao> findByCliente_IdOrderByDataAvaliacaoDesc(
            Long clienteId,
            Pageable pageable
    );
}
