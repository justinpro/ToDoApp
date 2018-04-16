package com.example.justin.todoapp.util;

import android.content.Context;

import com.example.justin.todoapp.data.source.TasksRepository;
import com.example.justin.todoapp.data.source.local.TasksLocalDataSource;
import com.example.justin.todoapp.data.source.local.ToDoDatabase;
import com.example.justin.todoapp.data.source.remote.TasksRemoteDataSource;

public class Injection {

    public static TasksRepository getTasksRepository(Context context){
        ToDoDatabase toDoDatabase = ToDoDatabase.getInstance(context);
        return TasksRepository.getInstance(
                TasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(
                        new AppExecutors(),toDoDatabase.taskDao()));
    }
}
