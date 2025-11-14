package com.bankmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CHEQUE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompteCheque extends Compte {

    @Column(nullable = true)
    private BigDecimal tauxFrais = new BigDecimal("0.008");

    @Override
    public boolean peutEffectuerRetrait(BigDecimal montant) {

        BigDecimal montantAvecFrais = montant.add(montant.multiply(tauxFrais));

        return getSolde().compareTo(montantAvecFrais) >= 0;
    }


    public BigDecimal calculerFrais(BigDecimal montant) {
        return montant.multiply(tauxFrais);
    }
}