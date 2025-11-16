package com.bankmanager.repository;

import com.bankmanager.entity.Transaction;
import com.bankmanager.entity.enums.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByIdTransaction(String idTransaction);
    List<Transaction> findByCompteId(UUID compteId);
    List<Transaction> findByCompteIdOrderByDateTransactionDesc(UUID compteId);

    @Query("SELECT t FROM Transaction t WHERE t.dateTransaction >= :startDate ORDER BY t.dateTransaction DESC")
    List<Transaction> findTransactionsAfterDate(LocalDateTime startDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.dateTransaction >= :startOfDay AND t.dateTransaction < :endOfDay")
    long countTodayTransactions(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT t FROM Transaction t WHERE t.compte.id = :compteId AND t.type = :type")
    List<Transaction> findByCompteIdAndType(UUID compteId, TypeTransaction type);

    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.compte.id = :compteId AND t.type = :type")
    BigDecimal sumMontantByCompteAndType(UUID compteId, TypeTransaction type);

    List<Transaction> findAllByOrderByDateTransactionDesc();

    long countByDateTransactionAfter(LocalDateTime dateDebut);

    List<Transaction> findByDateTransactionAfterOrderByDateTransactionDesc(LocalDateTime dateDebut);
}