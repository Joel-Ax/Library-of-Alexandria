package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // Hämta alla administratörer
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Skapa admin
    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }
}
