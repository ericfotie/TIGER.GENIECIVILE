package dev.BatimenTIGER.Repository;

import dev.BatimenTIGER.Model.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    // Pour le client : Récupère tous les projets avec leurs photos en une seule requête
    @Query("SELECT DISTINCT p FROM Projet p LEFT JOIN FETCH p.photos ORDER BY p.id DESC")
    List<Projet> findAllWithPhotos();

    // Pour le client : Filtrer par catégorie
    List<Projet> findByCategorieId(Long categorieId);

    // Pour l'admin : Rechercher un projet par titre
    List<Projet> findByTitreContainingIgnoreCase(String titre);
}
