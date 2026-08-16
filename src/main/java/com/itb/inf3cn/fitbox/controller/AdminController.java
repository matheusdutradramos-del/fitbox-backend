package com.itb.inf3cn.fitbox.controller;

import com.itb.inf3cn.fitbox.model.entity.Admin;
import com.itb.inf3cn.fitbox.model.services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admins")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // =========================
    // LISTAR ADMINS
    // =========================
    @GetMapping
    public ResponseEntity<List<Admin>> listar() {
        return ResponseEntity.ok(adminService.findAll());
    }

    // =========================
    // BUSCAR ADMIN POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Admin> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findById(id));
    }

    // =========================
    // LOGIN DO ADMIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> login) {

        String email = login.get("email");
        String password = login.get("password");

        if (email == null || password == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message", "Email e senha são obrigatórios."
                    ));
        }

        Admin admin = adminService.findAll()
                .stream()
                .filter(a ->
                        email.equalsIgnoreCase(a.getEmail())
                                && password.equals(a.getPassword())
                )
                .findFirst()
                .orElse(null);

        if (admin == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Email ou senha inválidos."
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "id", admin.getId(),
                        "nome", admin.getNome(),
                        "email", admin.getEmail(),
                        "tipoUsuario", "ADMIN",
                        "nivel", "admin"
                )
        );
    }

    // =========================
    // CADASTRAR ADMIN
    // =========================
    @PostMapping
    public ResponseEntity<Admin> salvar(@RequestBody Admin admin) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.save(admin));
    }

    // =========================
    // ATUALIZAR ADMIN
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<Admin> atualizar(
            @PathVariable Long id,
            @RequestBody Admin admin) {

        return ResponseEntity.ok(
                adminService.update(id, admin)
        );
    }

    // =========================
    // EXCLUIR ADMIN
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        adminService.delete(id);

        return ResponseEntity.noContent().build();
    }
}