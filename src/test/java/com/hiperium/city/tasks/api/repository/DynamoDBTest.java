package com.hiperium.city.tasks.api.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.hiperium.city.tasks.api.common.DynamoDBContainerBase;
import com.hiperium.city.tasks.api.model.Device;
import org.junit.jupiter.api.*;

import java.util.Objects;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DynamoDBTest extends DynamoDBContainerBase {

    private static final String DEVICE_ID = "1";
    private static AmazonDynamoDB client;
    private static DynamoDBMapper dynamoDBMapper;

    @BeforeAll
    static void init() {
        var containerEndpoint = String.format("http://localhost:%d", DYNAMODB_CONTAINER.getFirstMappedPort());
        client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(containerEndpoint, Regions.US_EAST_1.getName()))
                .build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }

    @Test
    @Order(1)
    @DisplayName("Create Table")
    void givenDynamoDBClient_whenCreateTable_mustCreateTable() {
        CreateTableRequest request = new CreateTableRequest()
                .withTableName("Devices")
                .withKeySchema(new KeySchemaElement("Id", KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition("Id", ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        CreateTableResult result = client.createTable(request);
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(2)
    @DisplayName("Save Item")
    void givenDeviceObject_whenSave_mustSaveDeviceItem() {
        Device device = Device.builder()
                .id(DEVICE_ID)
                .name("Device 1")
                .description("Device 1 description")
                .status("Active")
                .build();
        dynamoDBMapper.save(device);
        Assertions.assertNotNull(dynamoDBMapper.load(Device.class, device.getId()));
    }

    @Test
    @Order(3)
    @DisplayName("Update Item")
    void givenDeviceItem_whenUpdate_mustUpdateDeviceItem() {
        Device device = dynamoDBMapper.load(Device.class, DEVICE_ID);
        if (Objects.isNull(device)) {
            Assertions.fail("Device not found");
        }
        device.setStatus("Inactive");
        dynamoDBMapper.save(device);
        Assertions.assertEquals("Inactive", dynamoDBMapper.load(Device.class, device.getId()).getStatus());
    }

}
