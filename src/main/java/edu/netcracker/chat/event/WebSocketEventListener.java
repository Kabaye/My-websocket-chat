package edu.netcracker.chat.event;

import edu.netcracker.chat.model.ResponseType;
import edu.netcracker.chat.model.SimpleMessage;
import edu.netcracker.chat.model.SimpleMessageResponse;
import edu.netcracker.chat.repository.ChatRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Objects;

@Component
public class WebSocketEventListener {
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketEventListener(ChatRepository chatRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatRepository = chatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Map<String, Object> simpSessionAttributes = StompHeaderAccessor.wrap(event.getMessage()).getSessionAttributes();
        if (Objects.nonNull(simpSessionAttributes)) {
            SimpleMessageResponse simpleMessageResponse = SimpleMessageResponse.builder()
                    .responseType(ResponseType.SIMPLE_MESSAGE)
                    .simpleMessage(chatRepository.save(
                            SimpleMessage.builder()
                                    .messageType(SimpleMessage.Type.LEAVE)
                                    .clientNickname(String.valueOf(simpSessionAttributes.get("clientNickname")))
                                    .build()
                                    .setCurrentTime()))
                    .build();
            simpMessagingTemplate.convertAndSend("/chat/public", simpleMessageResponse);
        } else {
            throw new RuntimeException();
        }
    }
}
