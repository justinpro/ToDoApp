package com.example.justin.todoapp.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.justin.todoapp.data.Task;

import java.util.List;

@Dao
public interface TaskDao {

    /**
     * 选择表所有的数据
     *
     * @return all
     */
    @Query("select * from tasks")
    List<Task> getTasks();

    @Query("select * from tasks where entryId = :taskId")
    Task getTaskById(String taskId);

    /**
     * 插入一个任务
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    /**
     * 更新某个任务
     *
     * @return 任务被更新的数量，这个数应该为1
     */
    @Update
    int updateTask(Task task);

    /**
     * 由任务的id更新任务的状态
     */
    @Query("update tasks set completed = :completed where entryId = :taskId")
    void updateCompleted(String taskId, boolean completed);

    /**
     * 由id删除任务
     * @return 应该是1
     */
    @Query("delete from tasks where entryId = :taskId")
    int deleteTaskById(String taskId);

    /**
     * 删除所有的任务
     */
    @Query("delete from tasks")
    void deleteTasks();

    /**
     * 删除所有完成的任务
     * @return 删除的任务数量
     */
    @Query("delete from tasks where completed = 1")
    int deleteCompletedTasks();
}
