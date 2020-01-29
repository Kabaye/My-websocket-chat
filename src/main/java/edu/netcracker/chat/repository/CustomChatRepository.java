package edu.netcracker.chat.repository;

import edu.netcracker.chat.entity.SimpleMessage;

import java.util.List;

public interface CustomChatRepository {
    List<SimpleMessage> getMessagesInRange(long lowerBound, long amount);
}
