package dev.BatimenTIGER.Service;

import dev.BatimenTIGER.dto.MessageDTO;

import java.util.List;

public interface IMessageService {
    // --- PARTIE CLIENT ---
    MessageDTO envoyerMessage(MessageDTO messageRequest);

    // --- PARTIE ADMIN ---
    List<MessageDTO> listerTousLesMessages();
    List<MessageDTO> listerMessagesNonTraites();
    MessageDTO obtenirMessage(Long id);
    void marquerCommeTraite(Long id);
    void supprimerMessage(Long id);
}
