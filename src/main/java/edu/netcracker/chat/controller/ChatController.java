package edu.netcracker.chat.controller;

import edu.netcracker.chat.model.OldMessagesRequest;
import edu.netcracker.chat.model.ResponseType;
import edu.netcracker.chat.model.SimpleMessage;
import edu.netcracker.chat.model.SimpleMessageResponse;
import edu.netcracker.chat.service.SocketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ChatController {
    private final SocketService socketService;

    public ChatController(SocketService socketService) {
        this.socketService = socketService;
    }

    @MessageMapping("/chat/send_message")
    @SendTo("/chat/public")
    public SimpleMessageResponse sendMessage(@Payload SimpleMessage simpleMessage) {
        return SimpleMessageResponse.builder().responseType(ResponseType.SIMPLE_MESSAGE)
                .simpleMessage(socketService.sendMessage(simpleMessage)).build();
    }

    @MessageMapping("/chat/add_user")
    @SendTo("/chat/public")
    public SimpleMessageResponse addClient(@Payload SimpleMessage simpleMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        return SimpleMessageResponse.builder().responseType(ResponseType.SIMPLE_MESSAGE)
                .simpleMessage(socketService.addClient(simpleMessage, simpMessageHeaderAccessor)).build();
    }

    @MessageMapping("/chat/get_old_messages")
    public void getOldMessages(@Payload OldMessagesRequest oldMessagesRequest) {
        socketService.getOldMessages(oldMessagesRequest);
    }

    @MessageExceptionHandler(Exception.class)
    public void handleException(Exception exc, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        socketService.sendErrorToClient(simpMessageHeaderAccessor, exc.getMessage());
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<Void> checkNickname(@RequestBody String nickname) {
        if (!socketService.checkIfNicknameUnique(nickname)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
