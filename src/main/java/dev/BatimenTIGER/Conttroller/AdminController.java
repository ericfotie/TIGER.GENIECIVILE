package dev.BatimenTIGER.Conttroller;

import dev.BatimenTIGER.Service.ICategorieService;
import dev.BatimenTIGER.Service.IMessageService;
import dev.BatimenTIGER.Service.IProjetService;
import dev.BatimenTIGER.dto.CategorieDTO;
import dev.BatimenTIGER.dto.MessageDTO;
import dev.BatimenTIGER.dto.ProjetRequestDTO;
import dev.BatimenTIGER.dto.ProjetResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final IProjetService projetService;
    private final IMessageService messageService;
    private final ICategorieService categorieService;

    public AdminController(IProjetService projetService,
                           IMessageService messageService,
                           ICategorieService categorieService) {
        this.projetService = projetService;
        this.messageService = messageService;
        this.categorieService = categorieService;
    }

    // Création d'un projet complet avec fichiers
    @PostMapping(value = "/projets", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProjetResponseDTO> createProject(
            @RequestPart("data") ProjetRequestDTO request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @RequestPart(value = "plans", required = false) List<MultipartFile> plans) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projetService.creerProjet(request, photos, plans));
    }

    @DeleteMapping("/projets/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projetService.supprimerProjet(id);
        return ResponseEntity.noContent().build();
    }

    // Gestion des messages clients
    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        return ResponseEntity.ok(messageService.listerTousLesMessages());
    }

    @PatchMapping("/messages/{id}/traiter")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        messageService.marquerCommeTraite(id);
        return ResponseEntity.ok().build();
    }

    // Gestion des catégories
    @PostMapping("/categories")
    public ResponseEntity<CategorieDTO> addCategorie(@RequestBody CategorieDTO dto) {
        return ResponseEntity.ok(categorieService.creerCategorie(dto));
    }
}