package dev.BatimenTIGER.Controlleur;

import dev.BatimenTIGER.Service.IMessageService;
import dev.BatimenTIGER.dto.MessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Gardez ceci pour les routes publiques
public class MessageController {

    private final IMessageService messageService;

    public MessageController(IMessageService messageService) {
        this.messageService = messageService;
    }

    // --- ACCÈS PUBLIC ---
    @PostMapping("/public/messages")
    public ResponseEntity<MessageDTO> postMessage(@RequestBody MessageDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.envoyerMessage(dto));
    }

    // --- ACCÈS ADMINISTRATION ---
    // Changez ces mappings pour inclure /admin/ directement ici
    // ou assurez-vous qu'ils sont bien appelés avec /api/admin/messages par le front
    @GetMapping("/admin/messages")
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        return ResponseEntity.ok(messageService.listerTousLesMessages());
    }

    @GetMapping("/admin/messages/non-traites")
    public ResponseEntity<List<MessageDTO>> getUnreadMessages() {
        return ResponseEntity.ok(messageService.listerMessagesNonTraites());
    }

    @GetMapping("/admin/messages/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.obtenirMessage(id));
    }

    @PatchMapping("/admin/messages/{id}/traiter")
    public ResponseEntity<Void> markAsProcessed(@PathVariable Long id) {
        messageService.marquerCommeTraite(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.supprimerMessage(id);
        return ResponseEntity.noContent().build();
    }
}