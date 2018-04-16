package com.example.justin.todoapp.tasks;

public enum TasksFilterType {
    /**
     * 不过滤任务
     */
    ALL_TASKS,
    /**
     * 只过滤active的任务
     */
    ACTIVE_TASKS,
    /**
     * 过滤completed的任务
     */
    COMPLETED_TASKS
}
