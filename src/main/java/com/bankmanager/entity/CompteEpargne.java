package com.bankmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("EPARGNE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompteEpargne extends Compte {

    @Column(nullable = true)
    private LocalDate dateDeblocage;

    @Override
    public boolean peutEffectuerRetrait(BigDecimal montant) {

        if (LocalDate.now().isBefore(dateDeblocage)) {
            return false;
        }

        return getSolde().compareTo(montant) >= 0;
    }
}