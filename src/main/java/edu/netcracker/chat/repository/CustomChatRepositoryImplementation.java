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
public class CustomChatRepositoryImplementation implements CustomChatRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomChatRepositoryImplementation(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<SimpleMessage> getMessagesInRange(long lowerBound, long amount) {
        long documentAmount = mongoTemplate.count(new Query(), "messages");
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        long limit = documentAmount - lowerBound;
        long skip = documentAmount - lowerBound - amount;
        aggregationOperations.add(Aggregation.limit(limit));
        aggregationOperations.add(Aggregation.skip(skip));
        return mongoTemplate.aggregate(Aggregation.newAggregation(SimpleMessage.class, aggregationOperations),
                SimpleMessage.class, SimpleMessage.class)
                .getMappedResults();
    }
}
