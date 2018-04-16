package com.example.justin.todoapp.addedittask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.justin.todoapp.R;

public class AddEditTaskFragment extends Fragment implements AddEditTaskContract.View {
    private AddEditTaskContract.Presenter presenter;

    private EditText add_title;
    private EditText add_describe;

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    public AddEditTaskFragment() {
    }

    public static AddEditTaskFragment newInstance() {
        return new AddEditTaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.add_frag, container, false);

        add_title = root.findViewById(R.id.add_title);
        add_describe = root.findViewById(R.id.add_describe);
        return root;
    }

    @Override
    public void setPresenter(AddEditTaskContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        //悬浮按钮
        FloatingActionButton fab = getActivity().findViewById(R.id.add_float);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveTask(add_title.getText().toString().trim(),
                        add_describe.getText().toString().trim());
            }
        });
    }

    @Override
    public void showTaskList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        add_title.setText(title);
    }

    @Override
    public void setDescribe(String describe) {
        add_describe.setText(describe);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
