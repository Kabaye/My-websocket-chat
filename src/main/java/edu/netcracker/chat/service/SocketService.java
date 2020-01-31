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

//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye1").messageType(SimpleMessage.Type.JOIN).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye1").content("Hiiii!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye2").messageType(SimpleMessage.Type.JOIN).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye2").content("Hello there!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye2").content("Who's here??").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye1").content("I am!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye2").content("Oh, nice to meet you, Kabaye1!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye1").content("Nice to meet you too, Kabaye2!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye3").messageType(SimpleMessage.Type.JOIN).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye2").content("Hello, Kabaye3!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye1").content("Hello!!!!!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());
//        chatRepository.save(SimpleMessage.builder().clientNickname("Kabaye3").content("Hi to everybody!").messageType(SimpleMessage.Type.WRITE).build().setCurrentTime());

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

    public void sendErrorToClient(SimpMessageHeaderAccessor simpMessageHeaderAccessor, Object payload) {
        if (Objects.nonNull(simpMessageHeaderAccessor.getSessionAttributes())) {
            simpMessagingTemplate.convertAndSend("chat/error/" + simpMessageHeaderAccessor.getSessionAttributes().get("clientNickname"), payload);
        }
    }
}
