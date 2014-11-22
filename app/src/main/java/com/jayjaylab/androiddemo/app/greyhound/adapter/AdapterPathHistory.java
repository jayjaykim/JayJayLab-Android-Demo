package com.jayjaylab.androiddemo.app.greyhound.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jayjaylab.androiddemo.Path;
import com.jayjaylab.androiddemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jongjoo on 11/22/14.
 */
public class AdapterPathHistory extends RecyclerView.Adapter<AdapterPathHistory.ViewHolder> {
    List<Path> items = new ArrayList<Path>(20);

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Path path = items.get(position);
        if(path != null) {
            viewHolder.textviewDateTime.setText(path.getStartTime());
            viewHolder.textviewPathInfo.setText(path.getEndTime());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item_greyhound_path_history, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.textviewDateTime = (TextView)view.findViewById(R.id.textview_datetime);
        vh.textviewPathInfo = (TextView)view.findViewById(R.id.textview_pathinfo);

        return vh;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<Path> paths) {
        items.addAll(paths);
        // this will causes problems because this method has some bugs reported
        // in Google bug tracking site
        notifyDataSetChanged();
    }

    public Path getItem(int position) {
        return items.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textviewDateTime;
        public TextView textviewPathInfo;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
