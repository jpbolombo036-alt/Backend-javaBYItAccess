package com.example.repositories;

import com.example.entities.VirtualWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VirtualWalletRepository extends JpaRepository<VirtualWallet, UUID> {
    
    Optional<VirtualWallet> findByDoctorId(UUID doctorId);
}
