package edu.netcracker.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@Document(collection = "messages")
public class SimpleMessage {
    @Id
    private String id;
    @JsonProperty("client_nickname")
    private String clientNickname;
    private String content;
    @JsonProperty("message_type")
    private Type messageType;
    @Setter(AccessLevel.PRIVATE)
    private String time;

    public SimpleMessage setCurrentTime() {
        time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        return this;
    }

    public enum Type {
        JOIN, LEAVE, WRITE
    }
}
