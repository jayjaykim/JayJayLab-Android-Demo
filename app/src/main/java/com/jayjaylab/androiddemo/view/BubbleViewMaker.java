package com.jayjaylab.androiddemo.view;

import android.widget.ImageView;

/**
 * Created by jongjoo on 11/3/14.
 */
public class BubbleViewMaker {
    /**
     * Makes the given <code>view</code> into bubble image
     * @param view  an instance of ImageView to have bubble image in it
     * @param bubbleResourceId  resource id of bubble image
     * @return  an instance of Imageview which has bubble image in it
     */
    public static ImageView make(ImageView view, int bubbleResourceId) {
        view.setImageResource(bubbleResourceId);
        // TODO sets size and position

        return view;
    }

    /**
     * Animates the given <code>view</code> by scaling, translating
     * @param view  an instance of ImageView to be animated
     * @return  an instance of ImageView which is animating
     */
    public static ImageView animate(ImageView view) {
        // TODO animates

        return  view;
    }
}
