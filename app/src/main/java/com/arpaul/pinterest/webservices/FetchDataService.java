package com.arpaul.pinterest.webservices;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.arpaul.filedownloader.webservices.RestServiceCalls;
import com.arpaul.filedownloader.webservices.WEBSERVICE_TYPE;
import com.arpaul.filedownloader.webservices.WebServiceResponse;
import com.arpaul.pinterest.common.AppConstants;
import com.arpaul.pinterest.dataobject.ProfileDO;
import com.arpaul.pinterest.parsers.URLParser;

import java.util.ArrayList;

/**
 * Created by ARPaul on 22-01-2017.
 */

public class FetchDataService extends AsyncTaskLoader {

    WebServiceResponse response;
    private ArrayList<ProfileDO> arrProfileDO;

    public FetchDataService(Context context) {
        super(context);
    }

    @Override
    public ArrayList<ProfileDO> loadInBackground() {

        response = new RestServiceCalls(AppConstants.REST_URL, null, null, WEBSERVICE_TYPE.GET).getData();//2nd data

        arrProfileDO = new URLParser(response).parseData();

        return arrProfileDO;
    }
}
