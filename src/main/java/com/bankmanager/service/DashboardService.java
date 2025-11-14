package com.bankmanager.service;

import com.bankmanager.dto.response.CompteResponse;
import com.bankmanager.dto.response.DashboardResponse;
import com.bankmanager.dto.response.TransactionResponse;
import com.bankmanager.repository.CompteRepository;
import com.bankmanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final CompteService compteService;
    private final TransactionService transactionService;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData() {
        long totalComptes = compteRepository.countActiveComptes();

        BigDecimal soldeTotal = compteRepository.getTotalSolde();
        if (soldeTotal == null) {
            soldeTotal = BigDecimal.ZERO;
        }

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        long transactionsDuJour = transactionRepository.countTodayTransactions(startOfDay, endOfDay);

        List<CompteResponse> comptesRecents = compteRepository
                .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(compte -> compteService.getCompteById(compte.getId()))
                .collect(Collectors.toList());

        List<TransactionResponse> transactionsRecentes = transactionRepository
                .findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateTransaction")))
                .stream()
                .map(transaction -> transactionService.getTransactionById(transaction.getId()))
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalComptes(totalComptes)
                .soldeTotal(soldeTotal)
                .transactionsDuJour(transactionsDuJour)
                .comptesRecents(comptesRecents)
                .transactionsRecentes(transactionsRecentes)
                .build();
    }
}