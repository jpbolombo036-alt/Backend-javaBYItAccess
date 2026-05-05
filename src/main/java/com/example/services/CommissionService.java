package com.example.services;

import com.example.entities.Commission;
import com.example.enums.CommissionStatus;
import com.example.repositories.CommissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CommissionService {

    @Autowired
    private CommissionRepository commissionRepository;

    public List<Commission> getCommissionsByDoctor(UUID doctorId) {
        return commissionRepository.findByDoctorId(doctorId);
    }

    public List<Commission> getPendingCommissionsByDoctor(UUID doctorId) {
        return commissionRepository.findPendingCommissionsByDoctor(doctorId);
    }

    public List<Commission> getAllPendingCommissions() {
        return commissionRepository.findByStatus(CommissionStatus.PENDING);
    }

    public BigDecimal getPendingCommissionTotalByDoctor(UUID doctorId) {
        BigDecimal total = commissionRepository.sumPendingCommissionsByDoctor(doctorId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Object[]> getPendingCommissionTotalsByAllDoctors() {
        return commissionRepository.sumPendingCommissionsByAllDoctors();
    }

    public Commission getCommissionById(UUID commissionId) {
        return commissionRepository.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Commission not found"));
    }

    @Transactional
    public Commission updateCommissionStatus(UUID commissionId, CommissionStatus status) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Commission not found"));
        
        commission.setStatus(status);
        return commissionRepository.save(commission);
    }
}
