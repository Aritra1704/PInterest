package com.arpaul.pinterest.parsers;

import com.arpaul.filedownloader.webservices.WebServiceResponse;
import com.arpaul.pinterest.dataobject.ProfileDO;
import com.arpaul.utilitieslib.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ARPaul on 22-01-2017.
 */

public class URLParser {

    private WebServiceResponse response;

    public static final String TAG_USER             = "user";
    public static final String TAG_NAME             = "name";

    public static final String TAG_PROFILE_IMAGE    = "profile_image";
    public static final String TAG_MEDIUM           = "medium";

    public static final String TAG_URLS             = "urls";
    public static final String TAG_REGULAR          = "regular";

    public URLParser(WebServiceResponse response) {
        this.response = response;
    }

    public ArrayList<ProfileDO> parseData() {
        ArrayList<ProfileDO> arrProfileDO = null;
        ProfileDO objProfileDO = null;

        if(response.getResponseCode() == WebServiceResponse.SUCCESS){
            String resData = response.getResponseMessage();
            try {
                JSONArray arrGroup = new JSONArray(resData);

                if(arrGroup != null && arrGroup.length() > 0) {
                    arrProfileDO = new ArrayList<>();
                    for(int i= 0; i < arrGroup.length(); i++) {
                        objProfileDO = new ProfileDO();
                        JSONObject body = arrGroup.getJSONObject(i);

                        if(JSONUtils.hasJSONtag(body, TAG_USER)){
                            JSONObject user        = body.getJSONObject(TAG_USER);
                            if(JSONUtils.hasJSONtag(user, TAG_NAME))
                                objProfileDO.UserName    = user.getString(TAG_NAME);

                            if(JSONUtils.hasJSONtag(user, TAG_PROFILE_IMAGE)) {
                                JSONObject image        = user.getJSONObject(TAG_PROFILE_IMAGE);
                                if(JSONUtils.hasJSONtag(image, TAG_MEDIUM))
                                    objProfileDO.ProfileImage    = image.getString(TAG_MEDIUM);
                            }
                        }

                        if(JSONUtils.hasJSONtag(body, TAG_URLS)){
                            JSONObject urls        = body.getJSONObject(TAG_URLS);
                            if(JSONUtils.hasJSONtag(urls, TAG_REGULAR))
                                objProfileDO.UserImage    = urls.getString(TAG_REGULAR);
                        }

                        arrProfileDO.add(objProfileDO);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return arrProfileDO;
    }
}
