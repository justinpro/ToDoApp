package com.example.justin.todoapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

@Entity(tableName = "tasks")
public class Task {
    private static final String TAG = "shit";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryId")
    private String mId;

    @Nullable
    @ColumnInfo(name = "title")
    private String mTitle;

    @Nullable
    @ColumnInfo(name = "description")
    private String mDescription;

    @ColumnInfo(name = "completed")
    private boolean mCompleted;

    public Task() {
    }

    public Task(@NonNull String Id, @Nullable String Title, @Nullable String Description, boolean Completed) {
        mId = Id;
        mTitle = Title;
        mDescription = Description;
        mCompleted = Completed;
    }

    /**
     * 这个构造函数用来创建正在执行的任务
     * 新建立的任务不可能马上被完成，
     * 所以这里completed = false
     */
    @Ignore
    public Task(@Nullable String Title, @Nullable String Description) {
        this(UUID.randomUUID().toString(), Title, Description, false);
    }

    /**
     * 如果任务已经有了id，那么就创建一个正在执行的任务
     * 新建立的任务不可能马上被完成，
     * 所以这里completed = false
     */
    @Ignore
    public Task(@NonNull String Id, @Nullable String Title, @Nullable String Description) {
        this(Id, Title, Description, false);
    }

    /**
     * 使用这个构造函数创建一个已完成的任务
     */
    @Ignore
    public Task(@Nullable String Title, @Nullable String Description, boolean Completed) {
        this(UUID.randomUUID().toString(), Title, Description, Completed);
    }



    //在构造函数那里被确定
    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle)
                && Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Task task = (Task) obj;
        return Objects.equal(mId, task.mId)
                && Objects.equal(mTitle, task.mTitle)
                && Objects.equal(mDescription, task.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }

    @Override
    public String toString() {
        return "Task With Title : " + mTitle;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    /**
     * 千万不能忘记写setter 和getter
     */
    public void setId(@NonNull String mId) {
        this.mId = mId;
    }

    public void setTitle(@Nullable String mTitle) {
        this.mTitle = mTitle;
    }

    public void setDescription(@Nullable String mDescription) {
        this.mDescription = mDescription;
    }

    public void setCompleted(boolean mCompleted) {
        this.mCompleted = mCompleted;
    }

    @NonNull
    public String getId() {

        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean ismCompleted() {
        return mCompleted;
    }
}
