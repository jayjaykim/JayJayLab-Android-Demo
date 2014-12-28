package com.jayjaylab.androiddemo.app.greyhound.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.DaoMaster;
import com.jayjaylab.androiddemo.DaoSession;
import com.jayjaylab.androiddemo.Path;
import com.jayjaylab.androiddemo.PathDao;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.adapter.AdapterPathHistory;
import com.jayjaylab.androiddemo.event.OnClickEvent;
import com.jayjaylab.androiddemo.event.OnLongClickEvent;

import java.util.List;

import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;


/**
 * Created by jongjoo on 11/17/14.
 */
public class FragmentPathHistory extends RoboFragment {
    public static final String TAG = FragmentPathHistory.class.getSimpleName();
    private final int HISTORY_ROW_NUM_LIMIT = 10;

    ActionMode actionMode;
    DaoSession daoSession;
    PathDao pathDao;

    @Inject Handler handler;
    @Inject AdapterPathHistory adapter;
    // views
    @InjectView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createDaoSession();
        setViews();
        loadTenPaths(-1);
    }

    protected void handleOnClickEvent(@Observes OnClickEvent event) {
        Ln.d("handleOnClickEvent() : event : %s", event);

        if(actionMode != null) {
            actionMode.setTitle(String.valueOf(adapter.getCountSelected()) + " " + getResources().getString(R.string.selected));
        }
    }

    protected void handleOnLongClickEvent(@Observes OnLongClickEvent event) {
        Ln.d("handleOnLongClickEvent() : actionMode : %s, event : %s", actionMode, event);
        if(actionMode != null) {
            return;
        }

        actionMode = ((ActionBarActivity)getActivity()).startSupportActionMode(actionModeCallback);
        actionMode.setTitle(String.valueOf(adapter.getCountSelected()) + " " + getResources().getString(R.string.selected));
    }

    protected void setViews() {
        setRecyclerView();
    }

    protected void createDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "path-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        pathDao = daoSession.getPathDao();
    }

    /**
     * Loads the recents ten paths
     * @param fromIndex < 0 means loading recent 10 paths,<code>fromIndex</code> > 0 means
     *                  loading 10 paths which is smaller than the index
     */
    public void loadTenPaths(int fromIndex) {
        if(fromIndex < 0) {
            // loads the recent ten paths
            List<Path> pathList = pathDao.queryBuilder().
                    orderDesc(PathDao.Properties.Id).
                    limit(HISTORY_ROW_NUM_LIMIT).list();
            Ln.d("loadTenPaths() : paths : %s, # paths : %d", pathList, pathList.size());
            adapter.addItems(pathList);
        } else {
            // TODO loads the paths which is smaller than the fromIndex
        }
    }

    protected void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(onScrollListener);
        recyclerView.setAdapter(adapter);
    }

    public void addPath(com.jayjaylab.androiddemo.app.greyhound.model.Path path) {
        Ln.d("addPath() : path : %s", path);

        pathDao.insertWithoutSettingPk(path.getPathEntity());
        adapter.addItem(path.getPathEntity());
    }

    protected ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.menu_cab_fragment_path_history, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch(menuItem.getItemId()) {
                case R.id.menu_delete:
                    // FIXME async deletetion
                    List<AdapterPathHistory.PathImpl> pathImplList = adapter.getSelectedItems();
                    for(AdapterPathHistory.PathImpl path : pathImplList) {
                        adapter.removeItem(path);
                        pathDao.delete(path.getPath());
                    }
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            FragmentPathHistory.this.actionMode = null;
            adapter.cancelCAB();
        }
    };

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            Ln.d("onScrollStateChanged() : newState : %d", newState);
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            Ln.d("onScrolled() : dx : %d, dy : %d", dx, dy);
            super.onScrolled(recyclerView, dx, dy);
        }
    };
}
