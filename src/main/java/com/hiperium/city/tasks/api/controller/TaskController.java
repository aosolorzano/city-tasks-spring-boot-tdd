package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.exception.TaskNotFoundException;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(TasksUtil.TASKS_PATH)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody Task task) {
        return this.taskService.create(task);
    }

    @GetMapping("{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") long taskId) {
        return this.taskService.findById(taskId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    @GetMapping
    public List<Task> getAll() {
        return this.taskService.findAll();
    }

    @PutMapping("{id}")
    public ResponseEntity<Task> update(@PathVariable("id") long taskId, @RequestBody Task modifiedTask) {
        return this.taskService.findById(taskId)
                .map(savedTask -> {
                    BeanUtils.copyProperties(modifiedTask, savedTask);
                    Task updatedTask = this.taskService.update(savedTask);
                    return new ResponseEntity<>(updatedTask, HttpStatus.OK);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long taskId) {
        return this.taskService.findById(taskId)
                .map(savedTask -> {
                    this.taskService.delete(savedTask);
                    return new ResponseEntity<>("Resource deleted successfully.", HttpStatus.OK);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }
}
