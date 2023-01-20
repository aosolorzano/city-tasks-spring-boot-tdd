package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.exception.TaskScheduleException;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.JobsUtil;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
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

    public Task create(Task task) {
        LOGGER.debug("create(): {}", task);
        task.setJobId(TasksUtil.generateJobId());
        try {
            this.createAndScheduleJob(task);
            task.setCreatedAt(ZonedDateTime.now());
            task.setUpdatedAt(ZonedDateTime.now());
            return this.taskRepository.save(task);
        } catch (SchedulerException e) {
            LOGGER.error("Error creating task: {}", e.getMessage());
            throw new TaskScheduleException("Error creating task due to scheduler component error.");
        }
    }

    public Optional<Task> findById(Long id) {
        LOGGER.debug("findById(): {}", id);
        return this.taskRepository.findById(id);
    }

    public List<Task> findAll() {
        return this.taskRepository.findAll();
    }

    public Task update(Task task) {
        LOGGER.debug("update(): {}", task);
        Trigger actualTrigger;
        try {
            actualTrigger = this.getCurrentTrigger(task);
            if (Objects.isNull(actualTrigger)) {
                LOGGER.warn("Task Trigger not found. Creating a new one for Task ID: {}", task.getId());
                this.createAndScheduleJob(task);
            } else {
                LOGGER.debug("Actual trigger to update: {}", actualTrigger);
                Trigger newTrigger = JobsUtil.createCronTriggerFromTask(task, this.zoneId);
                Date newTriggerFirstFire = this.scheduler.rescheduleJob(actualTrigger.getKey(), newTrigger);
                if (Objects.isNull(newTriggerFirstFire)) {
                    LOGGER.error("Cannot reschedule the Trigger for the Task ID: {}", task.getId());
                } else {
                    LOGGER.info("Successfully rescheduled trigger for Task ID: {}", task.getId());
                }
            }
            task.setUpdatedAt(ZonedDateTime.now());
            return this.taskRepository.save(task);
        } catch (SchedulerException e) {
            LOGGER.error("Error updating task: {}", e.getMessage());
            throw new TaskScheduleException("Error updating task due to scheduler component error.");
        }
    }

    public void delete(Task task) {
        LOGGER.debug("delete(): {}", task);
        Trigger actualTrigger;
        try {
            actualTrigger = this.getCurrentTrigger(task);
            if (Objects.isNull(actualTrigger)) {
                LOGGER.warn("Task Trigger not found. Nothing to delete for Task ID: {}", task.getId());
            } else {
                LOGGER.debug("Actual trigger to delete: {}", actualTrigger);
                this.scheduler.unscheduleJob(actualTrigger.getKey());
                LOGGER.info("Successfully unscheduled trigger for Task ID: {}", task.getId());
            }
            this.taskRepository.delete(task);
        } catch (SchedulerException e) {
            LOGGER.error("Error deleting task: {}", e.getMessage());
            throw new TaskScheduleException("Error deleting task due to scheduler component error.");
        }
    }


    private void createAndScheduleJob(Task task) throws SchedulerException {
        LOGGER.debug("createAndScheduleJob() - BEGIN: {}", task.getName());
        JobDetail job = JobsUtil.createJobDetailFromTask(task);
        Trigger trigger = JobsUtil.createCronTriggerFromTask(task, this.zoneId);
        // TODO: Fix error SchedulerException: Based on configured schedule, the given trigger 'Task#...' will never fire.
        this.scheduler.scheduleJob(job, trigger);
        LOGGER.debug("createAndScheduleJob() - END: {}", task.getJobId());
    }

    private Trigger getCurrentTrigger(Task task) throws SchedulerException {
        LOGGER.debug("getCurrentTrigger() - BEGIN: {}", task.getJobId());
        Trigger trigger = null;
        for (JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JobsUtil.TASK_GROUP_NAME))) {
            LOGGER.debug("Existing JobKey found: {}", jobKey);
            if (jobKey.getName().equals(task.getJobId())) {
                TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobId(), JobsUtil.TASK_GROUP_NAME);
                LOGGER.debug("Existing TriggerKey found: {}", triggerKey);
                trigger = this.scheduler.getTrigger(triggerKey);
            }
        }
        LOGGER.debug("getTrigger() - END");
        return trigger;
    }

}
