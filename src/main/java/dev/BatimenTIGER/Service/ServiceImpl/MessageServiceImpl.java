package dev.BatimenTIGER.Service.ServiceImpl;

import dev.BatimenTIGER.Mapper.MessageMapper;
import dev.BatimenTIGER.Model.Message;
import dev.BatimenTIGER.Repository.MessageRepository;
import dev.BatimenTIGER.Service.IMessageService;
import dev.BatimenTIGER.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper mapper;

    @Override
    public MessageDTO envoyerMessage(MessageDTO dto) {
        Message m = mapper.toEntity(dto);
        return mapper.toDTO(messageRepository.save(m));
    }

    @Override
    public List<MessageDTO> listerTousLesMessages() {
        return messageRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<MessageDTO> listerMessagesNonTraites() {
        return messageRepository.findByEstTraiteFalse().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public MessageDTO obtenirMessage(Long id) {
        return messageRepository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));
    }

    @Override
    public void marquerCommeTraite(Long id) {
        Message m = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));
        m.setEstTraite(true);
        messageRepository.save(m);
    }

    @Override
    public void supprimerMessage(Long id) {
        messageRepository.deleteById(id);
    }
}