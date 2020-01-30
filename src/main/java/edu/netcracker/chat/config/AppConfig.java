package edu.netcracker.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class AppConfig {
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        MongoClientURI uri = new MongoClientURI(
//                "mongodb+srv://dbUser1:Nh2XRSbY2C3RWcSo@kabayedb-gw5zw.gcp.mongodb.net/test?retryWrites=true&w=majority");
//        MongoDbFactory mongoDbFactory = new SimpleMongoClientDbFactory()
//        MongoClient mongoClient = new MongoClientImpl();
//        return new MongoTemplate(mongoClient, "");
//    }

    private final Environment environment;

    @Autowired
    public AppConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void setServerPort() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("CMD", "/C", "SET");
        pb.environment().put("SERVER_PORT", String.valueOf(environment.getProperty("server.port")));
        pb.start();
    }

}
