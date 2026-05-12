package dev.BatimenTIGER.Mapper;

import dev.BatimenTIGER.Model.ServiceOffert;
import dev.BatimenTIGER.dto.ServiceDTO;
import org.springframework.stereotype.Component;

@Component
public class ServiceOffertMapper {

    // Vers le DTO (pour l'affichage public ou admin)
    public ServiceDTO toDTO(ServiceOffert service) {
        if (service == null) return null;
        return new ServiceDTO(
                service.getId(),
                service.getTitre(),         // Mapping : nom -> titre
                service.getDescription(),
                service.getIconeName(),      // Mapping : icone -> iconeName
                service.isActive()        // Mapping : actif -> isActive
        );
    }

    // Vers l'Entité (pour l'ajout en base de données)
    public ServiceOffert toEntity(ServiceDTO dto) {
        if (dto == null) return null;
        ServiceOffert service = new ServiceOffert();
        service.setTitre(dto.titre());        // Utilise titre() du record
        service.setDescription(dto.description());
        service.setIconeName(dto.iconeName());  // Utilise iconeName() du record
        service.setActive(dto.isActive());   // Utilise isActive() du record
        return service;
    }

    // Pour la mise à jour (Update)
    public void updateEntityFromDTO(ServiceDTO dto, ServiceOffert service) {
        if (dto == null || service == null) return;
        service.setTitre(dto.titre());
        service.setDescription(dto.description());
        service.setIconeName(dto.iconeName());
        service.setActive(dto.isActive());
    }
}