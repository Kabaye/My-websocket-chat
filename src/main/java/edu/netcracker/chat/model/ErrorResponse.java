package edu.netcracker.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("response_type")
    private ResponseType responseType;
}