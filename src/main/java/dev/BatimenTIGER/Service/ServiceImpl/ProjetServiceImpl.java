package dev.BatimenTIGER.Service.ServiceImpl;

import dev.BatimenTIGER.Mapper.ProjetMapper;
import dev.BatimenTIGER.Model.*;
import dev.BatimenTIGER.Repository.CategorieRepository;
import dev.BatimenTIGER.Repository.ProjetRepository;
import dev.BatimenTIGER.Service.IFileStorageService;
import dev.BatimenTIGER.Service.IProjetService;
import dev.BatimenTIGER.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ProjetServiceImpl implements IProjetService {

    private final ProjetRepository projetRepository;
    private final CategorieRepository categorieRepository;
    private final IFileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ProjetMapper projetMapper;

    public ProjetServiceImpl(ProjetRepository projetRepository, CategorieRepository categorieRepository, IFileStorageService fileStorageService, SimpMessagingTemplate messagingTemplate, ProjetMapper projetMapper) {
        this.projetRepository = projetRepository;
        this.categorieRepository = categorieRepository;
        this.fileStorageService = fileStorageService;
        this.messagingTemplate = messagingTemplate;
        this.projetMapper = projetMapper;
    }

    @Override
    public List<ProjetHomeDTO> getProjetsPourAccueil() {
        return projetRepository.findAllWithPhotos().stream()
                .map(projetMapper::toHomeDTO)
                .toList();
    }

    @Override
    public ProjetResponseDTO creerProjet(ProjetRequestDTO request, List<MultipartFile> photos, List<MultipartFile> plans) {
        Categorie cat = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        Projet projet = projetMapper.toEntity(request, cat);
        java.time.LocalDate dateDebut = java.time.LocalDate.now();
        projet.setDateDebut(dateDebut);
        projet.setDateFin(dateDebut.plusMonths(6));
        // -----------------------------------

        if (projet.getPhotos() == null) {
            projet.setPhotos(new java.util.ArrayList<>());
        }
        if (projet.getPlans() == null) {
            projet.setPlans(new java.util.ArrayList<>());
        }

        // 3. Traitement des fichiers Photos (Utilisation du service de stockage)
        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile file = photos.get(i);
                if (!file.isEmpty()) {
                    // Stockage via le service (retourne une URL)
                    String url = fileStorageService.stockerFichier(file, "projets/photos");

                    Photo p = new Photo();
                    p.setUrl(url); // On stocke l'URL au lieu du byte[]
                    p.setLegende("Photo " + (i + 1) + " - " + projet.getTitre());
                    p.setPrincipale(i == 0);
                    p.setProjet(projet);

                    projet.getPhotos().add(p);
                }
            }
        }

        // 4. Traitement des fichiers Plans
        if (plans != null && !plans.isEmpty()) {
            for (int i = 0; i < plans.size(); i++) {
                MultipartFile file = plans.get(i);
                if (!file.isEmpty()) {
                    String url = fileStorageService.stockerFichier(file, "projets/plans");
                    String nomOrigine = file.getOriginalFilename();

                    Plan pl = new Plan();
                    pl.setNomDocument(nomOrigine != null ? nomOrigine : "Plan_" + (i + 1));
                    pl.setFichierUrl(url);
                    pl.setProjet(projet);
                    pl.setIndiceRevision("Ind. 0");

                    if (nomOrigine != null && nomOrigine.toLowerCase().contains("archi")) {
                        pl.setTypeTechnique("Architecture");
                    } else if (nomOrigine != null && nomOrigine.toLowerCase().contains("structure")) {
                        pl.setTypeTechnique("Structure / Béton Armé");
                    } else {
                        pl.setTypeTechnique("Plan Technique général " + (i + 1));
                    }

                    projet.getPlans().add(pl);
                }
            }
        }

        Projet saved = projetRepository.save(projet);
        messagingTemplate.convertAndSend("/topic/projets", "UPDATE");

        return projetMapper.toResponseDTO(saved);
    }

    @Override
    public ProjetResponseDTO modifierProjet(Long id, ProjetRequestDTO r) {
        Projet existing = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        Categorie cat = categorieRepository.findById(r.categorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        projetMapper.updateEntityFromDTO(r, existing, cat);

        Projet updated = projetRepository.save(existing);
        messagingTemplate.convertAndSend("/topic/projets", "UPDATE");
        return projetMapper.toResponseDTO(updated);
    }

    @Override
    public ProjetResponseDTO getDetailsProjet(Long id) {
        return projetRepository.findById(id)
                .map(projetMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));
    }

    @Override
    public void supprimerProjet(Long id) {
        projetRepository.deleteById(id);
        messagingTemplate.convertAndSend("/topic/projets", "DELETE");
    }

    @Override
    public List<ProjetHomeDTO> filtrerParCategorie(Long id) {
        return projetRepository.findByCategorieId(id).stream()
                .map(projetMapper::toHomeDTO)
                .toList();
    }

    @Override
    public ProjetResponseDTO modifierStatut(Long id, String s) {
        Projet p = projetRepository.findById(id).orElseThrow();
        p.setStatut(StatutProjet.valueOf(s));
        return projetMapper.toResponseDTO(projetRepository.save(p));
    }

    @Override
    public List<ProjetHomeDTO> rechercherProjets(String m) {
        if (m == null || m.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return projetRepository.findByTitreContainingIgnoreCase(m.trim()).stream()
                .map(projetMapper::toHomeDTO)
                .toList();
    }

    @Override
    public void definirPhotoPrincipale(Long pid, Long phid) {
        Projet projet = projetRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("Projet introuvable avec l'ID : " + pid));

        boolean photoTrouvee = false;
        for (Photo photo : projet.getPhotos()) {
            if (photo.getId().equals(phid)) {
                photo.setPrincipale(true);
                photoTrouvee = true;
            } else {
                photo.setPrincipale(false);
            }
        }

        if (!photoTrouvee) {
            throw new RuntimeException("La photo avec l'ID " + phid + " n'appartient pas à ce projet.");
        }

        projetRepository.save(projet);
        messagingTemplate.convertAndSend("/topic/projets", "UPDATE");
    }

    @Override
    public void supprimerPhoto(Long id) {
        Projet projet = projetRepository.findAll().stream()
                .filter(p -> p.getPhotos().stream().anyMatch(ph -> ph.getId().equals(id)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo introuvable ou associée à aucun projet."));

        Photo photoASupprimer = projet.getPhotos().stream()
                .filter(ph -> ph.getId().equals(id))
                .findFirst()
                .orElseThrow();

        // Suppression uniquement logique via orphanRemoval
        projet.getPhotos().remove(photoASupprimer);

        if (photoASupprimer.isPrincipale() && !projet.getPhotos().isEmpty()) {
            projet.getPhotos().get(0).setPrincipale(true);
        }

        projetRepository.save(projet);
        messagingTemplate.convertAndSend("/topic/projets", "UPDATE");
    }
}