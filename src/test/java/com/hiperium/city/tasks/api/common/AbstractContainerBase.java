package com.hiperium.city.tasks.api.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBase {

    protected static final PostgreSQLContainer POSTGRES_CONTAINER;
    protected static final GenericContainer DYNAMODB_CONTAINER;

    public static final int DYNAMODB_PORT = 8000;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:14.4")
                .withUsername("postgres")
                .withPassword("postgres123")
                .withDatabaseName("HiperiumCityTasksDB");
        POSTGRES_CONTAINER.start();

        DYNAMODB_CONTAINER = new GenericContainer<>("amazon/dynamodb-local:latest")
                .withCommand("-jar DynamoDBLocal.jar -inMemory -sharedDb")
                .withExposedPorts(DYNAMODB_PORT);
        DYNAMODB_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("aws.region", () -> "us-west-2");
        registry.add("aws.dynamodb.endpoint-override", () ->
                "http://localhost:" + DYNAMODB_CONTAINER.getFirstMappedPort());
    }
}
