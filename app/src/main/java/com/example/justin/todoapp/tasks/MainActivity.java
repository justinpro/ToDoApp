package com.example.justin.todoapp.tasks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.justin.todoapp.R;
import com.example.justin.todoapp.data.source.TasksDataSource;
import com.example.justin.todoapp.data.source.TasksRepository;
import com.example.justin.todoapp.data.source.local.TasksLocalDataSource;
import com.example.justin.todoapp.data.source.local.ToDoDatabase;
import com.example.justin.todoapp.data.source.remote.TasksRemoteDataSource;
import com.example.justin.todoapp.util.ActivityUtils;
import com.example.justin.todoapp.util.AppExecutors;
import com.example.justin.todoapp.util.Injection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test";

    private static String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    private DrawerLayout drawerLayout;

    private TasksPresenter mTasksPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //自定义Toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerLayout = this.findViewById(R.id.drawer_layout);

        //两种方式弹出drawer
        //这是一种,我喜欢这种
        //第二种是重写onOptionsItemSelected
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        //建立导航视图
        NavigationView navigationView = this.findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        /**
         * 在frameLayout 加载fragment
         *
         * 注意这种从fragment转activity的方法
         */
        TasksFragment tasksFragment = (TasksFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        Log.i(TAG, "MainActivity : TaskFragment 存不存在 :" + tasksFragment);

        if (tasksFragment == null) {
            //如果不存在就实例化
            tasksFragment = TasksFragment.newInstance();
            //添加Fragment
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager()
                    , tasksFragment, R.id.content_frame);
        }

        //创建Presenter
        mTasksPresenter = new TasksPresenter(
                Injection.getTasksRepository(getApplicationContext()),
                tasksFragment);

        /**
         * 加载之前保存的状态
         */
        if (savedInstanceState != null) {
            Log.i(TAG, "恢复状态 onCreate: ");
            TasksFilterType currentFiltering = (TasksFilterType)
                    savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mTasksPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "保存状态 onSaveInstanceState: ");
        outState.putSerializable(CURRENT_FILTERING_KEY, mTasksPresenter.getFiltering());
    }

    private void setupDrawerContent(NavigationView nv) {
        nv.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_item_list:
                                //什么都不用做，因为已经在这个界面了。
                                break;
                            case R.id.nav_item_statistics:
                                //Intent intent = new Intent(MainActivity.this,)
                                break;
                        }
                        item.setChecked(true);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //统一管理id
        if (item.getItemId() == android.R.id.home) {
            //当点击toolbar的home键时，打开抽屉视图
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
