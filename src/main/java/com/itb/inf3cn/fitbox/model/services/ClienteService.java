package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Cliente;
import com.itb.inf3cn.fitbox.model.repository.ClienteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(
            ClienteRepository clienteRepository
    ) {

        this.clienteRepository =
                clienteRepository;
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

        return clienteRepository
                .findAll()
                .stream()
                .filter(
                        cliente ->
                                cliente.getEmail() != null &&
                                        cliente.getPassword() != null &&
                                        cliente.getEmail()
                                                .equalsIgnoreCase(email) &&
                                        cliente.getPassword()
                                                .equals(password)
                )
                .findFirst()
                .orElse(null);
    }


    // ==========================================
    // CADASTRAR
    // ==========================================

    @Transactional
    public Cliente save(Cliente cliente) {

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
                clienteAtualizado.getPassword()
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


