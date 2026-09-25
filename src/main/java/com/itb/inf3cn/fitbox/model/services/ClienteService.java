package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Cliente;
import com.itb.inf3cn.fitbox.model.repository.ClienteRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.clienteRepository =
                clienteRepository;

        this.passwordEncoder =
                passwordEncoder;
    }


    // ==========================================
    // AUXILIARES DE SENHA
    // ==========================================

    // Identifica se a senha já está em formato BCrypt (hash),
    // pra nunca criptografar uma senha que já é um hash.
    private boolean senhaEstaCriptografada(String senha) {

        return senha != null &&
                (senha.startsWith("$2a$") ||
                        senha.startsWith("$2b$") ||
                        senha.startsWith("$2y$"));
    }

    // Só criptografa se a senha vier em texto puro.
    private String protegerSenha(String senha) {

        if (senha == null || senha.isBlank()) {
            return senha;
        }

        if (senhaEstaCriptografada(senha)) {
            return senha;
        }

        return passwordEncoder.encode(senha);
    }


    // ==========================================
    // LISTAR
    // ==========================================

    public List<Cliente> findAll() {

        return clienteRepository.findAll();
    }


    // ==========================================
    // BUSCAR POR ID
    // ==========================================

    public Cliente findById(Long id) {

        return clienteRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFound(
                                "Cliente não encontrado com id " + id
                        )
                );
    }


    // ==========================================
    // LOGIN
    // ==========================================

    public Cliente login(
            String email,
            String password
    ) {

        Cliente cliente = clienteRepository
                .findAll()
                .stream()
                .filter(
                        c ->
                                c.getEmail() != null &&
                                        c.getEmail().equalsIgnoreCase(email)
                )
                .findFirst()
                .orElse(null);

        if (cliente == null || cliente.getPassword() == null) {
            return null;
        }

        boolean senhaCorreta;

        if (senhaEstaCriptografada(cliente.getPassword())) {

            // Conta já usa senha em hash: compara com BCrypt
            senhaCorreta = passwordEncoder.matches(
                    password,
                    cliente.getPassword()
            );

        } else {

            // Conta antiga, senha ainda em texto puro
            senhaCorreta = cliente.getPassword().equals(password);

            if (senhaCorreta) {

                // Migra a senha pra hash automaticamente no primeiro login
                cliente.setPassword(passwordEncoder.encode(password));

                clienteRepository.save(cliente);
            }
        }

        return senhaCorreta ? cliente : null;
    }


    // ==========================================
    // CADASTRAR
    // ==========================================

    @Transactional
    public Cliente save(Cliente cliente) {

        cliente.setPassword(
                protegerSenha(cliente.getPassword())
        );

        cliente.setCodStatus(true);

        if (
                cliente.getNumeroPontos() == null ||
                        cliente.getNumeroPontos().isBlank()
        ) {

            cliente.setNumeroPontos("0");
        }

        Cliente salvo =
                clienteRepository.save(cliente);

        return clienteRepository
                .findById(salvo.getId())
                .orElse(salvo);
    }


    // ==========================================
    // ATUALIZAR
    // ==========================================

    @Transactional
    public Cliente update(
            Long id,
            Cliente clienteAtualizado
    ) {

        Cliente cliente =
                findById(id);

        cliente.setNome(
                clienteAtualizado.getNome()
        );

        cliente.setEmail(
                clienteAtualizado.getEmail()
        );

        cliente.setPassword(
                protegerSenha(clienteAtualizado.getPassword())
        );

        cliente.setNumeroPontos(
                clienteAtualizado.getNumeroPontos()
        );

        cliente.setLogradouro(
                clienteAtualizado.getLogradouro()
        );

        cliente.setNumero(
                clienteAtualizado.getNumero()
        );

        cliente.setBairro(
                clienteAtualizado.getBairro()
        );

        cliente.setCidade(
                clienteAtualizado.getCidade()
        );

        cliente.setCep(
                clienteAtualizado.getCep()
        );

        cliente.setComplemento(
                clienteAtualizado.getComplemento()
        );

        cliente.setReferencia(
                clienteAtualizado.getReferencia()
        );

        cliente.setLatitude(
                clienteAtualizado.getLatitude()
        );

        cliente.setLongitude(
                clienteAtualizado.getLongitude()
        );

        return clienteRepository.save(cliente);
    }


    // ==========================================
    // EXCLUIR
    // ==========================================

    @Transactional
    public void delete(Long id) {

        Cliente cliente =
                findById(id);

        clienteRepository.delete(cliente);
    }
}