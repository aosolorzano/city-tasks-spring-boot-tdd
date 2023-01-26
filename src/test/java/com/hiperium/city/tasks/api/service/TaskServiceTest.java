package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.model.Task;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskServiceTest extends AbstractContainerBase {

    private static final String DEVICE_ID = "1";

    @Autowired
    private TaskService taskService;

    private Task task;

    @Test
    @Order(1)
    @DisplayName("Create Task and Schedule a Job")
    void givenTaskObject_whenSave_thenReturnSavedTask() {
        this.task = Task.builder()
                .name("Test class")
                .description("Task description.")
                .hour(12)
                .minute(0)
                .executionDays("MON,WED,SUN")
                .executionCommand("java -jar test.jar")
                .deviceId(DEVICE_ID)
                .deviceAction("ACTIVATE")
                .build();
        Task savedTask = this.taskService.create(this.task);
        BeanUtils.copyProperties(savedTask, this.task);
        assertThat(this.task.getId()).isPositive();
    }

    @Test
    @Order(2)
    @DisplayName("Find Task by ID")
    void givenTaskObject_whenFindById_thenReturnTask() {
        Optional<Task> task = this.taskService.findById(this.task.getId());
        assertThat(task).isPresent();
    }

    @Test
    @Order(3)
    @DisplayName("Update Task and Job")
    void givenTaskObject_whenUpdate_thenReturnUpdatedTask() {
        this.task.setName("Updated task");
        this.task.setHour(13);
        Task updatedTask = this.taskService.update(this.task);
        BeanUtils.copyProperties(updatedTask, this.task);
        assertThat(this.task.getName()).isEqualTo("Updated task");
        assertThat(this.task.getHour()).isEqualTo(13);
    }

    @Test
    @Order(4)
    @DisplayName("Delete Task and Job")
    void givenTaskId_whenDelete_thenDeleteTaskObject() {
        this.taskService.delete(this.task);
        Optional<Task> task = this.taskService.findById(this.task.getId());
        assertThat(task).isEmpty();
    }
}
