package com.hiperium.city.tasks.api.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.hiperium.city.tasks.api.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class DeviceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRepository.class);

    private final DynamoDBMapper dynamoDBMapper;

    public DeviceRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Device findById(String id) {
        LOGGER.debug("findById(): {}", id);
        return dynamoDBMapper.load(Device.class, id);
    }

    public void update(Device device) {
        LOGGER.debug("save(): {}", device);
        if (Objects.isNull(device.getId()) || device.getId().isBlank()) {
            throw new IllegalArgumentException("Device ID cannot be null");
        }
        this.dynamoDBMapper.save(device);
    }
}
