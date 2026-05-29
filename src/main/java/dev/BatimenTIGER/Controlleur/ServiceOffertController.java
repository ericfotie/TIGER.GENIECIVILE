package dev.BatimenTIGER.Controlleur;

import dev.BatimenTIGER.Service.IServiceOffertService;
import dev.BatimenTIGER.dto.ServiceDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceOffertController {

    private final IServiceOffertService serviceOffertService;

    public ServiceOffertController(IServiceOffertService serviceOffertService) {
        this.serviceOffertService = serviceOffertService;
    }

    // --- ACCÈS PUBLIC (VISITEURS) ---

    @GetMapping("/public/services")
    public ResponseEntity<List<ServiceDTO>> getPublicServices() {
        return ResponseEntity.ok(serviceOffertService.listerServicesPublics());
    }

    // --- ACCÈS ADMINISTRATION ---

    @GetMapping("/admin/services")
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(serviceOffertService.listerTousLesServices());
    }

    @PostMapping("/admin/services")
    public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceDTO serviceDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceOffertService.ajouterService(serviceDto));
    }

    @PutMapping("/admin/services/{id}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable Long id, @RequestBody ServiceDTO serviceDto) {
        return ResponseEntity.ok(serviceOffertService.modifierService(id, serviceDto));
    }

    @PatchMapping("/admin/services/{id}/etat")
    public ResponseEntity<Void> toggleServiceStatus(@PathVariable Long id, @RequestParam boolean isActive) {
        serviceOffertService.changerEtatService(id, isActive);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceOffertService.supprimerService(id);
        return ResponseEntity.noContent().build();
    }
}