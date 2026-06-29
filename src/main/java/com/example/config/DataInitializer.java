package com.example.config;

import com.example.entities.*;
import com.example.enums.UserRole;
import com.example.enums.PrescriptionStatus;
import com.example.repositories.*;
import com.example.services.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private VirtualWalletRepository virtualWalletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create sample admin user
        User admin = new User("Admin User", "admin@med.com", passwordEncoder.encode("admin123"), UserRole.ADMIN);
        userRepository.save(admin);

        // Create sample doctor user
        User doctorUser = new User("Dr. John Smith", "doctor@med.com", passwordEncoder.encode("doctor123"), UserRole.DOCTOR);
        userRepository.save(doctorUser);

        // Create sample doctor
        Doctor doctor = new Doctor();
        doctor.setUser(doctorUser);
        doctor.setFullName("Dr. John Smith");
        doctor.setSpecialty("Cardiology");
        doctor.setLicenseNo("MD123456");
        doctor.setPhone("555-0123");
        doctorRepository.save(doctor);

        // Create virtual wallet for doctor
        VirtualWallet wallet = new VirtualWallet(doctor);
        virtualWalletRepository.save(wallet);

        // Create sample patients
        Patient patient1 = new Patient("Alice Johnson", LocalDate.of(1980, 5, 15), "555-0124");
        patientRepository.save(patient1);

        Patient patient2 = new Patient("Bob Wilson", LocalDate.of(1975, 8, 22), "555-0125");
        patientRepository.save(patient2);

        // Create sample prescriptions
        Prescription prescription1 = new Prescription();
        prescription1.setDoctor(doctor);
        prescription1.setPatient(patient1);
        prescription1.setProcessedBy(admin);
        prescription1.setTotalAmount(new BigDecimal("1000.00"));
        prescription1.setNotes("Regular checkup and medication");
        prescription1.setStatus(PrescriptionStatus.VALIDATED);
        prescriptionService.createPrescription(prescription1);

        Prescription prescription2 = new Prescription();
        prescription2.setDoctor(doctor);
        prescription2.setPatient(patient2);
        prescription2.setProcessedBy(admin);
        prescription2.setTotalAmount(new BigDecimal("2500.00"));
        prescription2.setNotes("Specialized treatment");
        prescription2.setStatus(PrescriptionStatus.PENDING);
        prescriptionService.createPrescription(prescription2);
    }
}