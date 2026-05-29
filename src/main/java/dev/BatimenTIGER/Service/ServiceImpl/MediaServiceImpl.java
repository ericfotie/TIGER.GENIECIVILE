package dev.BatimenTIGER.Service.ServiceImpl;

import dev.BatimenTIGER.Model.Photo;
import dev.BatimenTIGER.Repository.PhotoRepository;
import dev.BatimenTIGER.Service.IMediaService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MediaServiceImpl implements IMediaService {

    private final PhotoRepository photoRepository;
    private final Path fileStorageLocation;

    public MediaServiceImpl(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
        // Définit le dossier 'uploads' à la racine de l'application
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    }

    @Override
    public Resource loadFileAsResource(Long photoId) {
        try {
            Photo photo = photoRepository.findById(photoId)
                    .orElseThrow(() -> new RuntimeException("Photo non trouvée pour l'ID : " + photoId));

            String url = photo.getUrl(); // ex: "/api/files/projets/photos/nom.jpg"

            // 1. Nettoyage strict : on enlève le préfixe et le slash initial
            String cheminRelatif = url.replace("/api/files/", "");
            if (cheminRelatif.startsWith("/")) {
                cheminRelatif = cheminRelatif.substring(1);
            }

            // 2. Résolution du chemin complet sur le disque
            Path filePath = this.fileStorageLocation.resolve(cheminRelatif).normalize();

            // 3. Sécurité : vérifier que le fichier est bien DANS le dossier uploads
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new SecurityException("Accès non autorisé : tentative d'accès hors du dossier uploads.");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Log utile pour déboguer : affiche le chemin exact attendu
                System.err.println("Fichier introuvable au chemin : " + filePath.toAbsolutePath());
                throw new RuntimeException("Fichier introuvable sur le disque.");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", ex);
        }
    }
}