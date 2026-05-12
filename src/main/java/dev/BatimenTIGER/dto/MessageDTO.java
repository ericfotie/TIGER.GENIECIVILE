package dev.BatimenTIGER.dto;

import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        String nomClient,
        String email,
        String telephone,
        String sujet,
        String contenu,
        LocalDateTime dateReception,
        boolean estTraite,
        String whatsappLink // Le lien généré pour l'admin
) {
}
