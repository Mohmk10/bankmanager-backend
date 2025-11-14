package com.bankmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteDetailsResponse {
    private CompteResponse compte;
    private BigDecimal totalDepots;
    private BigDecimal totalRetraits;
    private long nombreTransactions;
}
