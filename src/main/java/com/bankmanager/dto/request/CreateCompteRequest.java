package com.bankmanager.dto.request;

import com.bankmanager.entity.enums.TypeCompte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompteRequest {

    @NotNull(message = "ID client obligatoire")
    private UUID clientId;

    @NotNull(message = "Type de compte obligatoire")
    private TypeCompte typeCompte;

    @NotNull(message = "Solde initial obligatoire")
    @Positive(message = "Solde initial doit être positif")
    private BigDecimal soldeInitial;

    private LocalDate dateDeblocage;
}