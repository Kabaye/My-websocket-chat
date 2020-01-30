package edu.netcracker.chat.service;

import edu.netcracker.chat.entity.OldMessagesRequest;
import edu.netcracker.chat.entity.OldMessagesResponse;
import edu.netcracker.chat.entity.ResponseType;
import edu.netcracker.chat.entity.SimpleMessage;
import edu.netcracker.chat.repository.ChatRepository;
import edu.netcracker.chat.repository.CustomChatRepositoryImplementation;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SocketService {
    private final CustomChatRepositoryImplementation customChatRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public SocketService(CustomChatRepositoryImplementation customChatRepository, ChatRepository chatRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.customChatRepository = customChatRepository;
        this.chatRepository = chatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public SimpleMessage addClient(SimpleMessage simpleMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        String clientNickname = (String) simpMessageHeaderAccessor.getSessionAttributes().get("clientNickname");
        if (Objects.isNull(clientNickname) && Objects.nonNull(simpleMessage.getClientNickname())) {
            simpMessageHeaderAccessor.getSessionAttributes().put("clientNickname", simpleMessage.getClientNickname());
        } else {
            throw new RuntimeException("User with such nickname is already in the chat. Use another one.");
        }

//        chatRepository.save(SimpleMessage.builder().clientNickname("Darkss").messageType(SimpleMessage.Type.JOIN).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Darkss").content("10 Hiiii!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye").messageType(SimpleMessage.Type.JOIN).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye").content("11 Hello there!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye").content("12 Who's here??").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Darkss").content("13 I am!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye").content("14 Oh, nice to meet you, Darkss!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());

        simpMessagingTemplate.convertAndSend("/chat/public/" + simpleMessage.getClientNickname(),
                OldMessagesResponse.builder().responseType(ResponseType.OLD_MESSAGES)
                        .oldMessages(customChatRepository.getMessagesInRange(0, 10))
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
}
