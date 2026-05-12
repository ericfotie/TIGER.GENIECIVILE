package dev.BatimenTIGER.Service.ServiceImpl;

import dev.BatimenTIGER.Mapper.FileMapper;
import dev.BatimenTIGER.Service.IFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Transactional
@RequiredArgsConstructor
public class FileStorageServiceImpl implements IFileStorageService {

    private final Path root = Paths.get("uploads");
    private final FileMapper fileMapper; // Injection du mapper

    @Override
    public String stockerFichier(MultipartFile file, String dossier) {
        try {
            if (file.isEmpty()) throw new RuntimeException("Le fichier est vide");

            // 1. Préparer le dossier de destination
            Path folderPath = this.root.resolve(dossier);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // 2. Utiliser le mapper pour générer le nom unique
            String filename = fileMapper.generateUniqueFileName(file);
            Path targetPath = folderPath.resolve(filename);

            // 3. Copier le fichier physiquement
            Files.copy(file.getInputStream(), targetPath);

            // 4. Utiliser le mapper pour retourner l'URL finale
            return fileMapper.buildPublicUrl(dossier, filename);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du stockage : " + e.getMessage());
        }
    }

    @Override
    public void supprimerFichierPhysique(String url) {
        try {
            // On extrait le chemin relatif de l'URL (ex: /api/files/projets/photo.jpg -> projets/photo.jpg)
            String pathInUploads = url.replace("/api/files/", "");
            Path fileToDelete = this.root.resolve(pathInUploads);
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier : " + e.getMessage());
        }
    }

    @Override
    public Resource chargerFichier(String pathToFile) {
        try {
            Path file = root.resolve(pathToFile);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Impossible de lire le fichier");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur URL : " + e.getMessage());
        }
    }
}