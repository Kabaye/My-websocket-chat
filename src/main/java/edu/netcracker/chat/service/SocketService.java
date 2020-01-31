package edu.netcracker.chat.service;

import edu.netcracker.chat.model.OldMessagesRequest;
import edu.netcracker.chat.model.OldMessagesResponse;
import edu.netcracker.chat.model.ResponseType;
import edu.netcracker.chat.model.SimpleMessage;
import edu.netcracker.chat.repository.ChatRepository;
import edu.netcracker.chat.repository.CustomChatRepositoryImplementation;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class SocketService {
    private final CustomChatRepositoryImplementation customChatRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final Set<String> sessions = new ConcurrentSkipListSet<>();

    public SocketService(CustomChatRepositoryImplementation customChatRepository, ChatRepository chatRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.customChatRepository = customChatRepository;
        this.chatRepository = chatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public SimpleMessage addClient(SimpleMessage simpleMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        if (checkIfNicknameUnique(simpleMessage.getClientNickname()) && Objects.nonNull(simpleMessage.getClientNickname())) {
            sessions.add(simpleMessage.getClientNickname().toLowerCase());
            simpMessageHeaderAccessor.getSessionAttributes().put("clientNickname", simpleMessage.getClientNickname());
        } else {
            throw new RuntimeException("User with such nickname is already in the chat. Use another one.");
        }

        simpMessagingTemplate.convertAndSend("/chat/public/" + simpleMessage.getClientNickname(),
                OldMessagesResponse.builder().responseType(ResponseType.OLD_MESSAGES)
                        .oldMessages(customChatRepository.getMessagesInRange(0, 15))
                        .build());
        return chatRepository.save(simpleMessage.setCurrentTime());
    }

    public SimpleMessage sendMessage(SimpleMessage simpleMessage) {
        simpleMessage.setCurrentTime();
        return chatRepository.save(simpleMessage.setCurrentTime());
    }

    public void getOldMessages(OldMessagesRequest oldMessagesRequest) {
        simpMessagingTemplate.convertAndSend("/chat/public/" + oldMessagesRequest.getClientNickname(),
                OldMessagesResponse.builder().responseType(ResponseType.OLD_MESSAGES)
                        .oldMessages(customChatRepository.getMessagesInRange(oldMessagesRequest.getLowerBound(), oldMessagesRequest.getAmount()))
                        .build());
    }

    public void sendErrorToClient(SimpMessageHeaderAccessor simpMessageHeaderAccessor, Object payload) {
        if (Objects.nonNull(simpMessageHeaderAccessor.getSessionAttributes())) {
            simpMessagingTemplate.convertAndSend("chat/error/" + simpMessageHeaderAccessor.getSessionAttributes().get("clientNickname"), payload);
        }
    }

    public boolean checkIfNicknameUnique(String nickname) {
        return !sessions.contains(nickname.toLowerCase());
    }
}
