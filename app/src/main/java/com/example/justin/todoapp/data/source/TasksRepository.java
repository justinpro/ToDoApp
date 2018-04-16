package com.example.justin.todoapp.data.source;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.justin.todoapp.data.Task;

/**
 * 需要手动导入包
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * 从数据源加载任务到缓存的具体实现。
 * <p>
 * 为了简单起见，只有当本地数据库不存在或为空时
 * ，才通过使用远程数据源实现本地持久数据和从服务器获得的数据之间的哑同步。
 * <p>
 * 对这个类的 说明:
 * 这个类是本地数据处理和网络数据处理的总类，对本地和远程数据加以控制
 */
public class TasksRepository implements TasksDataSource {

    private static final String TAG = "shit";

    private static TasksRepository Instance = null;

    private TasksDataSource mTaskRemoteDataSource;
    private TasksDataSource mTaskLocalDataSource;

    /**
     * 这个变量具有包本地可视性，所以它能被用在测试中。
     */
    private Map<String, Task> mCachedTasks;

    /**
     * 将缓存标记为无效，在下一次请求数据时强制更新。
     * 这个变量具有包本地可见性，所以可以从测试中访问它。
     */
    private boolean mCachedIsDirty = false;

    /**
     * 防止直接实例化。
     */
    private TasksRepository(@NonNull TasksDataSource taskRemoteDataSource
            , @NonNull TasksDataSource taskLocalDataSource) {
        //这里必须要传进来一个TaskDataSource 的实例才能用，因为这里是使用它的引用
        //没有实例使用不了的
        mTaskRemoteDataSource = checkNotNull(taskRemoteDataSource);
        mTaskLocalDataSource = checkNotNull(taskLocalDataSource);
    }

    //实例化本类
    public static TasksRepository getInstance(TasksDataSource taskRemoteDataSource
            , TasksDataSource taskLocalDataSource) {
        if (Instance == null) {
            Instance = new TasksRepository(taskRemoteDataSource, taskLocalDataSource);
        }
        return Instance;
    }

    public static void destroyInstance() {
        Instance = null;
    }

    /**
     * 从缓存，本地数据源（SQLite）或远程数据源获取任务，先选择哪一个。
     * <p>
     * 这个方法主要是把数据放入参数里，等待Presenter来使用参数的数据
     */
    @Override
    public void getTasks(final LoadTasksCallback callback) {

        Log.i(TAG, "调用了getTasks: ");

        checkNotNull(callback);

        /**
         * 这里的mCachedDirty 是要由Presenter的loadTasks来控制
         * 如果mCachedTasks不为空，而且mCachedIsDirty为false
         */
        if (mCachedTasks != null && !mCachedIsDirty) {
            Log.i(TAG, "getTasks: mCachedTasks不为空 ");
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        Log.i(TAG, "测试 mCachedIsDirty => " + mCachedIsDirty);
        if (mCachedIsDirty) {
            Log.i(TAG, "网络获取数据 getTasks");

            //如果缓存不可用，我们需要从网络匹配新数据
            getTasksFromRemoteDataSource(callback);
        } else {
            Log.i(TAG, "本地获取数据 getTasks: ");

            //如果可用，则查询本地储存，如果没有查询网络
            mTaskLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    //刷新缓存
                    refreshCache(tasks);

                    Log.i(TAG, "把数据放进回调接口 onTasksLoaded: ");
                    //刷新了之后，必须要更新回调接口的数据
                    // !!! 把数据放入参数里
                    callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                    Log.i(TAG, "远程获得数据 onDataNotAvailable: ");
                }
            });
        }
    }

    @Override
    public void getTask(final String taskId, final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        //用来获得任务
        final Task cachedTask = getTaskWithId(taskId);
        if (cachedTask != null) {
            //把要获取的Task放进参数，给下面处理
            callback.onTaskLoaded(cachedTask);
            return;
        }

        //上面的程序中，已经把要要获取的Task放进参数里了，
        // 在这里就要把Task放进缓存Map里面
        mTaskLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                //在内存高速缓存更新，以保持应用程序界面最新
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(task.getId(), task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                //如果不能在本地加载数据，就要从网络获取数据
                mTaskRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(task.getId(), task);
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveTask(Task task) {
        checkNotNull(task);
        mTaskLocalDataSource.saveTask(task);
        mTaskRemoteDataSource.saveTask(task);

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(Task task) {
        checkNotNull(task);
        mTaskLocalDataSource.completeTask(task);
        mTaskRemoteDataSource.completeTask(task);

        Task completedTask = new Task(task.getId(),
                task.getTitle(), task.getDescription(), true);
        //相当于更新了原来id的状态，也就是completed:false -> true
        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(String taskId) {
        checkNotNull(taskId);
        //调用上面的那个方法
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(Task task) {
        checkNotNull(task);
        mTaskRemoteDataSource.activateTask(task);
        mTaskLocalDataSource.activateTask(task);

        //不传值completed当与false
        Task activeTask = new Task(task.getId(), task.getTitle()
                , task.getDescription());

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        mCachedTasks.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTaskLocalDataSource.clearCompletedTasks();
        mTaskRemoteDataSource.clearCompletedTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        //遍历mCacheTasks 如果发现是完成的就remove它
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCachedIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTaskRemoteDataSource.deleteAllTasks();
        mTaskLocalDataSource.deleteAllTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        //如果Map里面还有就清空它
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(String taskId) {
        mTaskRemoteDataSource.deleteTask(taskId);
        mTaskLocalDataSource.deleteTask(taskId);

        mCachedTasks.remove(taskId);
    }

    /**
     * 远程获得数据
     */
    private void getTasksFromRemoteDataSource(final LoadTasksCallback callback) {

        Log.i(TAG, "远程获得数据 getTasksFromRemoteDataSource: ");

        mTaskRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                //把远程的数据转到这个类的mCacheTask
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        Log.i(TAG, "刷新本地数据库 refreshLocalDataSource: TasksRepository");

        //这里调用的方法在TasksLocalDataSource里面已经被重写过了。
        mTaskLocalDataSource.deleteAllTasks();
        for (Task task : tasks) {
            mTaskLocalDataSource.saveTask(task);
        }
    }


    private void refreshCache(List<Task> tasks) {
        Log.i(TAG, "刷新 refreshCache: TasksRepository");

        //没有就建立
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        //有就清空（相当于刷新），然后再放新的进去
        mCachedTasks.clear();
        for (Task t : tasks) {
            mCachedTasks.put(t.getId(), t);
        }
        mCachedIsDirty = false;
    }

    //从CacheTask里面拿Task
    private Task getTaskWithId(String id) {

        Log.i(TAG, "getTaskWithId: TasksRepository");
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }
}
