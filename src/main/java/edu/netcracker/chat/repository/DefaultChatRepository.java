package edu.netcracker.chat.repository;

import edu.netcracker.chat.entity.SimpleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DefaultChatRepository implements CustomChatRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public DefaultChatRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<SimpleMessage> getMessagesInRange(long lowerBound, long amount) {
        long documentAmount = mongoTemplate.count(new Query(), "messages");
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(Aggregation.limit(documentAmount - lowerBound * 10));
        aggregationOperations.add(Aggregation.skip(documentAmount - lowerBound * 10 - amount));
        return mongoTemplate.aggregate(Aggregation.newAggregation(SimpleMessage.class, aggregationOperations),
                SimpleMessage.class, SimpleMessage.class)
                .getMappedResults();
    }
}
