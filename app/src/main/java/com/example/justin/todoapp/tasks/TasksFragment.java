package com.example.justin.todoapp.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.justin.todoapp.R;
import com.example.justin.todoapp.addedittask.AddEditTaskActivity;
import com.example.justin.todoapp.data.Task;
import com.example.justin.todoapp.taskdetail.TaskDetailActivity;
import com.example.justin.todoapp.taskdetail.TaskDetailFragment;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements TasksContract.View {
    private static final String TAG = "shit";

    private TasksContract.Presenter mPresenter;

    //适配器
    private TasksAdapter mTasksAdapter;

    //没有任务的时候的界面
    private View mNoTasksView;

    //没有任务的时候的图标
    private ImageView mNoTaskIcon;

    //没有任务的时候的主界面的文字
    private TextView mNoTaskMainView;

    private TextView mNoTaskAddView;

    private LinearLayout mTaskView;

    //过滤的标签
    private TextView mFilteringLabelView;

    public TasksFragment() {
        //需要个空的构造函数
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 有0和没的区别？
         */
        mTasksAdapter = new TasksAdapter(new ArrayList<Task>(), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        //Presenter 初始化,
        // 在TasksPresenter里面使用该方法,
        // 这样使用的原因是避免了再次初始化
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.task_frag, container, false);

        //设置有任务的时候的视图
        ListView listView = root.findViewById(R.id.frag_list);
        listView.setAdapter(mTasksAdapter);

        mFilteringLabelView = root.findViewById(R.id.filteringLabel);
        mTaskView = root.findViewById(R.id.frag_Tasks);

        //设置没有任务的时候的视图
        mNoTasksView = root.findViewById(R.id.frag_noTasks);
        mNoTaskIcon = root.findViewById(R.id.noTasksIcon);
        mNoTaskAddView = root.findViewById(R.id.noTasksAdd);
        mNoTaskMainView = root.findViewById(R.id.noTasksMain);

        /**
         * 刚开始默认是没有任何Task的，所以先显示"没有任务"的界面
         */
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTask();
            }
        });

        //设置悬浮按钮
        FloatingActionButton fab =
                getActivity().findViewById(R.id.add_button);

        /**
         * 点击悬浮View，使用presenter来添加任务
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewTask();
            }
        });

        //设置刷新进度条
        ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                root.findViewById(R.id.custom_swipe);
        //设置旋转的时候的颜色变化
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorRed)
        );
        //设置滚动的View
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTasks(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    /**
     * 用来创建菜单
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_frag_options, menu);
    }

    /**
     * 用来创建菜单点击时的，事件监听
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPresenter.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPresenter.loadTasks(true);
                break;
        }
        //到时候测试一下为false
        return true;
    }

    /**
     * 只是弹出刷新的加载进度条
     */
    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        Log.i(TAG, "设置加载进度setLoadingIndicator: " + active);
        final SwipeRefreshLayout srl = getView().findViewById(R.id.custom_swipe);

        //确保布局已经准备好了之后setRefreshing() 才被调用
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    /**
     * 跳转到添加Task的页面
     */
    @Override
    public void showAddTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTasks(List<Task> list) {
        mTasksAdapter.replaceData(list);

        //任务视图，设置是否可视化,没有任务的视图设置为不显示
        mTaskView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    //显示详情页
    @Override
    public void showTaskDetailsUi(String taskId) {
        /**
         * 注意:
         *
         * fragment 跳转到 activity 的context是用getContext() ，
         * 用类名.this是不行的，
         * 但是getContext就可以，因为fragment的 "根" 是activity的
         */
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID,taskId);
        startActivity(intent);
    }

    /**
     * 在单选框中选中了的消息弹窗
     */
    @Override
    public void showTaskMarkedComplete() {
        showMessage("任务已完成");
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage("任务未完成");
    }

    @Override
    public void showCompletedTasksCleared() {
        showMessage("已清除已完成的任务");
    }

    @Override
    public void showLoadingTasksError() {
        showMessage("在加载任务时出错了");
    }


    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText("未完成的任务");
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText("完成的任务");
    }

    @Override
    public void showAllFilteringLabel() {
        mFilteringLabelView.setText("所有");
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews("你还没有建立任务哦",
                R.drawable.ic_verified_user_24dp, false);
    }

    /**
     * 这是用在过滤器那里的，点击Completed，如果没有要显示的任务就调用该方法
     */
    @Override
    public void showNoCompletedTasks() {
        showNoTasksViews("你的任务都没有完成",
                R.drawable.ic_assignment_turned_in_24dp, false);
    }

    /**
     * 过滤的时候，如果Active没有任务，调用该方法
     */
    @Override
    public void showNoActiveTasks() {
        showNoTasksViews("你的任务都完成了",
                R.drawable.ic_check_circle_24dp, false);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage("任务保存成功");
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(),
                getActivity().findViewById(R.id.menu_filter));
        popupMenu.getMenuInflater().inflate(R.menu.tasks_filter, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.all:
                        mPresenter.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                    case R.id.active:
                        mPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        mPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                        break;
                }
                //选择过滤之后，重新加载任务
                mPresenter.loadTasks(false);

                return true;
            }
        });

        //没有这个show，是不会弹出的
        popupMenu.show();
    }

    private class TasksAdapter extends BaseAdapter {
        private List<Task> mTasks;

        private TaskItemListener mItemListener;

        TasksAdapter(List<Task> tasks, TaskItemListener mItemListener) {
            setList(tasks);
            this.mItemListener = mItemListener;
        }

        void setList(List<Task> tasks) {
            mTasks = checkNotNull(tasks);
        }

        //把mTasks换成新的数据
        void replaceData(List<Task> tasks) {
            setList(tasks);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Object getItem(int position) {
            return mTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            /**
             * 这样是可以优化内存的使用，
             * 不用多余建立新的 View
             */
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater
                        .from(parent.getContext());
                convertView = inflater.inflate(R.layout.task_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.titleItem = convertView.findViewById(R.id.item_title);
                viewHolder.completeCB = convertView.findViewById(R.id.item_check);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Task task = (Task) getItem(position);

            //在ListView依次设置标题
            viewHolder.titleItem.setText(task.getTitle());

            viewHolder.completeCB.setChecked(task.isCompleted());
            if (task.isCompleted()) {
                convertView.setBackgroundDrawable(parent.getContext().getResources()
                        .getDrawable(R.drawable.list_completed_touch_feedback));
            } else {
                convertView.setBackgroundDrawable(parent.getContext().getResources()
                        .getDrawable(R.drawable.touch_feedback));
            }
            viewHolder.completeCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 这个监听的是点击前的状态，如果点击前是Active，
                     * 那么isCompleted 为false，
                     * 点击之后就变为completed了，
                     * 所以要用onCompleteTaskClick把它变为completed
                     */
                    if (!task.isCompleted()){
                        mItemListener.onCompleteTaskClick(task);
                    }else {
                        mItemListener.onActivateTaskClick(task);
                    }
                }
            });

            //点击item进入详情页
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onTaskClick(task);
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        TextView titleItem;
        CheckBox completeCB;
    }

    /**
     * 显示没有任务的时候的视图
     */
    private void showNoTasksViews(String mainText, int icon, boolean showAddView) {
        mTaskView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(getResources().getDrawable(icon));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置弹出的消息
     */
    private void showMessage(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 任务Item监听器
     */
    private interface TaskItemListener {
        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }

    TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
            mPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            mPresenter.completedTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            mPresenter.activateTask(activatedTask);
        }
    };

}
