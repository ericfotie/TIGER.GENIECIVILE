package dev.BatimenTIGER.Conttroller;

import dev.BatimenTIGER.Service.IServiceOffertService;
import dev.BatimenTIGER.dto.ServiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services-offerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Pour ton frontend React/Next.js
public class ServiceOffertController {

    private final IServiceOffertService serviceOffertService;

    // --- ROUTES PUBLIQUES (Visiteurs) ---

    @GetMapping("/public")
    public ResponseEntity<List<ServiceDTO>> getServicesPublics() {
        return ResponseEntity.ok(serviceOffertService.listerServicesPublics());
    }

    // --- ROUTES ADMIN (Gestion) ---

    @GetMapping("/admin/all")
    public ResponseEntity<List<ServiceDTO>> getTousLesServices() {
        return ResponseEntity.ok(serviceOffertService.listerTousLesServices());
    }

    @PostMapping("/admin")
    public ResponseEntity<ServiceDTO> creerService(@RequestBody ServiceDTO dto) {
        return new ResponseEntity<>(serviceOffertService.ajouterService(dto), HttpStatus.CREATED);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable Long id, @RequestBody ServiceDTO dto) {
        return ResponseEntity.ok(serviceOffertService.modifierService(id, dto));
    }

    @PatchMapping("/admin/{id}/etat")
    public ResponseEntity<Void> toggleEtat(@PathVariable Long id, @RequestParam boolean active) {
        serviceOffertService.changerEtatService(id, active);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        serviceOffertService.supprimerService(id);
        return ResponseEntity.noContent().build();
    }
}