package com.bankmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private long totalComptes;
    private BigDecimal soldeTotal;
    private long transactionsDuJour;
    private List<CompteResponse> comptesRecents;
    private List<TransactionResponse> transactionsRecentes;
}