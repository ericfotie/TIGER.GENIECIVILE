package dev.BatimenTIGER.Service.ServiceImpl;

import dev.BatimenTIGER.Mapper.ServiceOffertMapper;
import dev.BatimenTIGER.Model.ServiceOffert;
import dev.BatimenTIGER.Repository.ServiceOffertRepository;
import dev.BatimenTIGER.Service.IServiceOffertService;
import dev.BatimenTIGER.dto.ServiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceOffertServiceImpl implements IServiceOffertService {

    private final ServiceOffertRepository repository;
    private final ServiceOffertMapper mapper;

    @Override
    public List<ServiceDTO> listerServicesPublics() {
        // Filtre pour ne montrer que les services actifs aux visiteurs
        return repository.findByIsActiveTrue().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ServiceDTO> listerTousLesServices() {
        // Liste complète pour le dashboard admin (inclut les services masqués)
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ServiceDTO ajouterService(ServiceDTO serviceDto) {
        ServiceOffert service = mapper.toEntity(serviceDto);
        ServiceOffert savedService = repository.save(service);
        return mapper.toDTO(savedService);
    }

    @Override
    public ServiceDTO modifierService(Long id, ServiceDTO serviceDto) {
        ServiceOffert existingService = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service introuvable avec l'ID : " + id));

        // Mise à jour des champs via le mapper
        mapper.updateEntityFromDTO(serviceDto, existingService);

        return mapper.toDTO(repository.save(existingService));
    }

    @Override
    public void changerEtatService(Long id, boolean isActive) {
        ServiceOffert service = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service introuvable"));

        service.setActive(isActive);
        repository.save(service);
    }

    @Override
    public void supprimerService(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Suppression impossible : Service inexistant");
        }
        repository.deleteById(id);
    }
}
