package com.example.justin.todoapp.data.source.local;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.example.justin.todoapp.data.Task;
import com.example.justin.todoapp.data.source.TasksDataSource;
import com.example.justin.todoapp.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

public class TasksLocalDataSource implements TasksDataSource {
    private static final String TAG = "test";

    private static TasksLocalDataSource Instance;

    private AppExecutors appExecutors;

    private TaskDao taskDao;

    public TasksLocalDataSource(AppExecutors appExecutors,
                                TaskDao taskDao) {
        this.appExecutors = appExecutors;
        this.taskDao = taskDao;
    }

    public static TasksLocalDataSource getInstance(AppExecutors appExecutors,
                                            TaskDao taskDao) {
        if (Instance == null) {
            synchronized (TasksLocalDataSource.class) {
                if (Instance == null) {
                    Instance = new TasksLocalDataSource(appExecutors, taskDao);
                }
            }
        }
        return Instance;
    }

    @VisibleForTesting
    static void clearInstance() {
        Instance = null;
    }

    @Override
    public void getTasks(final LoadTasksCallback callback) {
        Log.i(TAG, "getTasks: TasksLocalDataSource");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //取得数据
                final List<Task> tasks = taskDao.getTasks();
                appExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tasks.isEmpty()) {
                            //如果数据库是空的，就会被调用
                            callback.onDataNotAvailable();
                        } else {
                            //把数据放进回调接口
                            callback.onTasksLoaded(tasks);
                        }
                    }
                });
            }
        };

        appExecutors.getDiskIo().execute(runnable);
    }

    @Override
    public void getTask(final String taskId, final GetTaskCallback callback) {
        Log.i(TAG, "getTask: TasksLocalDataSource");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Task task = taskDao.getTaskById(taskId);

                appExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (task != null) {
                            callback.onTaskLoaded(task);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        appExecutors.getDiskIo().execute(runnable);
    }

    @Override
    public void saveTask(final Task task) {
        Log.i(TAG, "saveTask: TasksLocalDataSource");

        checkNotNull(task);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //保存就是插入
                taskDao.insertTask(task);
            }
        };

        appExecutors.getDiskIo().execute(runnable);
    }

    @Override
    public void completeTask(final Task task) {
        Log.i(TAG, "completeTask: TasksLocalDataSource");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                taskDao.updateCompleted(task.getId(), true);
            }
        };
        appExecutors.getDiskIo().execute(runnable);
    }

    @Override
    public void completeTask(String taskId) {

    }

    @Override
    public void activateTask(final Task task) {
        Log.i(TAG, "activateTask: TasksLocalDataSource");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                taskDao.updateCompleted(task.getId(), false);
            }
        };

        appExecutors.getDiskIo().execute(runnable);
    }

    @Override
    public void activateTask(String taskId) {

    }

    @Override
    public void clearCompletedTasks() {
        Log.i(TAG, "clearCompletedTasks: TasksLocalDataSource");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                taskDao.deleteCompletedTasks();
            }
        };
        appExecutors.getDiskIo().execute(runnable);
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        Log.i(TAG, "deleteAllTasks: TasksLocalDataSource");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                taskDao.deleteTasks();
            }
        };
        appExecutors.getDiskIo().execute(runnable);
    }

    @Override
    public void deleteTask(final String taskId) {
        Log.i(TAG, "deleteTask: TasksLocalDataSource");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                taskDao.deleteTaskById(taskId);
            }
        };
        appExecutors.getDiskIo().execute(runnable);
    }
}
