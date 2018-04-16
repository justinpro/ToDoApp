package com.example.justin.todoapp.addedittask;

import com.example.justin.todoapp.data.Task;
import com.example.justin.todoapp.data.source.TasksDataSource;
import com.example.justin.todoapp.data.source.TasksRepository;

public class AddEditTaskPresenter implements AddEditTaskContract.Presenter,
        TasksDataSource.GetTaskCallback {

    private TasksRepository tasksRepository;

    private AddEditTaskContract.View addTaskView;

    private String taskId;

    public AddEditTaskPresenter(TasksRepository tasksRepository,
                                AddEditTaskContract.View addTaskView,
                                String taskId) {

        this.tasksRepository = tasksRepository;
        this.addTaskView = addTaskView;
        this.taskId = taskId;

        addTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewTask()){
            populateTask();
        }
    }

    @Override
    public void saveTask(String title, String describe) {
        if (isNewTask()) {
            createTask(title, describe);
        } else {
            updateTask(title,describe,taskId);
        }
    }

    @Override
    public void populateTask() {
        tasksRepository.getTask(taskId,this);
    }

    @Override
    public void onTaskLoaded(Task task) {
        if (addTaskView.isActive()){
            addTaskView.setTitle(task.getTitle());
            addTaskView.setDescribe(task.getDescription());
        }
    }

    @Override
    public void onDataNotAvailable() {

    }

    private boolean isNewTask() {
        return taskId == null;
    }

    private void createTask(String title, String describe) {
        Task task = new Task(title, describe);
        if (!task.isEmpty()) {
            tasksRepository.saveTask(task);
            addTaskView.showTaskList();
        }
    }

    private void updateTask(String title, String describe, String taskId) {
        tasksRepository.saveTask(new Task(title, describe, taskId));
        addTaskView.showTaskList();
    }
}
