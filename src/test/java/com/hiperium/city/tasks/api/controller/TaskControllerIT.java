package com.hiperium.city.tasks.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerIT extends AbstractContainerBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @Test
    @Order(1)
    @DisplayName("Create Task")
    void givenTaskObject_whenSaveTask_thenReturnSavedTask() throws Exception {
        this.task = Task.builder()
                .name("Test class")
                .description("Task description.")
                .hour(12)
                .minute(0)
                .executionDays("MON,WED,SUN")
                .executionCommand("java -jar test.jar")
                .build();

        ResultActions savedResponse = this.mockMvc.perform(post(TasksUtil.TASKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.task)));
        Task savedTask = this.objectMapper.readValue(savedResponse.andReturn().getResponse().getContentAsString(), Task.class);
        BeanUtils.copyProperties(savedTask, this.task);

        savedResponse.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(0L)))
                .andExpect(jsonPath("$.name", is(this.task.getName())))
                .andExpect(jsonPath("$.description", is(this.task.getDescription())))
                .andExpect(jsonPath("$.hour", is(this.task.getHour())))
                .andExpect(jsonPath("$.minute", is(this.task.getMinute())));
    }

    @Test
    @Order(2)
    @DisplayName("Find Task by ID")
    void givenTaskId_whenFindTaskById_thenReturnTaskObject() throws Exception {
        ResultActions findResponse = this.mockMvc.perform(get(TasksUtil.TASKS_PATH + "/{id}", this.task.getId()));
        findResponse.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(this.task.getName())))
                .andExpect(jsonPath("$.description", is(this.task.getDescription())))
                .andExpect(jsonPath("$.hour", is(this.task.getHour())))
                .andExpect(jsonPath("$.minute", is(this.task.getMinute())));
    }

    @Test
    @Order(3)
    @DisplayName("Find all Tasks")
    void givenTasksList_whenFindAllTasks_thenReturnTasksList() throws Exception {
        ResultActions response = mockMvc.perform(get(TasksUtil.TASKS_PATH));
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    @Order(4)
    @DisplayName("Update Task")
    void givenModifiedTask_whenUpdateTask_thenReturnUpdateTaskObject() throws Exception {
        this.task.setName("Test class updated");
        this.task.setDescription("Task description updated.");
        this.task.setHour(13);
        this.task.setMinute(30);

        ResultActions updatedResponse = mockMvc.perform(put(TasksUtil.TASKS_PATH + "/{id}", this.task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.task)));
        Task updatedTask = this.objectMapper.readValue(updatedResponse.andReturn().getResponse().getContentAsString(), Task.class);
        BeanUtils.copyProperties(updatedTask, this.task);

        updatedResponse.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(this.task.getName())))
                .andExpect(jsonPath("$.description", is(this.task.getDescription())))
                .andExpect(jsonPath("$.hour", is(this.task.getHour())))
                .andExpect(jsonPath("$.minute", is(this.task.getMinute())));
    }

    @Test
    @Order(5)
    @DisplayName("Delete Task")
    void givenTaskId_whenDeleteTask_thenReturnResponse200() throws Exception {
        ResultActions deletedResponse = mockMvc.perform(delete(TasksUtil.TASKS_PATH + "/{id}", this.task.getId()));
        deletedResponse.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(6)
    @DisplayName("Find Task do not exist")
    void givenTaskId_whenFindTaskById_thenReturnError404() throws Exception {
        ResultActions findResponse = this.mockMvc.perform(get(TasksUtil.TASKS_PATH + "/{id}", this.task.getId()));
        findResponse.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(7)
    @DisplayName("Delete not existing Task by ID")
    void givenTaskId_whenDeleteTaskById_thenReturnError404() throws Exception {
        ResultActions deletedResponse = mockMvc.perform(delete(TasksUtil.TASKS_PATH + "/{id}", this.task.getId()));
        deletedResponse.andExpect(status().isNotFound())
                .andDo(print());
    }
}
