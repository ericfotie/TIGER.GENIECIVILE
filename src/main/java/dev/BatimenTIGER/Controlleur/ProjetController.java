package dev.BatimenTIGER.Controlleur;

import dev.BatimenTIGER.Service.IProjetService;
import dev.BatimenTIGER.dto.ProjetHomeDTO;
import dev.BatimenTIGER.dto.ProjetRequestDTO;
import dev.BatimenTIGER.dto.ProjetResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
@CrossOrigin(origins = "*") // Permet à ton application React de consommer l'API sans blocage CORS
public class ProjetController {

    private final IProjetService projetService;

    public ProjetController(IProjetService projetService) {
        this.projetService = projetService;
    }

    // ==========================================
    // 🌐 ESPACE VISITEURS (PUBLIC)
    // ==========================================

    // 1. Liste simplifiée pour la page d'accueil (avec couverture)
    @GetMapping("/accueil")
    public ResponseEntity<List<ProjetHomeDTO>> getProjetsPourAccueil() {
        return ResponseEntity.ok(projetService.getProjetsPourAccueil());
    }

    // 2. Consulter les détails complets d'un projet spécifique (avec plans et galerie)
    @GetMapping("/{id}")
    public ResponseEntity<ProjetResponseDTO> getDetailsProjet(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.getDetailsProjet(id));
    }

    // 3. Filtrer les projets par catégorie
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<ProjetHomeDTO>> filtrerParCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(projetService.filtrerParCategorie(categorieId));
    }

    // 4. Rechercher des projets par mot-clé (barre de recherche de la vitrine)
    @GetMapping("/recherche")
    public ResponseEntity<List<ProjetHomeDTO>> rechercherProjets(@RequestParam("keyword") String motCle) {
        return ResponseEntity.ok(projetService.rechercherProjets(motCle));
    }

    // ==========================================
    // 🐯 ESPACE GESTION (ADMINISTRATION)
    // ==========================================

    // 1. Création d'un projet complet avec gestion multi-fichiers (Multipart FORM DATA)
    @PostMapping(value = "/admin", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProjetResponseDTO> creerProjet(
            @RequestPart("data") ProjetRequestDTO request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @RequestPart(value = "plans", required = false) List<MultipartFile> plans) {

        ProjetResponseDTO response = projetService.creerProjet(request, photos, plans);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Modification des informations textuelles d'un projet
    @PutMapping("/admin/{id}")
    public ResponseEntity<ProjetResponseDTO> modifierProjet(
            @PathVariable Long id,
            @RequestBody ProjetRequestDTO request) {
        return ResponseEntity.ok(projetService.modifierProjet(id, request));
    }

    // 3. Mise à jour de l'état d'avancement (ETUDE -> EN_COURS -> TERMINE)
    @PatchMapping("/admin/{id}/statut")
    public ResponseEntity<ProjetResponseDTO> modifierStatut(
            @PathVariable Long id,
            @RequestParam("statut") String nouveauStatut) {
        return ResponseEntity.ok(projetService.modifierStatut(id, nouveauStatut));
    }

    // 4. Suppression définitive d'un projet (et nettoyage logique via le service)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> supprimerProjet(@PathVariable Long id) {
        projetService.supprimerProjet(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 🖼️ GESTIONNAIRE DE LA GALERIE MEDIA (ADMIN)
    // ==========================================

    // Définir quelle image de la galerie devient la couverture par défaut
    @PatchMapping("/admin/{projetId}/galerie/principale/{photoId}")
    public ResponseEntity<Void> definirPhotoPrincipale(
            @PathVariable Long projetId,
            @PathVariable Long photoId) {
        projetService.definirPhotoPrincipale(projetId, photoId);
        return ResponseEntity.ok().build();
    }

    // Retirer une photo spécifique de la galerie média
    @DeleteMapping("/admin/galerie/photos/{photoId}")
    public ResponseEntity<Void> supprimerPhoto(@PathVariable Long photoId) {
        projetService.supprimerPhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}