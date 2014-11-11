package com.jayjaylab.androiddemo.main;

import android.content.Context;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.main.model.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jongjoo on 11/11/14.
 */
public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {
    protected List<App> items = new ArrayList<App>(10);

    @Inject
    public AdapterMain(Provider<Context> context) {}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.grid_item_mainapp, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.imageThumbnail = (TintImageView)view.findViewById(R.id.image_thumbnail);
        vh.textTitle = (TextView)view.findViewById(R.id.text_title);
        vh.textDescription = (TextView)view.findViewById(R.id.text_description);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final App app = items.get(position);
        if(app != null) {
            // TODO sets thumbnail
            viewHolder.textTitle.setText(app.getTitle());
            viewHolder.textDescription.setText(app.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<App> apps) {
        items.addAll(apps);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TintImageView imageThumbnail;
        public TextView textTitle;
        public TextView textDescription;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
