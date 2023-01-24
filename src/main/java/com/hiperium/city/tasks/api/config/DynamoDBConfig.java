package com.hiperium.city.tasks.api.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.dynamodb.endpoint-override}")
    private String dynamoDBEndpoint;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
        if (Objects.nonNull(this.dynamoDBEndpoint) && !dynamoDBEndpoint.isEmpty()) {
            builder.withEndpointConfiguration(new AmazonDynamoDBClientBuilder
                    .EndpointConfiguration(dynamoDBEndpoint, "us-east-1"));
        }
        return builder.build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(this.amazonDynamoDB());
    }

}
