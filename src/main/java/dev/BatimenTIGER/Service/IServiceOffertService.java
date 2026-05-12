package dev.BatimenTIGER.Service;

import dev.BatimenTIGER.dto.ServiceDTO;

import java.util.List;

public interface IServiceOffertService {
    List<ServiceDTO> listerServicesPublics(); // Uniquement les services actifs
    List<ServiceDTO> listerTousLesServices(); // Pour l'admin (gestion)
    ServiceDTO ajouterService(ServiceDTO serviceDto);
    ServiceDTO modifierService(Long id, ServiceDTO serviceDto);
    void changerEtatService(Long id, boolean isActive);
    void supprimerService(Long id);
}