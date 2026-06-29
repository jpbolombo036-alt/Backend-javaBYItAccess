package com.example.controllers;

import com.example.entities.Doctor;
import com.example.entities.User;
import com.example.entities.VirtualWallet;
import com.example.enums.UserRole;
import com.example.repositories.DoctorRepository;
import com.example.repositories.UserRepository;
import com.example.repositories.VirtualWalletRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management API", description = "API pour la gestion des utilisateurs (admin, docteurs)")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VirtualWalletRepository virtualWalletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/admin")
    @Operation(summary = "Créer un administrateur", description = "Crée un nouvel utilisateur avec le rôle administrateur")
    @ApiResponse(responseCode = "200", description = "Administrateur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<User> createAdmin(
            @Parameter(description = "Nom de l'administrateur") @RequestParam String name,
            @Parameter(description = "Email de l'administrateur") @RequestParam String email,
            @Parameter(description = "Mot de passe de l'administrateur") @RequestParam String password) {
        
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User admin = new User(name, email, passwordEncoder.encode(password), UserRole.ADMIN);
        User savedAdmin = userRepository.save(admin);
        
        return ResponseEntity.ok(savedAdmin);
    }

    @PostMapping("/doctor")
    @Operation(summary = "Créer un médecin", description = "Crée un nouvel utilisateur médecin avec son portefeuille virtuel")
    @ApiResponse(responseCode = "200", description = "Médecin créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Doctor> createDoctor(
            @Parameter(description = "Nom du médecin") @RequestParam String name,
            @Parameter(description = "Email du médecin") @RequestParam String email,
            @Parameter(description = "Mot de passe du médecin") @RequestParam String password,
            @Parameter(description = "Spécialité du médecin") @RequestParam String specialty,
            @Parameter(description = "Numéro de licence du médecin") @RequestParam String licenseNumber) {
        
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        // Créer l'utilisateur médecin
        User user = new User(name, email, passwordEncoder.encode(password), UserRole.DOCTOR);
        User savedUser = userRepository.save(user);

        // Créer le médecin
        Doctor doctor = new Doctor(savedUser, name, specialty, licenseNumber, "");
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Créer le portefeuille virtuel pour le médecin
        VirtualWallet wallet = new VirtualWallet(savedDoctor);
        virtualWalletRepository.save(wallet);

        return ResponseEntity.ok(savedDoctor);
    }

    @GetMapping("/all")
    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère la liste de tous les utilisateurs")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/doctors")
    @Operation(summary = "Lister tous les médecins", description = "Récupère la liste de tous les médecins")
    @ApiResponse(responseCode = "200", description = "Liste des médecins récupérée avec succès")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID", description = "Récupère les détails d'un utilisateur spécifique")
    @ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctor/{id}")
    @Operation(summary = "Récupérer un médecin par ID", description = "Récupère les détails d'un médecin spécifique")
    @ApiResponse(responseCode = "200", description = "Médecin trouvé")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Doctor> getDoctorById(
            @Parameter(description = "ID du médecin") @PathVariable UUID id) {
        return doctorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'un utilisateur", description = "Active ou désactive un utilisateur")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    public ResponseEntity<User> updateUserStatus(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id,
            @Parameter(description = "Nouveau statut") @RequestParam boolean active) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(active);
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
