package dev.BatimenTIGER.Controlleur;

import dev.BatimenTIGER.Service.IFileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileStorageController {

    private final IFileStorageService fileStorageService;

    public FileStorageController(IFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // --- ENREGISTREMENT D'UN FICHIER (ADMIN) ---
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("dossier") String dossier) {
        String fileUrl = fileStorageService.stockerFichier(file, dossier);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
    }

    // --- CONSULTATION / AFFICHAGE DIRECT DANS LE NAVIGATEUR (PUBLIC & ADMIN) ---
    @GetMapping("/{dossier}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String dossier, @PathVariable String filename) {
        String pathToFile = dossier + "/" + filename;
        Resource resource = fileStorageService.chargerFichier(pathToFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.IMAGE_JPEG) // S'adapte globalement aux images standards (.jpg, .png)
                .body(resource);
    }
}
