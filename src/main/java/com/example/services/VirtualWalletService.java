package com.example.services;

import com.example.entities.VirtualWallet;
import com.example.repositories.VirtualWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VirtualWalletService {

    @Autowired
    private VirtualWalletRepository virtualWalletRepository;

    public VirtualWallet getWalletByDoctor(UUID doctorId) {
        return virtualWalletRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Virtual wallet not found for doctor"));
    }

    public List<VirtualWallet> getAllWallets() {
        return virtualWalletRepository.findAll();
    }

    public VirtualWallet createWalletForDoctor(UUID doctorId) {
        VirtualWallet wallet = new VirtualWallet();
        // Note: You would need to set the doctor entity here
        // This is a simplified version
        return virtualWalletRepository.save(wallet);
    }

    public VirtualWallet updateWallet(VirtualWallet wallet) {
        return virtualWalletRepository.save(wallet);
    }
}
