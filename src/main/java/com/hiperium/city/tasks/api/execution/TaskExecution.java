package com.hiperium.city.tasks.api.execution;

import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.JobsUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskExecution implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecution.class);

    private final TaskRepository taskRepository;

    public TaskExecution(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("execute() - START");
        String jobId = context.getJobDetail().getJobDataMap().getString(JobsUtil.TASK_JOB_ID_DATA_KEY);
        Task task = taskRepository.findByJobId(jobId);
        if (task == null) {
            LOGGER.error("execute() - Task not found with Job ID: {}", jobId);
        } else {
            LOGGER.info("execute() - Task executed successfully: {}", task.getId());
        }
    }
}

