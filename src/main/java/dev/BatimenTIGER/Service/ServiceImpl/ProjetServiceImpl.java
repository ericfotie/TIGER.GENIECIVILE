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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjetServiceImpl implements IProjetService {

    private final ProjetRepository projetRepository;
    private final CategorieRepository categorieRepository;
    private final IFileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ProjetMapper projetMapper; // Injection du mapper

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

        // 1. Transformation du DTO en entité via le mapper
        Projet projet = projetMapper.toEntity(request, cat);

        // 2. Sécurisation des listes pour éviter le NullPointerException
        if (projet.getPhotos() == null) {
            projet.setPhotos(new java.util.ArrayList<>());
        }
        if (projet.getPlans() == null) {
            projet.setPlans(new java.util.ArrayList<>());
        }

        // 3. Traitement des fichiers Photos
        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile file = photos.get(i);
                if (!file.isEmpty()) {
                    String url = fileStorageService.stockerFichier(file, "projets/photos");
                    Photo p = new Photo();
                    p.setUrl(url);
                    p.setLegende("Photo " + (i + 1) + " - " + projet.getTitre());
                    p.setPrincipale(i == 0); // La première photo est la principale
                    p.setProjet(projet);     // Liaison clé étrangère vers le parent

                    projet.getPhotos().add(p);
                }
            }
        }

        // 4. Traitement des fichiers Plans (Arrangé avec valeurs par défaut propres)
        if (plans != null && !plans.isEmpty()) {
            for (int i = 0; i < plans.size(); i++) {
                MultipartFile file = plans.get(i);
                if (!file.isEmpty()) {
                    String url = fileStorageService.stockerFichier(file, "projets/plans");
                    String nomOrigine = file.getOriginalFilename();

                    Plan pl = new Plan();
                    pl.setNomDocument(nomOrigine != null ? nomOrigine : "Plan_" + (i + 1));
                    pl.setFichierUrl(url);
                    pl.setProjet(projet);    // Liaison clé étrangère vers le parent

                    // 🌟 VALEURS PAR DÉFAUT PROPRES ET PARLANTES :
                    pl.setIndiceRevision("Ind. 0"); // Représente la version initiale (ou "Rev A")

                    // Détection automatique du type technique selon le nom ou l'index
                    if (nomOrigine != null && nomOrigine.toLowerCase().contains("archi")) {
                        pl.setTypeTechnique("Architecture");
                    } else if (nomOrigine != null && nomOrigine.toLowerCase().contains("structure")) {
                        pl.setTypeTechnique("Structure / Béton Armé");
                    } else {
                        // Valeur générique et propre par défaut
                        pl.setTypeTechnique("Plan Technique général " + (i + 1));
                    }

                    projet.getPlans().add(pl);
                }
            }
        }

        // 5. Sauvegarde de l'ensemble (Le CascadeType.ALL dans l'entité Projet enregistrera les enfants)
        Projet saved = projetRepository.save(projet);

        // 6. Notification WebSocket en temps réel
        messagingTemplate.convertAndSend("/topic/projets", "UPDATE");

        return projetMapper.toResponseDTO(saved);
    }
    @Override
    public ProjetResponseDTO modifierProjet(Long id, ProjetRequestDTO r) {
        Projet existing = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        Categorie cat = categorieRepository.findById(r.categorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        // Mise à jour de l'entité existante via le mapper
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
        // Optionnel : supprimer les fichiers physiques avant de supprimer l'entrée en DB
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
        // Si le mot-clé est vide ou nul, on retourne une liste vide pour éviter les requêtes inutiles
        if (m == null || m.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // Récupération des entités et transformation en DTO pour l'affichage à l'accueil
        return projetRepository.findByTitreContainingIgnoreCase(m.trim()).stream()
                .map(projetMapper::toHomeDTO)
                .toList();
    }

    @Override
    public void definirPhotoPrincipale(Long pid, Long phid) {
        // 1. Récupérer le projet concerné
        Projet projet = projetRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("Projet introuvable avec l'ID : " + pid));

        // 2. Parcourir les photos du projet pour mettre à jour le statut "isPrincipale"
        boolean photoTrouvee = false;
        for (Photo photo : projet.getPhotos()) {
            if (photo.getId().equals(phid)) {
                photo.setPrincipale(true);
                photoTrouvee = true;
            } else {
                photo.setPrincipale(false); // Toutes les autres passent à false
            }
        }

        if (!photoTrouvee) {
            throw new RuntimeException("La photo avec l'ID " + phid + " n'appartient pas à ce projet.");
        }

        // 3. Sauvegarder les modifications et notifier le Front-end
        projetRepository.save(projet);
        messagingTemplate.convertAndSend("/topic/projets", "UPDATE");
    }

    @Override
    public void supprimerPhoto(Long id) {
        // 1. Trouver le projet qui possède cette photo pour pouvoir détacher le lien
        // (Utile car vous utilisez 'orphanRemoval = true' sur la liste des photos du Projet)
        Projet projet = projetRepository.findAll().stream()
                .filter(p -> p.getPhotos().stream().anyMatch(ph -> ph.getId().equals(id)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo introuvable ou associée à aucun projet."));

        // 2. Trouver l'entité Photo pour récupérer son URL et supprimer le fichier physique
        Photo photoASupprimer = projet.getPhotos().stream()
                .filter(ph -> ph.getId().equals(id))
                .findFirst()
                .orElseThrow();

        // Suppression du fichier sur le disque/serveur
        fileStorageService.supprimerFichierPhysique(photoASupprimer.getUrl());

        // 3. Retirer la photo de la liste du projet.
        // Grâce à 'orphanRemoval = true', Hibernate va automatiquement supprimer la ligne dans t_photos.
        projet.getPhotos().remove(photoASupprimer);

        // Si la photo supprimée était la principale, on peut réassigner la première restante par défaut
        if (photoASupprimer.isPrincipale() && !projet.getPhotos().isEmpty()) {
            projet.getPhotos().get(0).setPrincipale(true);
        }

        // 4. Sauvegarder le projet mis à jour et notifier l'application
        projetRepository.save(projet);
        messagingTemplate.convertAndSend("/topic/projets", "UPDATE");
    }
}