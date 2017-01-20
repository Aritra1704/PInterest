package com.arpaul.filedownloader.webservices;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Aritra on 05-08-2016.
 */
public class PostParamBuilder {

    public String prepareParam(LinkedHashMap<String, String> hashParam) {
        StringBuilder strBuilder = new StringBuilder();

        if(hashParam != null && hashParam.size() > 0) {
            Set<String> keyset = hashParam.keySet();
            strBuilder.append("?");
            int i = 0;
            for (String key : keyset) {
                i++;
                strBuilder.append(key + "=" + hashParam.get(key));
                if(i < keyset.size() - 1)
                    strBuilder.append("&");
            }
        }
        return strBuilder.toString();
    }

    public LinkedHashMap<String, String> downloadFileParam(String downloadPath, String fileName){
        LinkedHashMap<String, String> hashParam = new LinkedHashMap<>();

        hashParam.put("downloadPath", downloadPath);
        hashParam.put("fileName", fileName);

        return hashParam;
    }
}
