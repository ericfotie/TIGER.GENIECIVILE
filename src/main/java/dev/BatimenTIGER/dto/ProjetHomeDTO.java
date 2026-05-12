package dev.BatimenTIGER.dto;

public record ProjetHomeDTO(
        Long id,
        String titre,
        String localisation,
        String imageCouverture,
        String nomCategorie
) {
}
