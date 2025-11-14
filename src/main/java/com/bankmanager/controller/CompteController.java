package com.bankmanager.controller;

import com.bankmanager.dto.request.CreateCompteRequest;
import com.bankmanager.dto.response.CompteDetailsResponse;
import com.bankmanager.dto.response.CompteResponse;
import com.bankmanager.service.CompteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
public class CompteController {

    private final CompteService compteService;

    @PostMapping
    public ResponseEntity<CompteResponse> createCompte(@Valid @RequestBody CreateCompteRequest request) {
        CompteResponse response = compteService.createCompte(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompteResponse> getCompteById(@PathVariable UUID id) {
        CompteResponse response = compteService.getCompteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numeroCompte}")
    public ResponseEntity<CompteResponse> getCompteByNumero(@PathVariable String numeroCompte) {
        CompteResponse response = compteService.getCompteByNumero(numeroCompte);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CompteResponse>> getAllComptes(@RequestParam(required = false) Boolean active) {
        List<CompteResponse> response;

        if (active != null && active) {
            response = compteService.getActiveComptes();
        } else {
            response = compteService.getAllComptes();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CompteResponse>> getComptesByClient(@PathVariable UUID clientId) {
        List<CompteResponse> response = compteService.getComptesByClient(clientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<CompteDetailsResponse> getCompteDetails(@PathVariable UUID id) {
        CompteDetailsResponse response = compteService.getCompteDetails(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompte(@PathVariable UUID id) {
        compteService.deleteCompte(id);
        return ResponseEntity.noContent().build();
    }
}