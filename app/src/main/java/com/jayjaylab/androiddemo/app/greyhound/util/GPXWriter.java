package com.jayjaylab.androiddemo.app.greyhound.util;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.util.AndroidHelper;

import java.io.File;

import roboguice.util.Ln;

/**
 * Created by jongjoo on 11/29/14.
 */
public class GPXWriter {
    final long FREE_SPACE_LIMIT = 10 * 1024 * 1024;
    File currentFile;

    public boolean openFile(String dirPath, String fileName) {
        Ln.d("openFile() : dirPath : %s, fileName : %s", dirPath, fileName);

        if(!AndroidHelper.isExternalStorageWritable())
            return false;

        // checks whether there's enough storage
        final long freeSpace = AndroidHelper.getFreeSpace(dirPath);
        Ln.d("openFile() : freeSpace : %dbytes", freeSpace);
        if(freeSpace <= FREE_SPACE_LIMIT) {
            return false;
        }

        currentFile = new File(dirPath, fileName);
        Ln.d("openFile() : currentFile : %s", currentFile);
        // TODO add header

        return true;
    }

    public String snapshot() {
        Ln.d("snapshop()");

        StringBuilder builder = new StringBuilder();
        builder.append("currentFile : " + currentFile);

        return builder.toString();
    }

    public boolean isFileOpened() {
        Ln.d("isFileOpened()");
        if(currentFile == null)
            return false;

        if(!(currentFile.isFile() && currentFile.exists()))
            return false;

        return true;
    }

    public boolean isFileClosed() {
        Ln.d("isFileClosed()");

        if(currentFile == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isWriting() {
        Ln.d("isWriing()");
        // TODO
        return false;
    }

    public void closeFile() {
        Ln.d("closeFile()");

        if(currentFile == null)
            return;

        // TODO adds footer

        currentFile = null;
    }

    public void write(Location location) {
        Ln.d("write() : location : %s", location);
        // TODO
    }
}
