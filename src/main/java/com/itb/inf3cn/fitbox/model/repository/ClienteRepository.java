package com.itb.inf3cn.fitbox.model.repository;

import com.itb.inf3cn.fitbox.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
