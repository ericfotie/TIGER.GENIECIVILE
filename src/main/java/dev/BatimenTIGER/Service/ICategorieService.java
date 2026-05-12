package dev.BatimenTIGER.Service;

import dev.BatimenTIGER.dto.CategorieDTO;

import java.util.List;

public interface ICategorieService {
    List<CategorieDTO> listerToutesLesCategories();
    CategorieDTO creerCategorie(CategorieDTO categorieDto);
    CategorieDTO modifierCategorie(Long id, CategorieDTO categorieDto);
    void supprimerCategorie(Long id);
}
