package com.bankmanager.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientRequest {

    private String nom;
    private String prenom;

    @Email(message = "Email invalide")
    private String email;

    private String telephone;
    private String adresse;
}