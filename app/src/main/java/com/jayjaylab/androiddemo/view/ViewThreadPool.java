package com.jayjaylab.androiddemo.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import roboguice.util.Ln;

/**
 * This is a view pool to manage views which is to be displayed and disappear after doing what
 * they have to on an activity.
 *
 * Created by jongjoo on 11/2/14.
 */
public abstract class ViewThreadPool<T extends View> {
    private final int DEFAULT_LIMIT = 16;
    int poolSizeLimit;
    Map<Integer, ViewHolder<T>> pool;
    Provider<Context> contextProvider;

    @Inject
    public ViewThreadPool(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
        this.poolSizeLimit = DEFAULT_LIMIT;

        // expects that no rehash occurs
        pool = new HashMap<Integer, ViewHolder<T>>(poolSizeLimit, 1.2f);
        generateViews();
    }

    public synchronized T getView() {
        checkPreCondition();

        T view = null;
        // gets a view from the pool
        // finds if there's a idle view
        ViewHolder<T> holder = null;
        Set<Map.Entry<Integer, ViewHolder<T>>> entrySet = pool.entrySet();
        for(Map.Entry<Integer, ViewHolder<T>> entry : entrySet) {
            holder = entry.getValue();
            if(!holder.isUsed()) {
                view = holder.getView();
                holder.setUsed(true);
                break;
            }
        }

        return view;
    }

    public synchronized boolean recycle(T view) {
        checkPreCondition();

        // finds the view in the Map
        ViewHolder<T> holder = pool.get(view.hashCode());
        if(holder == null)
            return false;

        holder.setUsed(false);
        // TODO do something to make the view in default state? I'm not sure

        return true;
    }

    protected void checkPreCondition() {
        if(poolSizeLimit <= 0)
            throw new IllegalStateException("poolSizeLimit isn't specified.");
        if(pool == null)
            throw new IllegalStateException("pool is null.");
    }

//    @Inject
//    public ViewThreadPool(Provider<Context> contextProvider, int poolSizeLimit) {
//        this.poolSizeLimit = poolSizeLimit;
//
//        // expects that no rehash occurs
//        pool = new HashMap<Integer, ViewHolder<T>>(poolSizeLimit, 1.2f);
//        generateViews();
//    }

    protected abstract void generateViews();

    protected class ViewHolder<T> {
        boolean isUsed;
        T view;

        public boolean isUsed() {
            return isUsed;
        }

        public void setUsed(boolean isUsed) {
            this.isUsed = isUsed;
        }

        public T getView() {
            return view;
        }

        public void setView(T view) {
            this.view = view;
        }
    }
}
