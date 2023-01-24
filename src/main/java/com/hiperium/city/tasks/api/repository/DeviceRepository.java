package com.hiperium.city.tasks.api.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.hiperium.city.tasks.api.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRepository.class);

    private final DynamoDBMapper dynamoDBMapper;

    public DeviceRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Device findById(long id) {
        LOGGER.debug("findById(): {}", id);
        return dynamoDBMapper.load(Device.class, id);
    }
}
