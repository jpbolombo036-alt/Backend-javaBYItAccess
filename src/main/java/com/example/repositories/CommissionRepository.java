package com.example.repositories;

import com.example.entities.Commission;
import com.example.enums.CommissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, UUID> {
    
    List<Commission> findByDoctorId(UUID doctorId);
    
    List<Commission> findByStatus(CommissionStatus status);
    
    List<Commission> findByDoctorIdAndStatus(UUID doctorId, CommissionStatus status);
    
    @Query("SELECT c FROM Commission c WHERE c.doctor.id = :doctorId AND c.status = 'PENDING'")
    List<Commission> findPendingCommissionsByDoctor(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT SUM(c.commissionAmount) FROM Commission c WHERE c.doctor.id = :doctorId AND c.status = 'PENDING'")
    BigDecimal sumPendingCommissionsByDoctor(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT c.doctor.id, SUM(c.commissionAmount) FROM Commission c WHERE c.status = 'PENDING' GROUP BY c.doctor.id")
    List<Object[]> sumPendingCommissionsByAllDoctors();
}
