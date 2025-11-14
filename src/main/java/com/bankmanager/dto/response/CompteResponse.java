package com.bankmanager.dto.response;

import com.bankmanager.entity.enums.TypeCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteResponse {
    private UUID id;
    private String numeroCompte;
    private BigDecimal solde;
    private TypeCompte typeCompte;
    private Boolean isActive;
    private UUID clientId;
    private String clientNomComplet;
    private LocalDate dateDeblocage;
    private BigDecimal tauxFrais;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}