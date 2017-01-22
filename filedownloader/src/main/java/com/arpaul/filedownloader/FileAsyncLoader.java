package com.arpaul.filedownloader;

import com.arpaul.filedownloader.webservices.PostParamBuilder;
import com.arpaul.filedownloader.webservices.RestServiceCalls;
import com.arpaul.filedownloader.webservices.WEBSERVICE_TYPE;
import com.arpaul.filedownloader.webservices.WebServiceConstant;
import com.arpaul.filedownloader.webservices.WebServiceResponse;

import java.util.LinkedHashMap;

/**
 * Created by Aritra on 20-01-2017.
 */

public class FileAsyncLoader implements Runnable {

    private String strURL;
    private LinkedHashMap<String, String> paramSave;
    private DownloadInterface downloadInterface;


    public FileAsyncLoader(String strURL, LinkedHashMap<String, String> paramSave, DownloadInterface downloadInterface) {
        super();
        this.strURL = strURL;
        this.paramSave = paramSave;
        this.downloadInterface = downloadInterface;

    }

    @Override
    public void run() {
        synchronized (strURL) {
            WebServiceResponse response = new RestServiceCalls(strURL, null, paramSave, WEBSERVICE_TYPE.DOWNLOAD_FILE).getData();
            if(response.getResponseCode() == WebServiceResponse.SUCCESS){
                if(downloadInterface != null)
                    downloadInterface.getMessage(strURL, WebServiceConstant.STAT_SUCCESS);
            } else {
                if(downloadInterface != null)
                    downloadInterface.getMessage(strURL, WebServiceConstant.STAT_FAILURE);
            }
        }
    }

}
