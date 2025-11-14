package com.bankmanager.service;

import com.bankmanager.dto.request.CreateCompteRequest;
import com.bankmanager.dto.response.CompteDetailsResponse;
import com.bankmanager.dto.response.CompteResponse;
import com.bankmanager.entity.*;
import com.bankmanager.entity.enums.TypeCompte;
import com.bankmanager.entity.enums.TypeTransaction;
import com.bankmanager.exception.BadRequestException;
import com.bankmanager.exception.ResourceNotFoundException;
import com.bankmanager.repository.ClientRepository;
import com.bankmanager.repository.CompteRepository;
import com.bankmanager.repository.TransactionRepository;
import com.bankmanager.util.CompteNumberGenerator;
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

        Compte compte;
        if (request.getTypeCompte() == TypeCompte.EPARGNE) {
            if (request.getDateDeblocage() == null) {
                throw new BadRequestException("Date de déblocage obligatoire pour compte épargne");
            }
            if (request.getDateDeblocage().isBefore(LocalDate.now())) {
                throw new BadRequestException("Date de déblocage doit être dans le futur");
            }
            CompteEpargne compteEpargne = new CompteEpargne();
            compteEpargne.setDateDeblocage(request.getDateDeblocage());
            compte = compteEpargne;
        } else {
            CompteCheque compteCheque = new CompteCheque();
            compteCheque.setTauxFrais(new BigDecimal("0.008"));
            compte = compteCheque;
        }

        String numeroCompte;
        do {
            numeroCompte = CompteNumberGenerator.generate();
        } while (compteRepository.existsByNumeroCompte(numeroCompte));

        compte.setNumeroCompte(numeroCompte);
        compte.setSolde(request.getSoldeInitial());
        compte.setClient(client);
        compte.setIsActive(true);

        compte = compteRepository.save(compte);
        return mapToCompteResponse(compte);
    }

    @Transactional(readOnly = true)
    public CompteResponse getCompteById(UUID id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
        return mapToCompteResponse(compte);
    }

    @Transactional(readOnly = true)
    public CompteResponse getCompteByNumero(String numeroCompte) {
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
        return mapToCompteResponse(compte);
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> getAllComptes() {
        return compteRepository.findAll().stream()
                .map(this::mapToCompteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> getActiveComptes() {
        return compteRepository.findByIsActiveTrue().stream()
                .map(this::mapToCompteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> getComptesByClient(UUID clientId) {
        return compteRepository.findByClientId(clientId).stream()
                .map(this::mapToCompteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompteDetailsResponse getCompteDetails(UUID id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        BigDecimal totalDepots = transactionRepository.sumMontantByCompteAndType(id, TypeTransaction.DEPOT);
        BigDecimal totalRetraits = transactionRepository.sumMontantByCompteAndType(id, TypeTransaction.RETRAIT);
        long nombreTransactions = transactionRepository.findByCompteId(id).size();

        return CompteDetailsResponse.builder()
                .compte(mapToCompteResponse(compte))
                .totalDepots(totalDepots != null ? totalDepots : BigDecimal.ZERO)
                .totalRetraits(totalRetraits != null ? totalRetraits : BigDecimal.ZERO)
                .nombreTransactions(nombreTransactions)
                .build();
    }

    @Transactional
    public void deleteCompte(UUID id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
        compte.setIsActive(false);
        compteRepository.save(compte);
    }

    private CompteResponse mapToCompteResponse(Compte compte) {
        CompteResponse.CompteResponseBuilder builder = CompteResponse.builder()
                .id(compte.getId())
                .numeroCompte(compte.getNumeroCompte())
                .solde(compte.getSolde())
                .typeCompte(compte.getTypeCompte())
                .isActive(compte.getIsActive())
                .clientId(compte.getClient().getId())
                .clientNomComplet(compte.getClient().getPrenom() + " " + compte.getClient().getNom())
                .createdAt(compte.getCreatedAt())
                .updatedAt(compte.getUpdatedAt());

        if (compte instanceof CompteEpargne) {
            builder.dateDeblocage(((CompteEpargne) compte).getDateDeblocage());
        } else if (compte instanceof CompteCheque) {
            builder.tauxFrais(((CompteCheque) compte).getTauxFrais());
        }

        return builder.build();
    }
}