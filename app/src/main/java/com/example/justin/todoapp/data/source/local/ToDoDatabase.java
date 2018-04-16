package com.example.justin.todoapp.data.source.local;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.justin.todoapp.data.Task;

@Database(entities = {Task.class},version = 1)
public abstract class ToDoDatabase extends RoomDatabase {

    private static ToDoDatabase Instance;

    public abstract TaskDao taskDao();

    private static final Object lock = new Object();

    public static ToDoDatabase getInstance(Context context) {
        synchronized (lock){
            if (Instance == null){
                Instance = Room.databaseBuilder(context.getApplicationContext(),
                        ToDoDatabase.class,"task.db")
                        .build();
            }
            return Instance;
        }
    }
}
