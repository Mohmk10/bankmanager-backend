package com.bankmanager.dto.request;

import com.bankmanager.entity.enums.TypeTransaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    @NotNull(message = "ID compte obligatoire")
    private UUID compteId;

    @NotNull(message = "Type de transaction obligatoire")
    private TypeTransaction type;

    @NotNull(message = "Montant obligatoire")
    @Positive(message = "Montant doit être positif")
    private BigDecimal montant;

    private String description;
}