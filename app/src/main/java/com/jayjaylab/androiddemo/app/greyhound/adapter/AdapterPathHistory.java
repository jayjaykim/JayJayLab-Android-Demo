package com.jayjaylab.androiddemo.app.greyhound.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    List<PathImpl> items = new ArrayList<PathImpl>(20);
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
        final PathImpl pathImpl = items.get(position);
        readWriteLock.readLock().unlock();

        if(pathImpl != null) {
            DateTime startDatetime = DateTime.parse(pathImpl.getPath().getStartTime());
            DateTime endDatetime = DateTime.parse(pathImpl.getPath().getEndTime());
            viewHolder.textviewDateTime.setText(fmt.print(startDatetime));
            viewHolder.textviewPathInfo.setText(fmt.print(endDatetime));
            if(pathImpl.isSelected()) {
                viewHolder.layout.setBackgroundResource(R.color.background_collection_selected);
            } else {
                viewHolder.layout.setBackgroundResource(R.drawable.selector_gridcell_mainapp);
            }
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ln.d("onClick()");
                    if(isLongClicked) {
                        isLongClicked = false;
                        return;
                    }

                    if(isCABActivated) {
                        pathImpl.setSelected(!pathImpl.isSelected);
                        countSelected = pathImpl.isSelected() ? countSelected + 1 : countSelected - 1;
                        notifyItemChanged(position);
                        eventManager.fire(new OnClickEvent(viewHolder.layout, position));
                    }
                }
            });
            viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isCABActivated = true;
                    isLongClicked = true;
                    // activates CAB
                    pathImpl.setSelected(!pathImpl.isSelected);
                    countSelected = pathImpl.isSelected() ? countSelected + 1 : countSelected - 1;
                    notifyItemChanged(position);
                    eventManager.fire(new OnLongClickEvent(viewHolder.layout, position));
                    return false;
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item_greyhound_path_history, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.layout = view.findViewById(R.id.layout);
        vh.textviewDateTime = (TextView)view.findViewById(R.id.textview_datetime);
        vh.textviewPathInfo = (TextView)view.findViewById(R.id.textview_pathinfo);

        return vh;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<Path> paths) {
        ArrayList<PathImpl> list = new ArrayList<PathImpl>(paths.size());
        for(Path path : paths) {
            list.add(PathImpl.createPathImpl(path));
        }

        items.addAll(list);
        // this will causes problems because this method has some bugs reported
        // in Google bug tracking site
        notifyDataSetChanged();
    }

    public void addItem(final Path path) {
        Ln.d("addItem() : path : %s", path);

        handler.post(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                items.add(0, PathImpl.createPathImpl(path));
                notifyItemInserted(0);
//                notifyDataSetChanged();
                readWriteLock.writeLock().unlock();
            }
        });
    }

    public List<PathImpl> getSelectedItems() {
        ArrayList<PathImpl> list = new ArrayList<PathImpl>(10);

        for(PathImpl path : items) {
            if(path.isSelected) {
                list.add(path);
            }
        }

        return list;
    }

    public void removeItem(PathImpl pathImpl) {
        final int index = items.indexOf(pathImpl);
        items.remove(index);
        notifyItemRemoved(index);
    }

    public void cancelCAB() {
        Ln.d("cancelCAB()");

        countSelected = 0;
        isCABActivated = false;
        for(PathImpl path : items) {
            path.setSelected(false);
        }

        notifyDataSetChanged();
    }

    public int getCountSelected() {
        return countSelected;
    }

    public PathImpl getItem(int position) {
        return items.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textviewDateTime;
        public TextView textviewPathInfo;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class PathImpl {
        boolean isSelected;
        Path path;

        private PathImpl(Path path, boolean isSelected) {
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

        public static PathImpl createPathImpl(Path path) {
            return new PathImpl(path, false);
        }
    }
}
