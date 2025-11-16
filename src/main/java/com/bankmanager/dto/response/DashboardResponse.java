package com.bankmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private int totalComptes;
    private BigDecimal soldeTotal;
    private int transactionsDuJour;
    private List<CompteRecentResponse> comptesRecents;
    private List<TransactionRecenteResponse> transactionsRecentes;
}