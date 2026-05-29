package dev.BatimenTIGER.Mapper;

import dev.BatimenTIGER.Model.Categorie;
import dev.BatimenTIGER.dto.CategorieDTO;
import org.springframework.stereotype.Component;

@Component
public class CategorieMapper {

    // Vers le DTO
    public CategorieDTO toDTO(Categorie categorie) {
        if (categorie == null) return null;
        return new CategorieDTO(
                categorie.getNom(),
                categorie.getDescription()
        );
    }

    // Vers l'Entité (Création)
    public Categorie toEntity(CategorieDTO dto) {
        if (dto == null) return null;
        Categorie categorie = new Categorie();
        categorie.setNom(dto.nom());
        categorie.setDescription(dto.description());
        return categorie;
    }

    // Pour la mise à jour (Update)
    public void updateEntityFromDTO(CategorieDTO dto, Categorie categorie) {
        if (dto == null || categorie == null) return;
        categorie.setNom(dto.nom());
        categorie.setDescription(dto.description());
    }
}
