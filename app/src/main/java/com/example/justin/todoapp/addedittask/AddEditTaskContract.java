package com.example.justin.todoapp.addedittask;

import com.example.justin.todoapp.BasePresenter;
import com.example.justin.todoapp.BaseView;

public interface AddEditTaskContract {

    interface Presenter extends BasePresenter{
        void saveTask(String title,String describe);

        void populateTask();
    }

    interface View extends BaseView<Presenter>{

        void showTaskList();

        void setTitle(String title);

        void setDescribe(String describe);

        boolean isActive();
    }
}
