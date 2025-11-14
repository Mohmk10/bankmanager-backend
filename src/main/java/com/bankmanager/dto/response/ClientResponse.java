package com.bankmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {
    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private Boolean isActive;
    private Integer nombreComptes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}