package com.example.justin.todoapp.taskdetail;

import com.example.justin.todoapp.data.Task;
import com.example.justin.todoapp.data.source.TasksDataSource;
import com.example.justin.todoapp.data.source.TasksRepository;

import java.util.Objects;

public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private TasksRepository tasksRepository;
    private TaskDetailContract.View taskDetailFragment;

    private String taskId;

    public TaskDetailPresenter(TasksRepository tasksRepository,
                               TaskDetailContract.View taskDetailFragment,
                               String taskId) {
        this.tasksRepository = tasksRepository;
        this.taskDetailFragment = taskDetailFragment;

        taskDetailFragment.setPresenter(this);

        this.taskId = taskId;
    }

    /**
     * 这个方法一般是fragment初始化结束后，
     * 需要显示数据，
     * 启动presenter来获取数据来显示
     */
    @Override
    public void start() {
        openTask();
    }

    @Override
    public void editTask() {

        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                taskDetailFragment.showTitle(task.getTitle());
                taskDetailFragment.showDescription(task.getDescription());
                if (task.isCompleted()){
                    taskDetailFragment.showTaskMarkedComplete();
                }else {
                    taskDetailFragment.showTaskMarkedActive();
                }
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void deleteTask() {

    }

    @Override
    public void saveTask(final String title, final String describe) {
        //判断一下是否编辑了,这里我要求信息实时化，所以我再次获取一次数据库的信息
        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                //内容不相等就要保存
                if (!Objects.equals(task.getTitle(), title) ||
                        !Objects.equals(task.getDescription(), describe)){
                    tasksRepository.saveTask(new Task(taskId,title,describe));
                }
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    /**
     * 打开日志之后，加载数据
     */
    private void openTask() {
        /**
         * 这个回调接口的意义在于 ：
         *
         * 通过tasksRepository.getTasks这个方法来获取数据，并把数据放在接口的参数里。
         *
         * 因为接口需要被匿名内部类实现，所以实现接口的那个类就需要把数据放在参数里，
         * 由另外的一个方法来通过匿名内部类来获取数据
         */
        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                taskDetailFragment.hideTitle(task.getTitle());
                taskDetailFragment.hideDescription(task.getDescription());
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
}
