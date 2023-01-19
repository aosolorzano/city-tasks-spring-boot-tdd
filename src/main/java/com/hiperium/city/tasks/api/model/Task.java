package com.hiperium.city.tasks.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HIP_CTY_TASKS")
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", length = 31, nullable = false)
    private String name;

    @Column(name = "job_id", length = 31, nullable = false)
    private String jobId;

    @Column(name = "task_hour", nullable = false)
    private Integer hour;

    @Column(name = "task_minute", nullable = false)
    private Integer minute;

    @Column(name = "execution_days", length = 127, nullable = false)
    private String executionDays;

    @Column(name = "execution_command", nullable = false)
    private String executionCommand;

    @Column(name = "execute_until")
    private ZonedDateTime executeUntil;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
