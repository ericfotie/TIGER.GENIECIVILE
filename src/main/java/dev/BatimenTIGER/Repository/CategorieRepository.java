package dev.BatimenTIGER.Repository;

import dev.BatimenTIGER.Model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    // Vérifier si une catégorie existe déjà par son nom
    Optional<Categorie> findByNomIgnoreCase(String nom);
}
