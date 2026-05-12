package dev.BatimenTIGER.Service.ServiceImpl;

import dev.BatimenTIGER.Mapper.CategorieMapper;
import dev.BatimenTIGER.Model.Categorie;
import dev.BatimenTIGER.Repository.CategorieRepository;
import dev.BatimenTIGER.Service.ICategorieService;
import dev.BatimenTIGER.dto.CategorieDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor // Utilise Lombok pour injecter automatiquement le repository et le mapper
public class CategorieServiceImpl implements ICategorieService {

    private final CategorieRepository repository;
    private final CategorieMapper mapper;

    @Override
    public List<CategorieDTO> listerToutesLesCategories() {
        return repository.findAll().stream()
                .map(mapper::toDTO) // Utilisation du mapper
                .toList();
    }

    @Override
    public CategorieDTO creerCategorie(CategorieDTO dto) {
        Categorie categorie = mapper.toEntity(dto);
        Categorie savedCategorie = repository.save(categorie);
        return mapper.toDTO(savedCategorie);
    }

    @Override
    public CategorieDTO modifierCategorie(Long id, CategorieDTO dto) {
        // 1. Récupération de la catégorie existante dans la base TIGER
        Categorie existingCategorie = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'ID : " + id));

        // 2. Mise à jour des champs via le mapper
        mapper.updateEntityFromDTO(dto, existingCategorie);

        // 3. Sauvegarde et retour du DTO mis à jour
        return mapper.toDTO(repository.save(existingCategorie));
    }

    @Override
    public void supprimerCategorie(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Catégorie introuvable");
        }
        repository.deleteById(id);
    }
}