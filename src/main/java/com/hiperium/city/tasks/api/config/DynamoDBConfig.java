package com.hiperium.city.tasks.api.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DynamoDBConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBConfig.class);

    @Value("${aws.dynamodb.endpoint-override}")
    private String dynamoDBEndpoint;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        LOGGER.info("AWS region: {}", this.region);
        LOGGER.info("DynamoDB endpoint: {}", this.dynamoDBEndpoint);
        if (Objects.nonNull(this.dynamoDBEndpoint) && !dynamoDBEndpoint.isBlank()) {
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AmazonDynamoDBClientBuilder
                    .EndpointConfiguration(this.dynamoDBEndpoint, this.region))
                    .build();
        }
        return AmazonDynamoDBClientBuilder.defaultClient();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(this.amazonDynamoDB());
    }

}
