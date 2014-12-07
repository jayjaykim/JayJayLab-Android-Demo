package com.jayjaylab.androiddemo.app.greyhound.util;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.util.AndroidHelper;
import com.jayjaylab.androiddemo.util.NIOHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import roboguice.util.Ln;

/**
 * Created by jongjoo on 11/29/14.
 */
public class GPXWriter {
    final int BUFFER_SIZE = 1024;
    final long FREE_SPACE_LIMIT = 10 * 1024 * 1024;

    ByteBuffer byteBuffer;
    File currentFile;
//    RandomAccessFile currentFile;
    FileChannel fileChannel;

    public GPXWriter() {
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

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

        try {
//            currentFile = new RandomAccessFile(dirPath + File.separator + fileName, "rw");
            currentFile = new File(dirPath, fileName);
            fileChannel = new FileOutputStream(currentFile).getChannel();
        } catch (Exception e) {
            Ln.e(e);
            NIOHelper.closeChannel(fileChannel);
            currentFile = null;
            return false;
        }
        Ln.d("openFile() : currentFile : %s", currentFile);


        // adds header
        if(!wrtieStringToFile(getHeader(fileName))) {
            return false;
        }

        return true;
    }

    protected boolean wrtieStringToFile(String text) {
        byteBuffer.clear();
        byteBuffer.put(text.getBytes());
        byteBuffer.flip();
        while(byteBuffer.hasRemaining()) {
            try {
                fileChannel.write(byteBuffer);
            } catch (Exception e) {
                Ln.e(e);
                // closes the fileChannel
                NIOHelper.closeChannel(fileChannel);
                currentFile = null;
                return false;
            }
        }

        return true;
    }

    protected String getHeader(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n");
        builder.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"gpx4j\" version=\"1.1\" ");
        builder.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        builder.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n");
        builder.append("\t<trk>\n");
        builder.append("\t\t<name>" + name);
        builder.append("</name>\n\t\t<desc>(null)</desc>\n\t\t<trkseg>\n");

        return builder.toString();
    }

    protected String getFooter() {
        return "\t\t</trkseg>\n\t</trk>\n</gpx>";
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

        if(currentFile == null) {
            return false;
        } else {
            return true;
        }
    }

    public void closeFile() {
        Ln.d("closeFile()");

        if(currentFile == null)
            return;

        // adds footer
        wrtieStringToFile(getFooter());

        currentFile = null;
        NIOHelper.closeChannel(fileChannel);
    }

    public void write(Location location) {
        Ln.d("write() : location : %s", location);
        // TODO
    }
}
