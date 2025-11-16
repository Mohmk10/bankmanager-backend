package com.bankmanager.service;

import com.bankmanager.dto.request.CreateCompteRequest;
import com.bankmanager.dto.response.CompteDetailsResponse;
import com.bankmanager.dto.response.CompteResponse;
import com.bankmanager.entity.*;
import com.bankmanager.entity.enums.TypeCompte;
import com.bankmanager.entity.enums.TypeTransaction;
import com.bankmanager.exception.BusinessException;
import com.bankmanager.exception.ResourceNotFoundException;
import com.bankmanager.repository.ClientRepository;
import com.bankmanager.repository.CompteRepository;
import com.bankmanager.repository.TransactionRepository;
import com.bankmanager.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompteService {

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public CompteResponse createCompte(CreateCompteRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        // Vérifier que le client est actif
        if (!client.getIsActive()) {
            throw new BusinessException("Impossible de créer un compte pour un client inactif");
        }

        // Vérifier que le solde initial est positif
        if (request.getSoldeInitial().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Le solde initial doit être supérieur à 0");
        }

        Compte compte;
        String numeroCompte = AccountNumberGenerator.generate();

        // Créer le bon type de compte
        if (request.getTypeCompte() == TypeCompte.EPARGNE) {
            if (request.getDateDeblocage() == null) {
                throw new BusinessException("La date de déblocage est obligatoire pour un compte épargne");
            }
            if (request.getDateDeblocage().isBefore(LocalDate.now())) {
                throw new BusinessException("La date de déblocage doit être dans le futur");
            }

            CompteEpargne compteEpargne = new CompteEpargne();
            compteEpargne.setNumeroCompte(numeroCompte);
            compteEpargne.setSolde(request.getSoldeInitial());
            compteEpargne.setIsActive(true);
            compteEpargne.setClient(client);
            compteEpargne.setDateDeblocage(request.getDateDeblocage());
            compte = compteEpargne;

        } else {
            CompteCheque compteCheque = new CompteCheque();
            compteCheque.setNumeroCompte(numeroCompte);
            compteCheque.setSolde(request.getSoldeInitial());
            compteCheque.setIsActive(true);
            compteCheque.setClient(client);
            compteCheque.setTauxFrais(BigDecimal.valueOf(0.008));
            compte = compteCheque;
        }

        compte = compteRepository.save(compte);
        return mapToCompteResponse(compte);
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> getAllComptes(Boolean active) {
        List<Compte> comptes;
        if (active != null && !active) {
            comptes = compteRepository.findByIsActive(false);
        } else {
            comptes = compteRepository.findByIsActive(true);
        }

        return comptes.stream()
                .map(this::mapToCompteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompteResponse getCompteById(UUID id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec l'ID: " + id));

        // Vérifier que le compte est actif
        if (!compte.getIsActive()) {
            throw new BusinessException("Ce compte est inactif");
        }

        return mapToCompteResponse(compte);
    }

    @Transactional(readOnly = true)
    public CompteResponse getCompteByNumero(String numeroCompte) {
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec le numéro: " + numeroCompte));

        if (!compte.getIsActive()) {
            throw new BusinessException("Ce compte est inactif");
        }

        return mapToCompteResponse(compte);
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> getComptesByClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        return compteRepository.findByClientId(clientId).stream()
                .map(this::mapToCompteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompteDetailsResponse getCompteDetails(UUID id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec l'ID: " + id));

        List<Transaction> transactions = transactionRepository.findByCompteIdOrderByDateTransactionDesc(id);

        BigDecimal totalDepots = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.DEPOT)
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRetraits = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.RETRAIT)
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CompteDetailsResponse.builder()
                .compte(mapToCompteResponse(compte))
                .totalDepots(totalDepots)
                .totalRetraits(totalRetraits)
                .nombreTransactions(transactions.size())
                .build();
    }

    @Transactional
    public void deleteCompte(UUID id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        UUID clientId = compte.getClient().getId();

        compte.setIsActive(false);
        compteRepository.save(compte);

        long nombreComptesActifs = compteRepository.countByClientIdAndIsActiveTrue(clientId);

        if (nombreComptesActifs == 0) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
            client.setIsActive(false);
            clientRepository.save(client);
        }
    }

    public long countActiveComptes() {
        return compteRepository.findByIsActive(true).size();
    }

    public BigDecimal getTotalSolde() {
        return compteRepository.findByIsActive(true).stream()
                .map(Compte::getSolde)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CompteResponse mapToCompteResponse(Compte compte) {
        CompteResponse.CompteResponseBuilder builder = CompteResponse.builder()
                .id(compte.getId())
                .numeroCompte(compte.getNumeroCompte())
                .solde(compte.getSolde())
                .isActive(compte.getIsActive())
                .clientId(compte.getClient().getId())
                .clientNomComplet(compte.getClient().getPrenom() + " " + compte.getClient().getNom())
                .createdAt(compte.getCreatedAt())
                .updatedAt(compte.getUpdatedAt());

        // Gérer les spécificités selon le type de compte
        if (compte instanceof CompteEpargne) {
            CompteEpargne compteEpargne = (CompteEpargne) compte;
            builder.typeCompte(TypeCompte.EPARGNE)
                    .dateDeblocage(compteEpargne.getDateDeblocage());
        } else if (compte instanceof CompteCheque) {
            CompteCheque compteCheque = (CompteCheque) compte;
            builder.typeCompte(TypeCompte.CHEQUE)
                    .tauxFrais(compteCheque.getTauxFrais());
        }

        return builder.build();
    }
}