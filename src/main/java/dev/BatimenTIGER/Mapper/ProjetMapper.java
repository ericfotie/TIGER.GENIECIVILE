package dev.BatimenTIGER.Mapper;

import dev.BatimenTIGER.Model.*;
import dev.BatimenTIGER.dto.*;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ProjetMapper {

    public ProjetHomeDTO toHomeDTO(Projet p) {
        if (p == null) return null;

        // On mappe la liste complète des photos pour que le front-end ait accès aux ID
        List<PhotoDTO> photos = Optional.ofNullable(p.getPhotos())
                .orElse(Collections.emptyList()).stream()
                .map(ph -> new PhotoDTO(ph.getId(), ph.getUrl(), ph.getLegende(), ph.isPrincipale()))
                .toList();

        // On mappe la liste complète des plans
        List<PlanDTO> plans = Optional.ofNullable(p.getPlans())
                .orElse(Collections.emptyList()).stream()
                .map(pl -> new PlanDTO(pl.getId(), pl.getNomDocument(), pl.getFichierUrl(), pl.getTypeTechnique(), pl.getIndiceRevision()))
                .toList();

        return new ProjetHomeDTO(
                p.getId(),
                p.getTitre(),
                p.getDescription(),
                p.getLocalisation(),
                p.getStatut() != null ? p.getStatut().name() : "ETUDE",
                p.getCategorie() != null ? p.getCategorie().getNom() : "Sans catégorie",
                photos,
                plans
        );
    }

    public ProjetResponseDTO toResponseDTO(Projet p) {
        if (p == null) return null;

        List<PhotoDTO> photos = Optional.ofNullable(p.getPhotos())
                .orElse(Collections.emptyList()).stream()
                .map(ph -> new PhotoDTO(ph.getId(), ph.getUrl(), ph.getLegende(), ph.isPrincipale()))
                .toList();

        List<PlanDTO> plans = Optional.ofNullable(p.getPlans())
                .orElse(Collections.emptyList()).stream()
                .map(pl -> new PlanDTO(pl.getId(), pl.getNomDocument(), pl.getFichierUrl(), pl.getTypeTechnique(), pl.getIndiceRevision()))
                .toList();

        return new ProjetResponseDTO(
                p.getTitre(),
                p.getDescription(),
                p.getLocalisation(),
                p.getStatut() != null ? p.getStatut().name() : null,
                p.getCategorie() != null ? p.getCategorie().getNom() : null,
                photos,
                plans
        );
    }

    // Les méthodes toEntity et update... restent inchangées
    public Projet toEntity(ProjetRequestDTO request, Categorie categorie) {
        if (request == null) return null;
        Projet projet = new Projet();
        updateEntityFields(projet, request, categorie);
        return projet;
    }

    public void updateEntityFromDTO(ProjetRequestDTO dto, Projet projet, Categorie nouvelleCategorie) {
        if (dto != null && projet != null) {
            updateEntityFields(projet, dto, nouvelleCategorie);
        }
    }

    private void updateEntityFields(Projet p, ProjetRequestDTO dto, Categorie cat) {
        p.setTitre(dto.titre());
        p.setDescription(dto.description());
        p.setLocalisation(dto.localisation());
        p.setBudgetEstime(dto.budgetEstime());
        p.setCategorie(cat);
        try {
            p.setStatut(StatutProjet.valueOf(dto.statut().toUpperCase()));
        } catch (Exception e) {
            p.setStatut(StatutProjet.ETUDE);
        }
    }
}