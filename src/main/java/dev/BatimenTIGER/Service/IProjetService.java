package dev.BatimenTIGER.Service;

import dev.BatimenTIGER.dto.ProjetHomeDTO;
import dev.BatimenTIGER.dto.ProjetRequestDTO;
import dev.BatimenTIGER.dto.ProjetResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProjetService {
    // --- PARTIE CLIENT (PUBLIC) ---
    List<ProjetHomeDTO> getProjetsPourAccueil(); // Rapide, avec photo de couverture
    ProjetResponseDTO getDetailsProjet(Long id); // Détail complet avec galerie et plans
    List<ProjetHomeDTO> filtrerParCategorie(Long categorieId);
    List<ProjetHomeDTO> rechercherProjets(String motCle);

    // --- PARTIE ADMIN (GESTION) ---
    // Utilise MultipartFile pour recevoir les fichiers réels du front-end
    ProjetResponseDTO creerProjet(ProjetRequestDTO request, List<MultipartFile> photos, List<MultipartFile> plans);
    ProjetResponseDTO modifierProjet(Long id, ProjetRequestDTO request);
    ProjetResponseDTO modifierStatut(Long id, String nouveauStatut); // ETUDE -> EN_COURS -> TERMINE
    void supprimerProjet(Long id);

    // Gestion spécifique de la galerie
    void definirPhotoPrincipale(Long projetId, Long photoId);
    void supprimerPhoto(Long photoId);
}
