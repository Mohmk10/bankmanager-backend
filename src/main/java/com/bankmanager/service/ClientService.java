package com.bankmanager.service;

import com.bankmanager.dto.request.CreateClientRequest;
import com.bankmanager.dto.request.UpdateClientRequest;
import com.bankmanager.dto.response.ClientResponse;
import com.bankmanager.entity.Client;
import com.bankmanager.exception.BadRequestException;
import com.bankmanager.exception.ResourceNotFoundException;
import com.bankmanager.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email déjà utilisé");
        }

        Client client = new Client();
        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setAdresse(request.getAdresse());
        client.setIsActive(true);

        client = clientRepository.save(client);
        return mapToClientResponse(client);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        return mapToClientResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToClientResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getActiveClients() {
        return clientRepository.findByIsActiveTrue().stream()
                .map(this::mapToClientResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> searchClients(String search) {
        return clientRepository.searchClients(search).stream()
                .map(this::mapToClientResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClientResponse updateClient(UUID id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        if (request.getNom() != null) {
            client.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            client.setPrenom(request.getPrenom());
        }
        if (request.getEmail() != null) {
            if (!client.getEmail().equals(request.getEmail())
                    && clientRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email déjà utilisé");
            }
            client.setEmail(request.getEmail());
        }
        if (request.getTelephone() != null) {
            client.setTelephone(request.getTelephone());
        }
        if (request.getAdresse() != null) {
            client.setAdresse(request.getAdresse());
        }

        client = clientRepository.save(client);
        return mapToClientResponse(client);
    }

    @Transactional
    public void deleteClient(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        client.setIsActive(false);
        clientRepository.save(client);
    }

    private ClientResponse mapToClientResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .adresse(client.getAdresse())
                .isActive(client.getIsActive())
                .nombreComptes(client.getComptes().size())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}