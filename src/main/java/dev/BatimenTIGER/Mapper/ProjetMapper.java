package dev.BatimenTIGER.Mapper;

import dev.BatimenTIGER.Model.*;
import dev.BatimenTIGER.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjetMapper {

    // Vers ProjetHomeDTO (Pour la page d'accueil)
    public ProjetHomeDTO toHomeDTO(Projet p) {
        if (p == null) return null;

        String coverUrl = p.getPhotos().stream()
                .filter(Photo::isPrincipale)
                .map(Photo::getUrl)
                .findFirst()
                .orElse(p.getPhotos().isEmpty() ? null : p.getPhotos().get(0).getUrl());

        return new ProjetHomeDTO(
                p.getId(),
                p.getTitre(),
                p.getLocalisation(),
                coverUrl,
                p.getCategorie() != null ? p.getCategorie().getNom() : "Sans catégorie"
        );
    }

    // Vers ProjetResponseDTO (Détails complets)
    public ProjetResponseDTO toResponseDTO(Projet p) {
        if (p == null) return null;

        List<PhotoDTO> photos = p.getPhotos().stream()
                .map(ph -> new PhotoDTO(ph.getId(), ph.getUrl(), ph.getLegende(), ph.isPrincipale()))
                .toList();

        List<PlanDTO> plans = p.getPlans().stream()
                .map(pl -> new PlanDTO(pl.getId(), pl.getNomDocument(), pl.getFichierUrl(), pl.getTypeTechnique(), pl.getIndiceRevision()))
                .toList();

        return new ProjetResponseDTO(
                p.getId(),
                p.getTitre(),
                p.getDescription(),
                p.getLocalisation(),
                p.getStatut() != null ? p.getStatut().name() : null,
                p.getCategorie() != null ? p.getCategorie().getNom() : null,
                photos,
                plans
        );
    }

    // Création de l'entité de base à partir du RequestDTO
    public Projet toEntity(ProjetRequestDTO request, Categorie categorie) {
        if (request == null) return null;

        Projet projet = new Projet();
        projet.setTitre(request.titre());
        projet.setDescription(request.description());
        projet.setLocalisation(request.localisation());
        projet.setBudgetEstime(request.budgetEstime());
        projet.setStatut(StatutProjet.valueOf(request.statut()));
        projet.setCategorie(categorie);

        return projet;
    }

    // Méthode de mise à jour (Update)
    public void updateEntityFromDTO(ProjetRequestDTO dto, Projet projet, Categorie nouvelleCategorie) {
        if (dto == null || projet == null) return;

        projet.setTitre(dto.titre());
        projet.setDescription(dto.description());
        projet.setLocalisation(dto.localisation());
        projet.setBudgetEstime(dto.budgetEstime());
        projet.setStatut(StatutProjet.valueOf(dto.statut()));
        projet.setCategorie(nouvelleCategorie);
    }
}