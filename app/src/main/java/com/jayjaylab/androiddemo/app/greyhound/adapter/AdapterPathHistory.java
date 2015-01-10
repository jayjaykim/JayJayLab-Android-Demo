package com.jayjaylab.androiddemo.app.greyhound.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.Path;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.event.OnClickEvent;
import com.jayjaylab.androiddemo.event.OnLongClickEvent;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import roboguice.event.EventManager;
import roboguice.util.Ln;

/**
 * Created by jongjoo on 11/22/14.
 */
public class AdapterPathHistory extends RecyclerView.Adapter<AdapterPathHistory.ViewHolder> {
    public static String TAG_CLICK_IN_NORMAL_MODE = "clicked_normal_mode";
    public static String TAG_CLICK_IN_SELECT_MODE = "clicked_select_mode";
    public static String TAG_CLICK_LOAD_MORE = "clicked_load_more";

    List<PathWrapper> items = new ArrayList<PathWrapper>(20);
    DateTimeFormatter fmt;
    ReadWriteLock readWriteLock;

    boolean isLongClicked = false;
    boolean isCABActivated;
    int countSelected = 0;

    @Inject Handler handler;
    @Inject EventManager eventManager;

    public AdapterPathHistory() {
//        fmt = DateTimeFormat.forPattern("yyyy MMMM d");
        fmt = DateTimeFormat.forStyle("FF").withLocale(Locale.KOREAN);
        readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        readWriteLock.readLock().lock();
        final PathWrapper pathWrapper = items.get(position);
        readWriteLock.readLock().unlock();

        if(pathWrapper != null) {
            if(pathWrapper.getPath() == null) {
                viewHolder.layoutContent.setVisibility(View.INVISIBLE);
                viewHolder.layoutNext.setVisibility(View.VISIBLE);

                viewHolder.buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // loads next 10 paths
                        eventManager.fire(new OnClickEvent(viewHolder.layout, position, TAG_CLICK_LOAD_MORE));
                    }
                });
            } else {
                viewHolder.layoutContent.setVisibility(View.VISIBLE);
                viewHolder.layoutNext.setVisibility(View.INVISIBLE);

                DateTime startDatetime = DateTime.parse(pathWrapper.getPath().getStartTime());
                DateTime endDatetime = DateTime.parse(pathWrapper.getPath().getEndTime());
                viewHolder.textviewDateTime.setText(fmt.print(startDatetime));
                viewHolder.textviewPathInfo.setText(fmt.print(endDatetime));
                if (pathWrapper.isSelected()) {
                    viewHolder.layout.setBackgroundResource(R.color.background_collection_selected);
                } else {
                    viewHolder.layout.setBackgroundResource(R.drawable.selector_gridcell_mainapp);
                }
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Ln.d("onClick()");
                        if (isLongClicked) {
                            isLongClicked = false;
                            return;
                        }

                        if(isCABActivated) {
                            pathWrapper.setSelected(!pathWrapper.isSelected);
                            countSelected = pathWrapper.isSelected() ? countSelected + 1 : countSelected - 1;
                            notifyItemChanged(position);
                            eventManager.fire(new OnClickEvent(viewHolder.layout, position, TAG_CLICK_IN_SELECT_MODE));
                        } else {
                            eventManager.fire(new OnClickEvent(viewHolder.layout, position, TAG_CLICK_IN_NORMAL_MODE));
                        }
                    }
                });
                viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(isCABActivated) {
                            pathWrapper.setSelected(!pathWrapper.isSelected);
                            countSelected = pathWrapper.isSelected() ? countSelected + 1 : countSelected - 1;
                            notifyItemChanged(position);
                            eventManager.fire(new OnClickEvent(viewHolder.layout, position, TAG_CLICK_IN_SELECT_MODE));
                        } else {
                            isCABActivated = true;
                            isLongClicked = true;
                            // activates CAB
                            pathWrapper.setSelected(!pathWrapper.isSelected);
                            countSelected = pathWrapper.isSelected() ? countSelected + 1 : countSelected - 1;
                            notifyItemChanged(position);
                            eventManager.fire(new OnLongClickEvent(viewHolder.layout, position));
                        }
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item_greyhound_path_history, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.layout = view.findViewById(R.id.layout);
        vh.layoutContent = view.findViewById(R.id.layout_content);
        vh.layoutNext = view.findViewById(R.id.layout_to_next);
        vh.textviewDateTime = (TextView)view.findViewById(R.id.textview_datetime);
        vh.textviewPathInfo = (TextView)view.findViewById(R.id.textview_pathinfo);
        vh.buttonNext = (Button)view.findViewById(R.id.button_next);

        return vh;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Adds a group of items at the bottom of the list. The items are from database.
     * @param paths a bunch of Path instances
     */
    public void addItems(List<Path> paths) {
        Ln.d("addItems() : paths : %s", paths);
        /**
         * Logic
         * pre condition :
         * post condition : "Load More" button must be at the bottom of the list always
         * until there's no more items to be shown.
         * flow : a bunch of items always get added at the bottom.
         */

        removeLoadMoreButton();

        final int sizePrev = items.size();
        final int sizePost = sizePrev + paths.size();
        ArrayList<PathWrapper> list = new ArrayList<PathWrapper>(paths.size());
        for(Path path : paths) {
            Ln.d("addItems() : path.id : %d", path.getId());
            list.add(PathWrapper.createPathImpl(path));
        }

        items.addAll(list);

        // this will causes problems because this method has some bugs reported
        // in Google bug tracking site
        for(int i = sizePrev; i < sizePost; i++) {
            notifyItemInserted(i);
        }

        addLoadMoreButton(paths);
    }

    protected void removeLoadMoreButton() {
        Ln.d("removeLoadMoreButton()");
        final int size = items.size();
        if(size == 0)
            return;

        PathWrapper pathWrapper = items.get(size - 1);
        if(pathWrapper.getPath() == null) {
            items.remove(items.size() - 1);
            notifyItemRemoved(size - 1);
        }
    }

    protected void addLoadMoreButton(List<Path> paths) {
        Ln.d("addLoadMoreButton()");
        if(paths.size() == 0)
            return;

        items.add(new PathWrapper(null, false));
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Adds item on top of list. The item is the record which has just been recorded.
     * @param path an instance of Path
     */
    public void addItem(final Path path) {
        Ln.d("addItem() : path : %s", path);

        handler.post(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                items.add(0, PathWrapper.createPathImpl(path));
                notifyItemInserted(0);
//                notifyDataSetChanged();
                readWriteLock.writeLock().unlock();
            }
        });
    }

    public long getSmallestId() {
        Ln.d("getSmallestId()");

        final int size = items.size();
        PathWrapper pathWrapper = null;
        for(int i = size - 1; i >= 0; i--) {
            pathWrapper = getItem(i);
            if(pathWrapper.getPath() != null) {
                return pathWrapper.getPath().getId();
            }
        }

        return -1;
    }

    public List<PathWrapper> getSelectedItems() {
        ArrayList<PathWrapper> list = new ArrayList<PathWrapper>(10);

        for(PathWrapper path : items) {
            if(path.isSelected) {
                list.add(path);
            }
        }

        return list;
    }

    public void removeItem(final PathWrapper pathWrapper) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                final int index = items.indexOf(pathWrapper);
                items.remove(index);
                notifyItemRemoved(index);
            }
        });
    }

    public void cancelCAB() {
        Ln.d("cancelCAB()");

        countSelected = 0;
        isCABActivated = false;
        for(PathWrapper path : items) {
            path.setSelected(false);
        }

        notifyDataSetChanged();
    }

    public int getCountSelected() {
        return countSelected;
    }

    public PathWrapper getItem(int position) {
        return items.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textviewDateTime;
        public TextView textviewPathInfo;
        public Button buttonNext;
        public View layout;
        public View layoutContent;
        public View layoutNext;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class PathWrapper {
        boolean isSelected;
        Path path;

        private PathWrapper(Path path, boolean isSelected) {
            this.path = path;
            this.isSelected = isSelected;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public static PathWrapper createPathImpl(Path path) {
            return new PathWrapper(path, false);
        }
    }
}
