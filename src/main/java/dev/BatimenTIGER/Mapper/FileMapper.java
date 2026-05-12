package dev.BatimenTIGER.Mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Component
public class FileMapper {

    // Génère un nom de fichier unique pour éviter les doublons sur le serveur
    public String generateUniqueFileName(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) return null;
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "_");
    }

    // Construit l'URL publique qui sera stockée en base de données
    public String buildPublicUrl(String dossier, String fileName) {
        return "/api/files/" + dossier + "/" + fileName;
    }
}