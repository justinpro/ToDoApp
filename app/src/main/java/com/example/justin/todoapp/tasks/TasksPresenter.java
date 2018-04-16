package com.example.justin.todoapp.tasks;

import android.util.Log;

import com.example.justin.todoapp.data.Task;
import com.example.justin.todoapp.data.source.TasksDataSource;
import com.example.justin.todoapp.data.source.TasksRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksPresenter implements TasksContract.Presenter {
    private static final String TAG = "shit";

    //数据处理的库(presenter)
    private TasksRepository mTasksRepository;

    //view
    private TasksContract.View mTaskView;

    //默认当前状态
    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    //获得View 和 Model的实例
    public TasksPresenter(TasksRepository repository, TasksContract.View mTaskView) {
        this.mTasksRepository = checkNotNull(repository, "repository can not be null");
        this.mTaskView = checkNotNull(mTaskView, "mTaskView can not be null");

        //避免了在TaskView里面再次创建Presenter对象
        this.mTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        //开始是 false || true , true
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * 添加Task时先进入添加界面，即AddEditActivity
     */
    @Override
    public void addNewTask() {
        mTaskView.showAddTask();
    }

    /**
     * 打开日志详情页
     */
    @Override
    public void openTaskDetails(Task requestedTask) {
        checkNotNull(requestedTask, "任务无效");
        mTaskView.showTaskDetailsUi(requestedTask.getId());
    }

    //把数据库存放的数据的标记更新为completed
    @Override
    public void completedTask(Task completedTask) {
        checkNotNull(completedTask, "任务无效");
        //更改数据
        mTasksRepository.completeTask(completedTask);
        //让View显示标记
        mTaskView.showTaskMarkedComplete();
        //远程获取数据(到时候改为true)，还有不用弹出进度条,
        loadTasks(false, false);
    }

    @Override
    public void activateTask(Task activeTask) {
        checkNotNull(activeTask, "任务无效");
        //更改数据
        mTasksRepository.activateTask(activeTask);
        //让View显示标记
        mTaskView.showTaskMarkedActive();
        //远程获取数据(到时候改为true)，还有不用弹出进度条,
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTasks() {
        //数据库清除完成的任务
        mTasksRepository.clearCompletedTasks();
        //弹出消息
        mTaskView.showCompletedTasksCleared();
        //清理完成后自动再加载数据，并且使用加载进度
        loadTasks(false, false);
    }

    @Override
    public void setFiltering(TasksFilterType tasksFilterType) {
        mCurrentFiltering = tasksFilterType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUi) {
        Log.i(TAG, "正在调用TasksPresenter的loadTasks:");


        if (showLoadingUi) {
            //先弹出进度条
            mTaskView.setLoadingIndicator(true);
            Log.i(TAG, "loadTasks: 启动刷新进度条");
        }

        if (forceUpdate) {
            /**
             * 这个refreshTasks()把Tasks库的mCachedIsDirty变为false
             * 如果不用这个，那就需要从远程调用了
             */
            mTasksRepository.refreshTasks();
        }

        /**
         * 在这里，Tasks库已经拿到了所有的没有过滤的数据，
         * 但是，在这里需要过滤，所以，诉要再次使用它
         */
        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            /**
             * !!!! 注意:
             * 这里他会先调用TasksRepository的getTasks（）方法，
             * 并且运行方法内的代码之后才会运行这个匿名内部类的方法
             *
             * I: 正在调用TasksPresenter的loadTasks:
             * I: 调用了getTasks:
             * I: 匿名内部类onTasksLoaded:
             *
             * @param tasks getTasks()对这个参数进行了初始化，
             *              在这里可以直接使用参数的数据
             */
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                Log.i(TAG, "匿名内部类onTasksLoaded: ");

                //临时的，用来装过滤的数据
                List<Task> tasksToShow = new ArrayList<>();

                for (Task task : tasks) {
                    switch (mCurrentFiltering) {
                        case ACTIVE_TASKS:
                            if (task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETED_TASKS:
                            if (task.isCompleted()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }
                //不知道有什么用
                if (!mTaskView.isActive()) {
                    return;
                }

                Log.i(TAG, "onTasksLoaded: showLoadingUi => " + showLoadingUi);
                //把刷新进度条关闭
                if (showLoadingUi) {
                    Log.i(TAG, "onTasksLoaded: 关闭进度条 ");
                    mTaskView.setLoadingIndicator(false);
                }

                /**
                 * #####  重要了 #####
                 * 让View层显示数据
                 */
                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mTaskView.isActive()) {
                    return;
                }
                mTaskView.showLoadingTasksError();
            }
        });
    }

    /**
     * 让View层显示数据
     *
     * @param tasks 过滤的数据
     */
    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            //如果所过滤出来的内容如果为空，调用该方法
            processEmptyTasks();
        } else {
            //如果所过滤出来的数据为有，则让View层显示数据
            mTaskView.showTasks(tasks);
            //CheckBox打勾
            showFilterLabel();
        }
    }

    /**
     * 处理过滤数据为空时，所要显示的提示
     */
    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case COMPLETED_TASKS:
                mTaskView.showNoCompletedTasks();
                break;
            case ACTIVE_TASKS:
                mTaskView.showNoActiveTasks();
                break;
            case ALL_TASKS:
                mTaskView.showNoTasks();
                break;
        }
    }

    /**
     * 过滤时，在过滤出来的CheckBox打上标签（打勾）
     */
    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case COMPLETED_TASKS:
                mTaskView.showCompletedFilterLabel();
                break;
            case ACTIVE_TASKS:
                mTaskView.showActiveFilterLabel();
                break;
            case ALL_TASKS:
                mTaskView.showAllFilteringLabel();
                break;
        }
    }
}
