package com.itb.inf3cn.fitbox.model.services;

import com.itb.inf3cn.fitbox.exceptions.NotFound;
import com.itb.inf3cn.fitbox.model.entity.Admin;
import com.itb.inf3cn.fitbox.model.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() ->
                        new NotFound("Administrador não encontrado com id " + id));
    }

    @Transactional
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    @Transactional
    public Admin update(Long id, Admin adminAtualizado) {

        Admin admin = findById(id);

        admin.setNome(adminAtualizado.getNome());
        admin.setEmail(adminAtualizado.getEmail());
        admin.setPassword(adminAtualizado.getPassword());
        admin.setNivelAcesso(adminAtualizado.getNivelAcesso());

        return adminRepository.save(admin);
    }

    @Transactional
    public void delete(Long id) {

        Admin admin = findById(id);

        adminRepository.delete(admin);
    }
}