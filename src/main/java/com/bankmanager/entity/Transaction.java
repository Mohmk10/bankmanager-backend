package com.bankmanager.entity;

import com.bankmanager.entity.enums.StatutTransaction;
import com.bankmanager.entity.enums.TypeTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String idTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(precision = 19, scale = 2)
    private BigDecimal frais = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal soldeApres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransaction statut = StatutTransaction.COMPLETEE;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateTransaction;
}