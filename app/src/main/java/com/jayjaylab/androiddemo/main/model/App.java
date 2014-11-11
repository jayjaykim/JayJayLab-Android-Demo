package com.jayjaylab.androiddemo.main.model;

/**
 * Created by jongjoo on 11/11/14.
 */
public class App {
    String uri;
    String title;
    String description;

    public App(String uri, String title, String description) {
        this.uri = uri;
        this.title = title;
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
        builder.append("uri : " + uri);
        builder.append(", title : " + title);
        builder.append(", description : " + description);
        return builder.toString();
    }
}
