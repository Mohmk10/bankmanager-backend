package com.bankmanager.repository;

import com.bankmanager.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompteRepository extends JpaRepository<Compte, UUID> {
    Optional<Compte> findByNumeroCompte(String numeroCompte);
    boolean existsByNumeroCompte(String numeroCompte);
    List<Compte> findByClientId(UUID clientId);
    List<Compte> findByIsActiveTrue();
    long countByClientIdAndIsActiveTrue(UUID clientId);

    @Query("SELECT c FROM Compte c WHERE c.client.id = :clientId AND c.isActive = true")
    List<Compte> findActiveComptesByClientId(UUID clientId);

    @Query("SELECT COUNT(c) FROM Compte c WHERE c.isActive = true")
    long countActiveComptes();

    @Query("SELECT SUM(c.solde) FROM Compte c WHERE c.isActive = true")
    java.math.BigDecimal getTotalSolde();

    List<Compte> findByClientIdAndIsActiveTrue(UUID clientId);

    List<Compte> findByIsActive(boolean isActive);

    List<Compte> findAllByOrderByCreatedAtDesc();
}