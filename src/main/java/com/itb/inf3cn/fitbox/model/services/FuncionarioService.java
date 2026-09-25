package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Funcionario;
import com.itb.inf3cn.fitbox.model.repository.FuncionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    public Funcionario findById(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Funcionário não encontrado com id " + id));
    }


    // ==========================================
    // AUXILIARES DE SENHA
    // ==========================================

    private boolean senhaEstaCriptografada(String senha) {

        return senha != null &&
                (senha.startsWith("$2a$") ||
                        senha.startsWith("$2b$") ||
                        senha.startsWith("$2y$"));
    }

    private String protegerSenha(String senha) {

        if (senha == null || senha.isBlank()) {
            return senha;
        }

        if (senhaEstaCriptografada(senha)) {
            return senha;
        }

        return passwordEncoder.encode(senha);
    }


    @Transactional
    public Funcionario save(Funcionario funcionario) {

        funcionario.setPassword(
                protegerSenha(funcionario.getPassword())
        );

        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public Funcionario update(Long id, Funcionario funcionarioAtualizado) {

        Funcionario funcionario = findById(id);

        funcionario.setNome(funcionarioAtualizado.getNome());
        funcionario.setEmail(funcionarioAtualizado.getEmail());
        funcionario.setPassword(
                protegerSenha(funcionarioAtualizado.getPassword())
        );
        funcionario.setCnh(funcionarioAtualizado.getCnh());

        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void delete(Long id) {

        Funcionario funcionario = findById(id);

        funcionarioRepository.delete(funcionario);
    }
}