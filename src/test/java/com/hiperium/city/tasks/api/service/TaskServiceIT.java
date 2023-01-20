package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.common.PostgresTestContainerBase;
import com.hiperium.city.tasks.api.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class TaskServiceIT extends PostgresTestContainerBase {

    @Autowired
    private TaskService taskService;

    private Task task;

    @BeforeEach
    public void setup() {
        this.task = Task.builder()
                .name("Test class")
                .description("Task description.")
                .hour(12)
                .minute(0)
                .executionDays("MON,WED,SUN")
                .executionCommand("java -jar test.jar")
                .build();
    }

    @Test
    @DisplayName("Create Task and Schedule a Job")
    void givenTaskObject_whenSave_thenReturnSavedTask() throws SchedulerException {
        Task savedTask = this.taskService.create(this.task);
        assertThat(savedTask.getId()).isPositive();
    }

    @Test
    @DisplayName("Find Task by ID")
    void givenTaskObject_whenFindById_thenReturnTask() throws SchedulerException {
        Task savedTask = this.taskService.create(this.task);
        Optional<Task> task = this.taskService.findById(savedTask.getId());
        assertThat(task).isPresent();
        assertThat(task.get().getId()).isEqualTo(savedTask.getId());
    }

    @Test
    @DisplayName("Update Task and Scheduled Job")
    void givenTaskObject_whenUpdate_thenReturnUpdatedTask() throws SchedulerException {
        Task savedTask = this.taskService.create(this.task);
        savedTask.setName("Updated task");
        savedTask.setHour(13);
        Task updatedTask = this.taskService.update(savedTask);
        assertThat(updatedTask.getName()).isEqualTo("Updated task");
        assertThat(updatedTask.getHour()).isEqualTo(13);
    }

    @Test
    @DisplayName("Delete Task and Scheduled Job")
    void givenTaskId_whenDelete_thenDeleteTaskObject() throws SchedulerException {
        Task savedTask = this.taskService.create(this.task);
        this.taskService.delete(savedTask);
        Optional<Task> task = this.taskService.findById(savedTask.getId());
        assertThat(task).isEmpty();
    }
}
