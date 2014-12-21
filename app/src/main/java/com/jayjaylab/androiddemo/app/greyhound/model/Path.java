package com.jayjaylab.androiddemo.app.greyhound.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class contains an instance of database Path entity produced by GreenDao Framework.<br>
 * The purpose of this class is to provide a parcelable class of the database entity created by
 * GreenDao framework.
 *
 * Created by jongjoo on 12/7/14.
 */
public class Path implements Parcelable {
    com.jayjaylab.androiddemo.Path pathEntity;

    public Path() {
        if(pathEntity == null) {
            pathEntity = new com.jayjaylab.androiddemo.Path();
        }
    }

    public Path(Parcel in) {
        if(pathEntity == null) {
            pathEntity = new com.jayjaylab.androiddemo.Path();
        }

        readFromParcel(in);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id : " + pathEntity.getId());
        builder.append(", startTime : " + pathEntity.getStartTime());
        builder.append(", endTime : " + pathEntity.getEndTime());
        builder.append(", gpxPath : " + pathEntity.getGpxPath());

        return builder.toString();
    }

    public com.jayjaylab.androiddemo.Path getPathEntity() {
        return pathEntity;
    }

    protected void readFromParcel(Parcel in) {
        pathEntity.setId(in.readLong());
        pathEntity.setStartTime(in.readString());
        pathEntity.setEndTime(in.readString());
        pathEntity.setGpxPath(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(pathEntity.getId());
        dest.writeString(pathEntity.getStartTime());
        dest.writeString(pathEntity.getEndTime());
        dest.writeString(pathEntity.getGpxPath());
    }

    public static final Parcelable.Creator<Path> CREATOR =
            new Parcelable.Creator<Path>() {
                @Override
                public Path createFromParcel(Parcel source) {
                    return new Path(source);
                }

                @Override
                public Path[] newArray(int size) {
                    return new Path[size];
                }
            };
}
