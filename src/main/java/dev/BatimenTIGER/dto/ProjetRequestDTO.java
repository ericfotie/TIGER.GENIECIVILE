package dev.BatimenTIGER.dto;

public record ProjetRequestDTO(
        String titre,
        String description,
        String localisation,
        Double budgetEstime,
        String statut,
        Long categorieId
) {
}
