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

    public void begin() {
        if(!TextUtils.isEmpty(strURL) && paramSave != null) {
            FileAsyncLoader helper = new FileAsyncLoader(strURL, paramSave, downloadInterface);
            Thread thread = new Thread(helper);
            thread.start();
        }
    }
}
