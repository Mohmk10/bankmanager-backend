package com.bankmanager.service;

import com.bankmanager.dto.request.CreateTransactionRequest;
import com.bankmanager.dto.response.TransactionResponse;
import com.bankmanager.entity.*;
import com.bankmanager.entity.enums.StatutTransaction;
import com.bankmanager.entity.enums.TypeTransaction;
import com.bankmanager.exception.BusinessException;
import com.bankmanager.exception.ResourceNotFoundException;
import com.bankmanager.repository.CompteRepository;
import com.bankmanager.repository.TransactionRepository;
import com.bankmanager.util.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Compte compte = compteRepository.findById(request.getCompteId())
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        if (!compte.getIsActive()) {
            throw new BusinessException("Ce compte est inactif. Impossible d'effectuer des transactions.");
        }

        if (!compte.getClient().getIsActive()) {
            throw new BusinessException("Le client propriétaire de ce compte est inactif.");
        }

        if (request.getType() == TypeTransaction.RETRAIT) {
            return processRetrait(compte, request);
        } else {
            return processDepot(compte, request);
        }
    }

    private TransactionResponse processDepot(Compte compte, CreateTransactionRequest request) {
        BigDecimal nouveauSolde = compte.getSolde().add(request.getMontant());
        compte.setSolde(nouveauSolde);

        Transaction transaction = createTransactionEntity(
                compte,
                request.getType(),
                request.getMontant(),
                BigDecimal.ZERO,
                nouveauSolde,
                request.getDescription()
        );

        compte = compteRepository.save(compte);
        transaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    private TransactionResponse processRetrait(Compte compte, CreateTransactionRequest request) {
        BigDecimal frais = BigDecimal.ZERO;
        BigDecimal montantTotal = request.getMontant();

        if (compte instanceof CompteCheque) {
            CompteCheque compteCheque = (CompteCheque) compte;
            frais = compteCheque.calculerFrais(request.getMontant());
            montantTotal = request.getMontant().add(frais);
        }

        if (compte instanceof CompteEpargne) {
            CompteEpargne compteEpargne = (CompteEpargne) compte;
            if (LocalDate.now().isBefore(compteEpargne.getDateDeblocage())) {
                throw new BusinessException("Compte épargne bloqué jusqu'au " + compteEpargne.getDateDeblocage());
            }
        }

        if (!compte.peutEffectuerRetrait(request.getMontant())) {
            throw new BusinessException("Solde insuffisant pour effectuer ce retrait");
        }

        BigDecimal nouveauSolde = compte.getSolde().subtract(montantTotal);
        compte.setSolde(nouveauSolde);

        Transaction transaction = createTransactionEntity(
                compte,
                request.getType(),
                request.getMontant(),
                frais,
                nouveauSolde,
                request.getDescription()
        );

        compte = compteRepository.save(compte);
        transaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    private Transaction createTransactionEntity(
            Compte compte,
            TypeTransaction type,
            BigDecimal montant,
            BigDecimal frais,
            BigDecimal soldeApres,
            String description) {

        String idTransaction;
        do {
            idTransaction = TransactionIdGenerator.generate();
        } while (transactionRepository.findByIdTransaction(idTransaction).isPresent());

        Transaction transaction = new Transaction();
        transaction.setIdTransaction(idTransaction);
        transaction.setType(type);
        transaction.setMontant(montant);
        transaction.setFrais(frais);
        transaction.setSoldeApres(soldeApres);
        transaction.setStatut(StatutTransaction.COMPLETEE);
        transaction.setDescription(description);
        transaction.setCompte(compte);

        return transaction;
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction non trouvée"));
        return mapToTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByIdTransaction(String idTransaction) {
        Transaction transaction = transactionRepository.findByIdTransaction(idTransaction)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction non trouvée"));
        return mapToTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByCompte(UUID compteId) {
        return transactionRepository.findByCompteIdOrderByDateTransactionDesc(compteId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentTransactions(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return transactionRepository.findTransactionsAfterDate(startDate).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .idTransaction(transaction.getIdTransaction())
                .type(transaction.getType())
                .montant(transaction.getMontant())
                .frais(transaction.getFrais())
                .soldeApres(transaction.getSoldeApres())
                .statut(transaction.getStatut())
                .description(transaction.getDescription())
                .compteId(transaction.getCompte().getId())
                .numeroCompte(transaction.getCompte().getNumeroCompte())
                .dateTransaction(transaction.getDateTransaction())
                .build();
    }
}