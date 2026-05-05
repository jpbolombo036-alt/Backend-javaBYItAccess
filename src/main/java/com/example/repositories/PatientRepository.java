package com.example.repositories;

import com.example.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    List<Patient> findByFullNameContainingIgnoreCase(String fullName);
    
    List<Patient> findByPhoneContaining(String phone);
    
    @Query("SELECT p FROM Patient p WHERE p.fullName LIKE %:search% OR p.phone LIKE %:search%")
    List<Patient> findByFullNameOrPhoneContaining(@Param("search") String search);
}
