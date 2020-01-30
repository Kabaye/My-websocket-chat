package edu.netcracker.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleMessageResponse {
    @JsonProperty("simple_message")
    private SimpleMessage simpleMessage;
    @JsonProperty("response_type")
    private ResponseType responseType;
}
