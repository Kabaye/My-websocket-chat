package edu.netcracker.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "messages")
public class SimpleMessage {
    @Id
    private String id;
    @JsonProperty("client_nickname")
    private String clientNickname;
    private String content;
    @JsonProperty("message_type")
    private Type messageType;

    public enum Type {
        JOIN, LEAVE, WRITE
    }
}
