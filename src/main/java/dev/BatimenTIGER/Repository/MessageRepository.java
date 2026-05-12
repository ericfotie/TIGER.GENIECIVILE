package dev.BatimenTIGER.Repository;

import dev.BatimenTIGER.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Pour l'admin : Voir les messages non traités en priorité (du plus récent au plus ancien)
    List<Message> findByEstTraiteFalse();

    // Rechercher les messages d'un client par son numéro de téléphone
    List<Message> findByTelephoneContaining(String telephone);

    // Optionnel : Compter le nombre de messages non lus pour un badge de notification
    long countByEstTraiteFalse();
}