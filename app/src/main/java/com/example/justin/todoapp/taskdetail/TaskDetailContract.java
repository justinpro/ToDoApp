package com.example.justin.todoapp.taskdetail;

import com.example.justin.todoapp.BasePresenter;
import com.example.justin.todoapp.BaseView;
import com.example.justin.todoapp.data.Task;

public interface TaskDetailContract {

    interface Presenter extends BasePresenter {

        void editTask();

        void deleteTask();

        void saveTask(String title,String describe);
    }

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void hideTitle(String title);

        void showTitle(String title);

        void hideDescription(String describe);

        void showDescription(String description);

        void showCompletedStatus(boolean complete);

        void showTaskDeleted();

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        boolean isActive();
    }
}
