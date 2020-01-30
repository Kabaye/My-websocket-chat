package edu.netcracker.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OldMessagesRequest {
    @JsonProperty("lower_bound")
    private long lowerBound;
    private long amount;
    @JsonProperty("client_nickname")
    private String clientNickname;
}
