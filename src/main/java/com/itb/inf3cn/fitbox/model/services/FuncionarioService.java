package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Funcionario;
import com.itb.inf3cn.fitbox.model.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    public Funcionario findById(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Funcionário não encontrado com id " + id));
    }

    @Transactional
    public Funcionario save(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public Funcionario update(Long id, Funcionario funcionarioAtualizado) {

        Funcionario funcionario = findById(id);

        funcionario.setNome(funcionarioAtualizado.getNome());
        funcionario.setEmail(funcionarioAtualizado.getEmail());
        funcionario.setPassword(funcionarioAtualizado.getPassword());
        funcionario.setCnh(funcionarioAtualizado.getCnh());

        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void delete(Long id) {

        Funcionario funcionario = findById(id);

        funcionarioRepository.delete(funcionario);
    }
}