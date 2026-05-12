package dev.BatimenTIGER.dto;

public record PlanDTO(
        Long id,
        String nomDocument,
        String fichierUrl,
        String typeTechnique,
        String indiceRevision
) {
}
