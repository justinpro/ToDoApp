package com.example.justin.todoapp.addedittask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.justin.todoapp.R;
import com.example.justin.todoapp.util.ActivityUtils;
import com.example.justin.todoapp.util.Injection;

public class AddEditTaskActivity extends AppCompatActivity {

    public ActionBar actionBar;

    public static final int REQUEST_ADD_TASK = 1;

    private static final String SHOULD_LOAD_DATA_DROM_REPO_KEY
            = "SHOULD_LOAD_DATA_DROM_REPO_KEY";

    public AddEditTaskPresenter addEditTaskPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);

        Toolbar toolbar = this.findViewById(R.id.add_toolbar);
        toolbar.setTitle("新建日志");

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String taskId = getIntent().getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID);
        setTitle(taskId);

        AddEditTaskFragment addEditTaskFragment = (AddEditTaskFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_frame);
        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();

            /**
             * 编辑 日志的时候,
             * 接受传进来的日志ID
             */
            if (getIntent().hasExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)) {
                Bundle bundle = new Bundle();
                bundle.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                addEditTaskFragment.setArguments(bundle);
            }
            //跳转
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.add_frame);
        }

        /**
         * 实例化Presenter,但在这里并不使用该实例，
         * 而是为了让Fragment获得AddEditTaskPresenter的实例
         * ===> addTaskView.setPresenter(this);
         */
        addEditTaskPresenter = new AddEditTaskPresenter(
                Injection.getTasksRepository(
                        getApplicationContext()), addEditTaskFragment, taskId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolBarTitle(String taskId) {
        if (taskId == null) {
            actionBar.setTitle("新建日志");
        } else {
            actionBar.setTitle("编辑日志");
        }
    }
}
