package com.example.repositories;

import com.example.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    
    Optional<Doctor> findByUserId(UUID userId);
    
    Optional<Doctor> findByLicenseNo(String licenseNo);
    
    List<Doctor> findByIsActive(Boolean isActive);
    
    @Query("SELECT d FROM Doctor d WHERE d.isActive = true ORDER BY d.fullName")
    List<Doctor> findActiveDoctorsOrderedByName();
    
    boolean existsByLicenseNo(String licenseNo);
}
