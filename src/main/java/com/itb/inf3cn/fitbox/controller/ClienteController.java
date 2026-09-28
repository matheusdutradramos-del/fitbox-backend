package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.DTO.auth.AuthenticationResponse;
import com.itb.inf3cn.fitbox.DTO.auth.LoginClienteResponse;
import com.itb.inf3cn.fitbox.DTO.auth.AuthenticationService;
import com.itb.inf3cn.fitbox.model.entity.Cliente;
import com.itb.inf3cn.fitbox.model.services.ClienteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;
    private final AuthenticationService authenticationService;

    public ClienteController(
            ClienteService clienteService,
            AuthenticationService authenticationService
    ) {
        this.clienteService = clienteService;
        this.authenticationService = authenticationService;
    }


    // ==========================================
    // LISTAR CLIENTES
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {

        return ResponseEntity.ok(
                clienteService.findAll()
        );
    }


    // ==========================================
    // LOGIN DO CLIENTE
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> dados
    ) {

        String email = dados.get("email");
        String password = dados.get("password");

        if (email == null || password == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Email e senha são obrigatórios."
                            )
                    );
        }

        Cliente cliente =
                clienteService.login(
                        email,
                        password
                );

        if (cliente == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Email ou senha inválidos."
                            )
                    );
        }

        // Login válido: gera os tokens (access + refresh) e devolve junto
        // com os dados do cliente, no mesmo formato que o front já usa.
        AuthenticationResponse tokens =
                authenticationService.gerarTokens(cliente);

        return ResponseEntity.ok(
                new LoginClienteResponse(
                        cliente,
                        tokens.getAccessToken(),
                        tokens.getRefreshToken()
                )
        );
    }


    // ==========================================
    // BUSCAR CLIENTE
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                clienteService.findById(id)
        );
    }


    // ==========================================
    // CADASTRAR CLIENTE
    // ==========================================

    @PostMapping
    public ResponseEntity<Cliente> salvar(
            @RequestBody Cliente cliente
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        clienteService.save(cliente)
                );
    }


    // ==========================================
    // ATUALIZAR CLIENTE
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(
            @PathVariable Long id,
            @RequestBody Cliente cliente
    ) {

        return ResponseEntity.ok(
                clienteService.update(
                        id,
                        cliente
                )
        );
    }


    // ==========================================
    // EXCLUIR CLIENTE
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {

        clienteService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}