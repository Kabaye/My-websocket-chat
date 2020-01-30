package edu.netcracker.chat.repository;

import edu.netcracker.chat.entity.SimpleMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<SimpleMessage, String> {
}
