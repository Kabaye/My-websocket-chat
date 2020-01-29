package edu.netcracker.chat.controller;

import edu.netcracker.chat.entity.OldMessagesRequest;
import edu.netcracker.chat.entity.SimpleMessage;
import edu.netcracker.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

@Controller
public class ChatController {
    private final ChatRepository chatRepository;

    @Autowired
    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @MessageMapping("/chat/send-message")
    @SendTo("/chat/public")
    public SimpleMessage sendMessage(@Payload SimpleMessage simpleMessage) {
        return chatRepository.save(simpleMessage);
    }

    @MessageMapping("/chat/add-user")
    @SendTo("/chat/public")
    public List<SimpleMessage> addUser(@Payload SimpleMessage simpleMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        if (Objects.isNull(simpMessageHeaderAccessor.getSessionAttributes().get("clientNickname"))) {
            simpMessageHeaderAccessor.getSessionAttributes().put("clientNickname", simpleMessage.getClientNickname());
        } else {
            throw new RuntimeException("User with such nickname is already in chat. Use another one.");
        }
        chatRepository.save(simpleMessage);
        return chatRepository.getMessagesInRange(0, 10);

    }

    @MessageMapping("/chat/get-old-messages")
    @SendTo("/chat/public/{}")
    public List<SimpleMessage> getOldMessages(@Payload OldMessagesRequest oldMessagesRequest) {
        return chatRepository.getMessagesInRange(oldMessagesRequest.getLowerBound(), oldMessagesRequest.getAmount());
    }
}
