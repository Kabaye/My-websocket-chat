package edu.netcracker.chat.controller;

import edu.netcracker.chat.entity.OldMessagesRequest;
import edu.netcracker.chat.entity.ResponseType;
import edu.netcracker.chat.entity.SimpleMessage;
import edu.netcracker.chat.entity.SimpleMessageResponse;
import edu.netcracker.chat.service.SocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final SocketService socketService;

    public ChatController(SocketService socketService) {
        this.socketService = socketService;
    }

    @MessageMapping("/chat/send_message")
    @SendTo("/chat/public")
    public SimpleMessageResponse sendMessage(@Payload SimpleMessage simpleMessage) {
        return SimpleMessageResponse.builder().responseType(ResponseType.SIMPLE_MESSAGE).simpleMessage(socketService.sendMessage(simpleMessage)).build();
    }

    @MessageMapping("/chat/add_user")
    @SendTo("/chat/public")
    public SimpleMessageResponse addClient(@Payload SimpleMessage simpleMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        return SimpleMessageResponse.builder().responseType(ResponseType.SIMPLE_MESSAGE).simpleMessage(socketService.addClient(simpleMessage, simpMessageHeaderAccessor)).build();
    }

    @MessageMapping("/chat/get_old_messages")
    public void getOldMessages(@Payload OldMessagesRequest oldMessagesRequest) {
        socketService.getOldMessages(oldMessagesRequest);
    }
}
