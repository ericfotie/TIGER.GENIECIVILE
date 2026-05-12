package dev.BatimenTIGER.dto;

public record ServiceDTO(
        Long id,
        String titre,
        String description,
        String iconeName,
        boolean isActive
) {
}
