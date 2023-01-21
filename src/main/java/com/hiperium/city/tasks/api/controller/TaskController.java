package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.exception.TaskNotFoundException;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(TasksUtil.TASKS_PATH)
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
    public static final String TASK_NOT_FOUND_WITH_ID = "Task not found with ID: ";

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody Task task) {
        LOGGER.debug("create(): {}", task);
        return this.taskService.create(task);
    }

    @GetMapping("{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") long taskId) {
        LOGGER.debug("getById(): {}", taskId);
        return this.taskService.findById(taskId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_WITH_ID + taskId));
    }

    @GetMapping
    public List<Task> getAll() {
        LOGGER.debug("getAll() - START");
        return this.taskService.findAll();
    }

    @PutMapping("{id}")
    public ResponseEntity<Task> update(@PathVariable("id") long taskId,
                                       @RequestBody Task modifiedTask) {
        LOGGER.debug("update(): {} - {}", taskId, modifiedTask);
        return this.taskService.findById(taskId)
                .map(savedTask -> {
                    BeanUtils.copyProperties(modifiedTask, savedTask);
                    Task updatedTask = this.taskService.update(savedTask);
                    return new ResponseEntity<>(updatedTask, HttpStatus.OK);
                })
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_WITH_ID + taskId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long taskId) {
        LOGGER.debug("delete(): {}", taskId);
        return this.taskService.findById(taskId)
                .map(savedTask -> {
                    this.taskService.delete(savedTask);
                    return new ResponseEntity<>("Resource deleted successfully.", HttpStatus.OK);
                })
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_WITH_ID + taskId));
    }
}
