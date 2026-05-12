package dev.BatimenTIGER.dto;

import java.util.List;

public record ProjetResponseDTO(
        Long id,
        String titre,
        String description,
        String localisation,
        String statut,
        String nomCategorie,
        List<PhotoDTO> galerie,
        List<PlanDTO> plans
) {}
