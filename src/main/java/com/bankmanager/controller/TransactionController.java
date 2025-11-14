package com.bankmanager.controller;

import com.bankmanager.dto.request.CreateTransactionRequest;
import com.bankmanager.dto.response.TransactionResponse;
import com.bankmanager.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id-transaction/{idTransaction}")
    public ResponseEntity<TransactionResponse> getTransactionByIdTransaction(@PathVariable String idTransaction) {
        TransactionResponse response = transactionService.getTransactionByIdTransaction(idTransaction);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> response = transactionService.getAllTransactions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/compte/{compteId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCompte(@PathVariable UUID compteId) {
        List<TransactionResponse> response = transactionService.getTransactionsByCompte(compteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @RequestParam(defaultValue = "7") int days) {
        List<TransactionResponse> response = transactionService.getRecentTransactions(days);
        return ResponseEntity.ok(response);
    }
}