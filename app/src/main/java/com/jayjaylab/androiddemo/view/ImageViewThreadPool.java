package com.jayjaylab.androiddemo.view;

import android.content.Context;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Created by jongjoo on 11/2/14.
 */
public class ImageViewThreadPool extends ViewThreadPool<ImageView> {

    @Inject
    public ImageViewThreadPool(Provider<Context> contextProvider) {
        super(contextProvider);
    }

//    @Inject
//    public ImageViewThreadPool(Provider<Context> contextProvider, int poolSizeLimit) {
//        super(contextProvider, poolSizeLimit);
//    }

    @Override
    protected void generateViews() {
        checkPreCondition();

        for(int i = 0; i < poolSizeLimit; i++) {
            ViewHolder<ImageView> holder = new ViewHolder<ImageView>();
            holder.setView(new ImageView(contextProvider.get()));
            holder.setUsed(false);
            pool.put(holder.getView().hashCode(), holder);
        }
    }
}
