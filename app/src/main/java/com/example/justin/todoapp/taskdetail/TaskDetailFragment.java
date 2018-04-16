package com.example.justin.todoapp.taskdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.justin.todoapp.R;

public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {

    private TaskDetailContract.Presenter mPresenter;

    private static final String ARGUMENT_TASK_ID = "TASK_ID";

    private EditText detailTitle;
    private EditText detailDescribe;

    private boolean isEdited = false;

    public static TaskDetailFragment newInstance(String taskId) {

        Bundle args = new Bundle();
        args.putString(ARGUMENT_TASK_ID, taskId);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_frag, container, false);

        detailTitle = root.findViewById(R.id.add_title);
        detailDescribe = root.findViewById(R.id.add_describe);


        //fab 的不能用root的原因是，fab是activity的 不是fragment的
        final FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 没有编辑过的才能使用
                if (!isEdited) {
                    mPresenter.editTask();
                    //点击 fab之后，进入编辑模式，所以需要把图片换成确认的
                    fab.setImageResource(R.drawable.ic_done);

                    isEdited = true;
                } else {
                    //凡是编辑过的都保存一遍
                    mPresenter.saveTask(detailTitle.getText().toString().trim(),
                            detailDescribe.getText().toString().trim());

                    //点击 确认之后，进入不可编辑模式，所以需要把图片换成修改的
                    fab.setImageResource(R.drawable.ic_edit);

                    //再重新启动Presenter一遍,也就是刷新一次数据，顺便把EditText封印
                    mPresenter.start();

                    isEdited = false;
                }
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * 一般fragment的界面的初始化结束后，
         * 就马上要进presenter里获取数据来显示，
         * 所以mPresenter.start()里面的内容一般是获取数据库的数据，
         * 并且调用fragment来显示数据
         *
         * 注意:
         * fragment一定要初始化完成后才能调用该方法,不然数据获取了，
         * 界面没初始化会报错
         */
        mPresenter.start();
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * 从数据库拿数据事件长的话，先调用该方法来过渡
     */
    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            detailTitle.setText("");
            detailDescribe.setText("Loading");
        }
    }

    @Override
    public void hideTitle(String title) {
        detailTitle.setText(title);
        detailTitle.setFocusable(false);
        //detailTitle.setFocusableInTouchMode(false);
        //隐藏光标，不然太难看
        detailTitle.setCursorVisible(false);
        //detailTitle.clearFocus();
    }

    @Override
    public void showTitle(String title) {
        detailTitle.setText(title);
        detailTitle.setFocusable(true);
        detailTitle.setFocusableInTouchMode(true);
        detailTitle.requestFocus();
        detailTitle.setCursorVisible(true);
    }

    @Override
    public void hideDescription(String describe) {
        detailDescribe.setText(describe);
        detailDescribe.setFocusable(false);
        //detailDescribe.setFocusableInTouchMode(false);
        //隐藏光标，不然太难看
        detailDescribe.setCursorVisible(false);
        //detailTitle.clearFocus();
    }

    @Override
    public void showDescription(String description) {
        detailDescribe.setText(description);
        detailDescribe.setFocusable(true);
        detailDescribe.setFocusableInTouchMode(true);
        detailDescribe.requestFocus();
        detailDescribe.setCursorVisible(true);
    }

    @Override
    public void showCompletedStatus(boolean complete) {

    }

    /**
     * 关闭编辑界面
     */
    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }

    @Override
    public void showTaskMarkedComplete() {
        Snackbar.make(getView(), "当前任务已完成", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar.make(getView(), "当前任务未完成", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
