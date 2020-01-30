package edu.netcracker.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OldMessagesResponse {
    @JsonProperty("old_messages")
    private List<SimpleMessage> oldMessages;
    @JsonProperty("response_type")
    private ResponseType responseType;
}
