package com.jayjaylab.androiddemo.main.model;

/**
 * Created by jongjoo on 11/11/14.
 */
public class App {
    String thumbnailUri;
    int thumbnailResId;
    String title;
    String description;

    public App(String uri, int thumbnailResId, String title, String description) {
        this.thumbnailUri = uri;
        this.thumbnailResId = thumbnailResId;
        this.title = title;
        this.description = description;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public void setThumbnailResId(int thumbnailResId) {
        this.thumbnailResId = thumbnailResId;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("thumbnailUri : " + thumbnailUri);
        builder.append(", title : " + title);
        builder.append(", description : " + description);
        return builder.toString();
    }
}
