package com.hiperium.city.tasks.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerIT extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @BeforeEach
    void setup() {
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
    @Order(1)
    @DisplayName("Create Task")
    void givenTaskObject_whenSaveTask_thenReturnSavedTask() throws Exception {
        ResultActions response = this.mockMvc.perform(post(TasksUtil.TASKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.task)));
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(this.task.getName())))
                .andExpect(jsonPath("$.description", is(this.task.getDescription())))
                .andExpect(jsonPath("$.hour", is(this.task.getHour())))
                .andExpect(jsonPath("$.minute", is(this.task.getMinute())));
    }

    @Test
    @Order(2)
    @DisplayName("Find Task by ID")
    void givenTaskId_whenFindTaskById_thenReturnTaskObject() throws Exception {
        ResultActions createdResponse = this.mockMvc.perform(post(TasksUtil.TASKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.task)));
        Task createdTask = this.objectMapper.readValue(createdResponse.andReturn().getResponse().getContentAsString(), Task.class);

        ResultActions findResponse = this.mockMvc.perform(get(TasksUtil.TASKS_PATH + "/{id}", createdTask.getId()));
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
        this.mockMvc.perform(post(TasksUtil.TASKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.task)));
        ResultActions response = mockMvc.perform(get(TasksUtil.TASKS_PATH));
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    @Order(4)
    @DisplayName("Update Task")
    void givenModifiedTask_whenUpdateTask_thenReturnUpdateTaskObject() throws Exception {
        ResultActions createdResponse = this.mockMvc.perform(post(TasksUtil.TASKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.task)));

        Task savedTask = this.objectMapper.readValue(createdResponse.andReturn().getResponse().getContentAsString(), Task.class);
        Task modifiedTask = Task.builder().build();
        BeanUtils.copyProperties(savedTask, modifiedTask);
        modifiedTask.setName("Test class updated");
        modifiedTask.setDescription("Task description updated.");
        modifiedTask.setHour(13);
        modifiedTask.setMinute(30);

        ResultActions updatedResponse = mockMvc.perform(put(TasksUtil.TASKS_PATH + "/{id}", modifiedTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifiedTask)));
        updatedResponse.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(modifiedTask.getName())))
                .andExpect(jsonPath("$.description", is(modifiedTask.getDescription())))
                .andExpect(jsonPath("$.hour", is(modifiedTask.getHour())))
                .andExpect(jsonPath("$.minute", is(modifiedTask.getMinute())));
    }

    @Test
    @DisplayName("Delete Task")
    void givenTaskId_whenDeleteTask_thenReturnResponse200() throws Exception {
        ResultActions createdResponse = this.mockMvc.perform(post(TasksUtil.TASKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.task)));
        Task savedTask = this.objectMapper.readValue(createdResponse.andReturn().getResponse().getContentAsString(), Task.class);

        ResultActions deletedResponse = mockMvc.perform(delete(TasksUtil.TASKS_PATH + "/{id}", savedTask.getId()));
        deletedResponse.andExpect(status().isOk())
                .andDo(print());
    }
}
