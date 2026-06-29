package com.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PrescriptionRequest(
    @NotNull UUID doctorId,
    @NotNull UUID patientId,
    @NotNull @DecimalMin("0.01") BigDecimal totalAmount,
    String notes
) {}