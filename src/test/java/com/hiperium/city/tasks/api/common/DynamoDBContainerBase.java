package com.hiperium.city.tasks.api.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class DynamoDBContainerBase {

    protected static final GenericContainer DYNAMODB_CONTAINER;

    public static final int DYNAMODB_PORT = 8000;

    static {
        DYNAMODB_CONTAINER = new GenericContainer<>("amazon/dynamodb-local:latest")
                .withCommand("-jar DynamoDBLocal.jar -inMemory -sharedDb")
                .withExposedPorts(DYNAMODB_PORT);
        DYNAMODB_CONTAINER.start();
    }
}
