package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Hämta alla admins
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    // Skapa en ny administratör
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = adminService.createAdmin(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    // Radera en administratör
    @DeleteMapping("/{id}")
    public ResponseEntity<Admin> deleteAdmin(@PathVariable Long id) {
        Admin deletedAdmin = adminService.deleteAdmin(id);
        return new ResponseEntity<>(deletedAdmin, HttpStatus.OK);
    }
    

}
