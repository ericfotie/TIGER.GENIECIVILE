package dev.BatimenTIGER.Repository;

import dev.BatimenTIGER.Model.ServiceOffert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOffertRepository extends JpaRepository<ServiceOffert, Long> {
    // Récupérer uniquement les services actifs pour la page d'accueil
    List<ServiceOffert> findByIsActiveTrue();
}
