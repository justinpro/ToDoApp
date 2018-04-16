package com.example.justin.todoapp.data.source.remote;

import android.os.Handler;
import android.util.Log;

import com.example.justin.todoapp.data.Task;
import com.example.justin.todoapp.data.source.TasksDataSource;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TasksRemoteDataSource implements TasksDataSource {
    private static final String TAG = "shit";

    private static TasksRemoteDataSource Instance = null;

    private static final int SERVICE_LATENCY_IN_MILLIS = 1000;

    private static Map<String, Task> TASKS_SERVICE_DATA;

    public static TasksRemoteDataSource getInstance() {
        if (Instance == null){
            Instance = new TasksRemoteDataSource();
        }
        return Instance;
    }

    /**
     * 初始数据
     */
    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("test1", "fuck");
        addTask("test2", "noob");
    }

    private static void addTask(String title, String description) {
        Log.i(TAG, " 远程添加数据 addTask: ");
        Task newTask = new Task(title, description);
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    /**
     * 注意：{@link LoadTasksCallback 的 onDataNotAvailable（）}永远不会被触发。
     * 在真正的远程数据源实现中，如果无法联系服务器或服务器返回错误，则会触发此操作。
     */
    @Override
    public void getTasks(final LoadTasksCallback callback) {
        Handler handler = new Handler();
        //通过延迟执行来模拟网络。
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //把Map放进参数
                callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    /**
     * 注意：{@link GetTaskCallback 的 onDataNotAvailable（）}永远不会被触发。
     * 在真正的远程数据源实现中，如果无法联系服务器或服务器返回错误，则会触发此操作。
     */
    @Override
    public void getTask(String taskId, final GetTaskCallback callback) {
        //根据通过参数传进来的id，从map获取任务
        final Task task = TASKS_SERVICE_DATA.get(taskId);
        //通过延迟执行来模拟网络。
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTaskLoaded(task);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveTask(Task task) {
        //保存已经完成的任务
        Task completedTask = new Task(task.getId(), task.getTitle()
                , task.getDescription(), true);
        TASKS_SERVICE_DATA.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(Task task) {
        Task completedTask = new Task(task.getId(),task.getTitle(),
                task.getDescription(),true);
        TASKS_SERVICE_DATA.put(task.getId(),completedTask);
    }

    @Override
    public void completeTask(String taskId) {
    }

    @Override
    public void activateTask(Task task) {
        Task activeTask = new Task(task.getId(), task.getTitle()
                , task.getDescription());
        TASKS_SERVICE_DATA.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(String taskId) {

    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }
}
