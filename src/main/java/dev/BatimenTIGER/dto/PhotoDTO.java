package dev.BatimenTIGER.dto;

public record PhotoDTO(
        Long id,
        String url,
        String legende,
        boolean isPrincipale
) {
}
