package com.example.justin.todoapp.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.google.common.base.Preconditions.checkNotNull;

public class ActivityUtils {
    //因为可能要用多次，所有为了内存泄漏，使用静态的
    public static void addFragmentToActivity(FragmentManager manager
            , Fragment fragment, int frameId) {
        checkNotNull(manager);
        checkNotNull(fragment);
        checkNotNull(frameId);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}
