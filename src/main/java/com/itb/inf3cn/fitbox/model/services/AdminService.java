package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Admin;
import com.itb.inf3cn.fitbox.model.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Administrador não encontrado com id " + id));
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


    // ==========================================
    // LOGIN
    // ==========================================

    public Admin login(String email, String password) {

        Admin admin = adminRepository.findAll()
                .stream()
                .filter(a ->
                        a.getEmail() != null &&
                                a.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (admin == null || admin.getPassword() == null) {
            return null;
        }

        boolean senhaCorreta;

        if (senhaEstaCriptografada(admin.getPassword())) {

            senhaCorreta = passwordEncoder.matches(
                    password,
                    admin.getPassword()
            );

        } else {

            senhaCorreta = admin.getPassword().equals(password);

            if (senhaCorreta) {

                admin.setPassword(passwordEncoder.encode(password));

                adminRepository.save(admin);
            }
        }

        return senhaCorreta ? admin : null;
    }


    @Transactional
    public Admin save(Admin admin) {

        admin.setPassword(
                protegerSenha(admin.getPassword())
        );

        return adminRepository.save(admin);
    }

    @Transactional
    public Admin update(Long id, Admin adminAtualizado) {

        Admin admin = findById(id);

        admin.setNome(adminAtualizado.getNome());
        admin.setEmail(adminAtualizado.getEmail());
        admin.setPassword(
                protegerSenha(adminAtualizado.getPassword())
        );
        admin.setNivelAcesso(adminAtualizado.getNivelAcesso());

        return adminRepository.save(admin);
    }

    @Transactional
    public void delete(Long id) {

        Admin admin = findById(id);

        adminRepository.delete(admin);
    }
}