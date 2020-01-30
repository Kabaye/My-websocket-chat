package edu.netcracker.chat.controller;

import edu.netcracker.chat.entity.OldMessagesRequest;
import edu.netcracker.chat.entity.SimpleMessage;
import edu.netcracker.chat.repository.ChatRepository;
import edu.netcracker.chat.repository.CustomChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

@Controller
public class ChatController {
    private final ChatRepository chatRepository;
    private final CustomChatRepository customChatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatController(ChatRepository chatRepository, CustomChatRepository customChatRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatRepository = chatRepository;
        this.customChatRepository = customChatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat/send-message")
    @SendTo("/chat/public")
    public SimpleMessage sendMessage(@Payload SimpleMessage simpleMessage) {
        return chatRepository.save(simpleMessage);
    }

    @MessageMapping("/chat/add-user")
    @SendTo("/chat/public")
    public SimpleMessage addUser(@Payload SimpleMessage simpleMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        String clientNickname = (String) simpMessageHeaderAccessor.getSessionAttributes().get("clientNickname");
        if (Objects.isNull(clientNickname) && Objects.nonNull(simpleMessage.getClientNickname())) {
            simpMessageHeaderAccessor.getSessionAttributes().put("clientNickname", simpleMessage.getClientNickname());
        } else {
            throw new RuntimeException("User with such nickname is already in chat. Use another one.");
        }
        simpMessagingTemplate.convertAndSendToUser(clientNickname, "/chat/public/" + clientNickname, customChatRepository.getMessagesInRange(0, 10));
        return chatRepository.save(simpleMessage);

    }

    @MessageMapping("/chat/get-old-messages")
    public List<SimpleMessage> getOldMessages(@Payload OldMessagesRequest oldMessagesRequest) {
        return customChatRepository.getMessagesInRange(oldMessagesRequest.getLowerBound(), oldMessagesRequest.getAmount());
    }
}
