package com.arpaul.filedownloader.webservices;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Aritra on 01-08-2016.
 */
public class WEBSERVICE_TYPE {
    public static final int GET             = 10;
    public static final int POST            = 20;
    public static final int DOWNLOAD_FILE   = 30;
    public static final int UPLOAD_FILE     = 40;

    @IntDef({GET, POST, DOWNLOAD_FILE, UPLOAD_FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WebServiceTypePref{};
}
