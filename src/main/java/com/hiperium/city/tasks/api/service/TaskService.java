package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.JobUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Value("${tasks.time.zone.id}")
    private String zoneId;

    private final Scheduler scheduler;
    private final TaskRepository taskRepository;

    public TaskService(Scheduler scheduler, TaskRepository taskRepository) {
        this.scheduler = scheduler;
        this.taskRepository = taskRepository;
    }

    public Task create(Task task) throws SchedulerException {
        LOGGER.debug("create(): {}", task);
        task.setJobId(TaskUtil.generateJobId(30));
        this.createAndScheduleJob(task);
        task.setId(1L);
        task.setCreatedAt(ZonedDateTime.now());
        task.setUpdatedAt(ZonedDateTime.now());
        return this.taskRepository.save(task);
    }

    public Optional<Task> findById(Long id) {
        LOGGER.debug("findById(): {}", id);
        return this.taskRepository.findById(id);
    }


    public Task update(Task task) throws SchedulerException {
        Trigger actualTrigger = this.getCurrentTrigger(task);
        if (Objects.isNull(actualTrigger)) {
            LOGGER.warn("Task Trigger not found. Creating a new one for Task ID: {}", task.getId());
            this.createAndScheduleJob(task);
        } else {
            LOGGER.debug("Actual trigger to update: {}", actualTrigger);
            Trigger newTrigger = JobUtil.createCronTriggerFromTask(task, this.zoneId);
            Date newTriggerFirstFire = this.scheduler.rescheduleJob(actualTrigger.getKey(), newTrigger);
            if (Objects.isNull(newTriggerFirstFire)) {
                LOGGER.error("Cannot reschedule the Trigger for the Task ID: {}", task.getId());
            } else {
                LOGGER.info("Successfully rescheduled trigger for Task ID: {}", task.getId());
            }
        }
        task.setUpdatedAt(ZonedDateTime.now());
        return this.taskRepository.save(task);
    }

    public void delete(Task task) throws SchedulerException {
        Trigger actualTrigger = this.getCurrentTrigger(task);
        if (Objects.isNull(actualTrigger)) {
            LOGGER.warn("Task Trigger not found. Nothing to delete for Task ID: {}", task.getId());
        } else {
            LOGGER.debug("Actual trigger to delete: {}", actualTrigger);
            this.scheduler.unscheduleJob(actualTrigger.getKey());
            LOGGER.info("Successfully unscheduled trigger for Task ID: {}", task.getId());
        }
        this.taskRepository.delete(task);
    }


    private void createAndScheduleJob(Task task) throws SchedulerException {
        LOGGER.debug("createAndScheduleJob() - BEGIN: {}", task.getName());
        JobDetail job = JobUtil.createJobDetailFromTask(task);
        Trigger trigger = JobUtil.createCronTriggerFromTask(task, this.zoneId);
        // TODO: Fix error SchedulerException: Based on configured schedule, the given trigger 'Task#...' will never fire.
        this.scheduler.scheduleJob(job, trigger);
        LOGGER.debug("createAndScheduleJob() - END: {}", task.getJobId());
    }

    private Trigger getCurrentTrigger(Task task) throws SchedulerException {
        LOGGER.debug("getCurrentTrigger() - BEGIN: {}", task.getJobId());
        Trigger trigger = null;
        for (JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JobUtil.TASK_GROUP_NAME))) {
            LOGGER.debug("Existing JobKey found: {}", jobKey);
            if (jobKey.getName().equals(task.getJobId())) {
                TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobId(), JobUtil.TASK_GROUP_NAME);
                LOGGER.debug("Existing TriggerKey found: {}", triggerKey);
                trigger = this.scheduler.getTrigger(triggerKey);
            }
        }
        LOGGER.debug("getTrigger() - END");
        return trigger;
    }

}
