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

        // Utilisation du mapper pour transformer le DTO en entité
        Projet projet = projetMapper.toEntity(request, cat);

        // Le traitement des fichiers reste ici car c'est une action de service (IO)
        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                String url = fileStorageService.stockerFichier(photos.get(i), "photos");
                Photo p = new Photo();
                p.setUrl(url);
                p.setPrincipale(i == 0);
                p.setProjet(projet);
                projet.getPhotos().add(p);
            }
        }

        if (plans != null) {
            plans.forEach(f -> {
                String url = fileStorageService.stockerFichier(f, "plans");
                Plan pl = new Plan();
                pl.setNomDocument(f.getOriginalFilename());
                pl.setFichierUrl(url);
                pl.setProjet(projet);
                projet.getPlans().add(pl);
            });
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

    @Override public List<ProjetHomeDTO> rechercherProjets(String m) { return null; }
    @Override public void definirPhotoPrincipale(Long pid, Long phid) {}
    @Override public void supprimerPhoto(Long id) {}
}