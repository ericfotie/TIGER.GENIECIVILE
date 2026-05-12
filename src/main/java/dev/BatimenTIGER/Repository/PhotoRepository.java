package dev.BatimenTIGER.Repository;

import dev.BatimenTIGER.Model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // Trouver toutes les photos d'un projet spécifique
    List<Photo> findByProjetId(Long projetId);

    // Utile pour l'admin : réinitialiser la photo principale avant d'en choisir une nouvelle
    @Modifying
    @Query("UPDATE Photo p SET p.isPrincipale = false WHERE p.projet.id = :projetId")
    void resetPrincipale(Long projetId);
}