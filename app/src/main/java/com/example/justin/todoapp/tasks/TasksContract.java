package com.example.justin.todoapp.tasks;

import com.example.justin.todoapp.BasePresenter;
import com.example.justin.todoapp.BaseView;
import com.example.justin.todoapp.data.Task;

import java.util.List;

/**
 * 这是一个契约类
 * P和V是要相互通信的
 */
public interface TasksContract {

    //用来实现操作的类
    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(Task requestedTask);

        void completedTask(Task completedTask);

        void activateTask(Task activeTask);

        void clearCompletedTasks();

        void setFiltering(TasksFilterType tasksFilterType);

        TasksFilterType getFiltering();
    }

    interface View extends BaseView<Presenter> {

        //只是弹出刷新的加载进度条
        void setLoadingIndicator(boolean active);

        void showAddTask();

        //显示任务
        void showTasks(List<Task> list);

        void showTaskDetailsUi(String taskId);

        //任务的标记
        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTasksError();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilteringLabel();

        void showNoTasks();

        void showNoCompletedTasks();

        //过滤的时候，如果Active没有任务，调用该方法
        void showNoActiveTasks();

        //保存成功的信息
        void showSuccessfullySavedMessage();

        //是否是活动状态
        boolean isActive();

        //过滤的弹窗
        void showFilteringPopUpMenu();
    }
}
