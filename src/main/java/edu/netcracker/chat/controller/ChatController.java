package edu.netcracker.chat.controller;

import edu.netcracker.chat.model.OldMessagesRequest;
import edu.netcracker.chat.model.ResponseType;
import edu.netcracker.chat.model.SimpleMessage;
import edu.netcracker.chat.model.SimpleMessageResponse;
import edu.netcracker.chat.service.SocketService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final SocketService socketService;
//    private final Environment environment;

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

    @MessageExceptionHandler(Exception.class)
    public void handleException(Exception exc, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        socketService.sendErrorToClient(simpMessageHeaderAccessor, exc.getMessage());
    }
//
//    @RequestMapping("/port")
//    @ResponseBody
//    public String getPort() {
//        return String.valueOf(environment.getProperty("server.port"));
//    }
}
