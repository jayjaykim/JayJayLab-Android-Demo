package com.jayjaylab.androiddemo.util;

import java.nio.channels.Channel;

import roboguice.util.Ln;

/**
 * Created by jongjoo on 12/6/14.
 */
public class NIOHelper {
    @SuppressWarnings("unused")
    public static void closeChannel(Channel channel) {
        if(channel == null)
            return;

        try {
            channel.close();
        } catch(Exception e) {
            Ln.e(e);
        }
    }
}
