package edu.netcracker.chat.entity;

import lombok.Data;

@Data
public class OldMessagesRequest {
    private long lowerBound;
    private long amount;
}
