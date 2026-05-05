package com.example.repositories;

import com.example.entities.Prescription;
import com.example.enums.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    
    List<Prescription> findByDoctorId(UUID doctorId);
    
    List<Prescription> findByPatientId(UUID patientId);
    
    List<Prescription> findByStatus(PrescriptionStatus status);
    
    List<Prescription> findByProcessedById(UUID processedById);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor.id = :doctorId AND p.status = :status")
    List<Prescription> findByDoctorAndStatus(@Param("doctorId") UUID doctorId, @Param("status") PrescriptionStatus status);
    
    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :startDate AND :endDate")
    List<Prescription> findByPrescriptionDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.fullName LIKE %:search% OR p.doctor.fullName LIKE %:search%")
    List<Prescription> findByPatientOrDoctorNameContaining(@Param("search") String search);
}
