package dev.BatimenTIGER.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    // Retourne l'URL ou le chemin du fichier stocké
    String stockerFichier(MultipartFile file, String dossierDestination);
    void supprimerFichierPhysique(String fileUrl);
    Resource chargerFichier(String nomFichier); // Pour servir les fichiers au front-end
}