package com.bankmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompteRecentResponse {
    private UUID id;
    private String numeroCompte;
    private BigDecimal solde;
    private String typeCompte;
    private String clientNomComplet;
}