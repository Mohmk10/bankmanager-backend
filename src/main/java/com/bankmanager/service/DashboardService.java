package com.bankmanager.service;

import com.bankmanager.dto.response.CompteRecentResponse;
import com.bankmanager.dto.response.DashboardResponse;
import com.bankmanager.dto.response.TransactionRecenteResponse;
import com.bankmanager.entity.Compte;
import com.bankmanager.entity.Transaction;
import com.bankmanager.repository.CompteRepository;
import com.bankmanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData() {
        // Récupérer uniquement les comptes actifs
        List<Compte> comptesActifs = compteRepository.findByIsActive(true);

        BigDecimal soldeTotal = comptesActifs.stream()
                .map(Compte::getSolde)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = debutJour.plusDays(1);
        long transactionsDuJour = transactionRepository.countTodayTransactions(debutJour, finJour);

        // Les 5 derniers comptes créés (actifs uniquement)
        List<CompteRecentResponse> comptesRecents = compteRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(Compte::getIsActive) // Filtrer uniquement les actifs
                .limit(5)
                .map(this::toCompteRecentResponse)
                .collect(Collectors.toList());

        // Les 5 dernières transactions
        List<TransactionRecenteResponse> transactionsRecentes = transactionRepository
                .findAllByOrderByDateTransactionDesc()
                .stream()
                .limit(5)
                .map(this::toTransactionRecenteResponse)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalComptes(comptesActifs.size())
                .soldeTotal(soldeTotal)
                .transactionsDuJour((int) transactionsDuJour)
                .comptesRecents(comptesRecents)
                .transactionsRecentes(transactionsRecentes)
                .build();
    }

    private CompteRecentResponse toCompteRecentResponse(Compte compte) {
        return CompteRecentResponse.builder()
                .id(compte.getId())
                .numeroCompte(compte.getNumeroCompte())
                .solde(compte.getSolde())
                .typeCompte(compte.getTypeCompte().name())
                .clientNomComplet(compte.getClient().getPrenom() + " " + compte.getClient().getNom())
                .build();
    }

    private TransactionRecenteResponse toTransactionRecenteResponse(Transaction transaction) {
        return TransactionRecenteResponse.builder()
                .id(transaction.getId())
                .idTransaction(transaction.getIdTransaction())
                .type(transaction.getType().name())
                .montant(transaction.getMontant())
                .numeroCompte(transaction.getCompte().getNumeroCompte())
                .dateTransaction(transaction.getDateTransaction())
                .build();
    }
}