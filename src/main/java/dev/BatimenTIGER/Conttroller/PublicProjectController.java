package dev.BatimenTIGER.Conttroller;

import dev.BatimenTIGER.Service.IMessageService;
import dev.BatimenTIGER.Service.IProjetService;
import dev.BatimenTIGER.Service.IServiceOffertService;
import dev.BatimenTIGER.dto.MessageDTO;
import dev.BatimenTIGER.dto.ProjetHomeDTO;
import dev.BatimenTIGER.dto.ProjetResponseDTO;
import dev.BatimenTIGER.dto.ServiceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*") // Permet la connexion avec ton Front-end
public class PublicProjectController {

    private final IProjetService projetService;
    private final IServiceOffertService serviceOffertService;
    private final IMessageService messageService;

    public PublicProjectController(IProjetService projetService,
                                   IServiceOffertService serviceOffertService,
                                   IMessageService messageService) {
        this.projetService = projetService;
        this.serviceOffertService = serviceOffertService;
        this.messageService = messageService;
    }

    @GetMapping("/projets")
    public ResponseEntity<List<ProjetHomeDTO>> getHomeProjects() {
        return ResponseEntity.ok(projetService.getProjetsPourAccueil());
    }

    @GetMapping("/projets/{id}")
    public ResponseEntity<ProjetResponseDTO> getProjectDetails(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.getDetailsProjet(id));
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceDTO>> getPublicServices() {
        return ResponseEntity.ok(serviceOffertService.listerServicesPublics());
    }

    @PostMapping("/contact")
    public ResponseEntity<MessageDTO> postMessage(@RequestBody MessageDTO message) {
        return ResponseEntity.ok(messageService.envoyerMessage(message));
    }
}
