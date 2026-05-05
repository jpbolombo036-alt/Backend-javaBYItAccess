package com.example.repositories;

import com.example.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    List<Payment> findByDoctorId(UUID doctorId);
    
    List<Payment> findByPaidById(UUID paidById);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.doctor.id = :doctorId ORDER BY p.paymentDate DESC")
    List<Payment> findByDoctorIdOrderByPaymentDateDesc(@Param("doctorId") UUID doctorId);
}
