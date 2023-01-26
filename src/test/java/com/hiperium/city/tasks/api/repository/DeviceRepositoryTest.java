package com.hiperium.city.tasks.api.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.model.Device;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceRepositoryTest extends AbstractContainerBase {

    private static final String DEVICE_ID = "1";

    @Autowired
    private AmazonDynamoDB client;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    @Order(1)
    @DisplayName("Create Devices Table")
    void givenDynamoDBClient_whenCreateTable_mustCreateTable() {
        CreateTableRequest request = new CreateTableRequest()
                .withTableName("Devices")
                .withKeySchema(new KeySchemaElement("id", KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition("id", ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        CreateTableResult result = this.client.createTable(request);
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(2)
    @DisplayName("Save Device Item")
    void givenDeviceObject_whenSave_mustSaveDeviceItem() {
        Device device = Device.builder()
                .id(DEVICE_ID)
                .name("Device 1")
                .description("Device 1 description")
                .status("Active")
                .build();
        this.dynamoDBMapper.save(device);
        Assertions.assertNotNull(this.dynamoDBMapper.load(Device.class, device.getId()));
    }

    @Test
    @Order(3)
    @DisplayName("Find Device by ID")
    void givenDeviceId_whenFindById_mustReturnDevice() {
        Device device = this.deviceRepository.findById(DEVICE_ID);
        Assertions.assertNotNull(device);
    }

    @Test
    @Order(4)
    @DisplayName("Update Device Item")
    void givenDeviceItem_whenUpdate_mustUpdateDeviceItem() {
        Device device = this.deviceRepository.findById(DEVICE_ID);
        device.setName("Device 1 Updated");
        device.setDescription("Device 1 description Updated");
        device.setStatus("Inactive");
        this.deviceRepository.update(device);
        Device updatedDevice = this.deviceRepository.findById(DEVICE_ID);
        Assertions.assertEquals(device.getName(), updatedDevice.getName());
        Assertions.assertEquals(device.getDescription(), updatedDevice.getDescription());
        Assertions.assertEquals(device.getStatus(), updatedDevice.getStatus());
    }
}
