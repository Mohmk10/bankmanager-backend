package com.bankmanager.dto.response;

import com.bankmanager.entity.enums.StatutTransaction;
import com.bankmanager.entity.enums.TypeTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private UUID id;
    private String idTransaction;
    private TypeTransaction type;
    private BigDecimal montant;
    private BigDecimal frais;
    private BigDecimal soldeApres;
    private StatutTransaction statut;
    private String description;
    private UUID compteId;
    private String numeroCompte;
    private LocalDateTime dateTransaction;
}