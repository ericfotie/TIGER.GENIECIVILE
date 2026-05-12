package dev.BatimenTIGER.Mapper;

import dev.BatimenTIGER.Model.Message;
import dev.BatimenTIGER.dto.MessageDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Component
public class MessageMapper {

    public MessageDTO toDTO(Message m) {
        if (m == null) return null;

        // Génération du lien WhatsApp pour faciliter la réponse de l'admin
        String text = "Bonjour " + m.getNomClient() + ", concernant votre message : " + m.getSujet();
        String whatsappLink = "https://wa.me/" +
                m.getTelephone().replaceAll("[^0-9]", "") +
                "?text=" + UriUtils.encode(text, StandardCharsets.UTF_8);

        return new MessageDTO(
                m.getId(),
                m.getNomClient(),
                m.getEmail(),
                m.getTelephone(),
                m.getSujet(),
                m.getContenu(),
                m.getDateReception(),
                m.isEstTraite(),
                whatsappLink
        );
    }

    public Message toEntity(MessageDTO dto) {
        if (dto == null) return null;

        Message m = new Message();
        m.setNomClient(dto.nomClient());
        m.setEmail(dto.email());
        m.setTelephone(dto.telephone());
        m.setSujet(dto.sujet());
        m.setContenu(dto.contenu());
        // La date est souvent gérée par @PrePersist dans le modèle
        return m;
    }

    public void updateEntityFromDTO(MessageDTO dto, Message m) {
        if (dto == null || m == null) return;
        m.setEstTraite(dto.estTraite()); // Principalement utilisé pour marquer comme lu
    }
}