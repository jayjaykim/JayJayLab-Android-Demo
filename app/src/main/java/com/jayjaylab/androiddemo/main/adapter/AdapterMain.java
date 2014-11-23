package com.jayjaylab.androiddemo.main.adapter;

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
import com.jayjaylab.androiddemo.event.OnClickEvent;
import com.jayjaylab.androiddemo.main.model.App;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import roboguice.event.EventManager;
import roboguice.util.Ln;

/**
 * Created by jongjoo on 11/11/14.
 */
public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {
    List<App> items = new ArrayList<App>(10);
    @Inject Context context;
    @Inject EventManager eventManager;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Ln.i("onCreateViewHolder() : parent.h : " + parent.getHeight());

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item_mainapp, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.layout = view.findViewById(R.id.layout);
        vh.imageThumbnail = (TintImageView)view.findViewById(R.id.image_thumbnail);
        vh.textTitle = (TextView)view.findViewById(R.id.text_title);
        vh.textDescription = (TextView)view.findViewById(R.id.text_description);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                parent.getHeight() / 2));
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final App app = items.get(position);
        if(app != null) {
            try {
                if (app.getThumbnailResId() != 0) {
                    Picasso.with(context).load(app.getThumbnailResId()).
                            into(viewHolder.imageThumbnail);
                } else if (app.getThumbnailUri() != null) {
                    Picasso.with(context).load(app.getThumbnailUri()).
                            into(viewHolder.imageThumbnail);
                }
            } catch(Exception e) {
                Ln.e(e);
            }
            viewHolder.textTitle.setText(app.getTitle());
            viewHolder.textDescription.setText(app.getDescription());
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Ln.d("onClick() : v : %s", v);
//                }
//            });
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventManager.fire(new OnClickEvent(viewHolder.layout, position));
                }
            });
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
        public View layout;
        public TintImageView imageThumbnail;
        public TextView textTitle;
        public TextView textDescription;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
