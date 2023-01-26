package com.hiperium.city.tasks.api.job;

import com.hiperium.city.tasks.api.exception.TaskNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.DeviceRepository;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.JobsUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TaskJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskJob.class);

    private final TaskRepository taskRepository;
    private final DeviceRepository deviceRepository;

    public TaskJob(TaskRepository taskRepository, DeviceRepository deviceRepository) {
        this.taskRepository = taskRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void execute(JobExecutionContext context) {
        LOGGER.debug("execute() - START");
        String jobId = context.getJobDetail().getJobDataMap().getString(JobsUtil.TASK_JOB_ID_DATA_KEY);
        Task task = taskRepository.findByJobId(jobId);
        if (Objects.isNull(task)) {
            LOGGER.error("execute() - Task not found with Job ID: {}", jobId);
            throw new TaskNotFoundException("Task not found with Job ID: " + jobId);
        }
        Device device = deviceRepository.findById(task.getDeviceId());
        if ("ACTIVATE".equals(task.getDeviceAction())) {
            device.setStatus("ON");
        } else {
            device.setStatus("OFF");
        }
        deviceRepository.update(device);
        LOGGER.info("execute() - Task {} executed successfully.", task.getId());
    }
}

