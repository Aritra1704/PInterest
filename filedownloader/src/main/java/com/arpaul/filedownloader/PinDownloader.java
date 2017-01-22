package com.arpaul.filedownloader;

import android.content.Context;
import android.text.TextUtils;

import com.arpaul.filedownloader.webservices.PostParamBuilder;

import java.util.LinkedHashMap;

/**
 * Created by Aritra on 20-01-2017.
 */

public class PinDownloader {

    private DownloadInterface downloadInterface;
    private String strURL;
    private LinkedHashMap<String, String> paramSave;
    private Thread thread;
    private FileAsyncLoader helper;
    private int TIME_OUT = 3000;
    private boolean threadSuspended = false;

    public PinDownloader(DownloadInterface downloadInterface) {
        this.downloadInterface = downloadInterface;
    }

    public void loadURL(String strURL) {
        this.strURL = strURL;
    }

    public void saveDetail(String folderPath, String filename) {
        if(!TextUtils.isEmpty(folderPath) && !TextUtils.isEmpty(filename))
            paramSave = new PostParamBuilder().downloadFileParam(folderPath, filename);
    }

    public void setTimeout(int time_out) {
        this.TIME_OUT = time_out;
    }

    public void begin() {
        try {
            if(!TextUtils.isEmpty(strURL) && paramSave != null) {
                helper = new FileAsyncLoader(strURL, paramSave, downloadInterface);
                thread = new Thread(helper);
                thread.start();

                thread.join(TIME_OUT);
                if (thread.isAlive() && threadSuspended) {
                    thread.interrupt();
                    return;
                }/* else {
                }*/
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void stopThread() {
        if(thread != null && thread.isAlive() && helper != null) {
            threadSuspended = true;
        }
    }
}
