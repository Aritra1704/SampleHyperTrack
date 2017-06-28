package com.arpaul.track.webService;


import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Aritra on 05-08-2016.
 */
public class PostParamBuilder {

    public LinkedHashMap<String, String> revGeoParam(double latitude, double longitude){
        LinkedHashMap<String, String> hashParam = new LinkedHashMap<>();
        hashParam.put("latitude", latitude + "");
        hashParam.put("longitude", longitude + "");

        return hashParam;
    }

    public String revGeoBody(double latitude, double longitude){
        StringBuilder sbNearbyParams = new StringBuilder();

        sbNearbyParams.append("&latitude=").append(latitude);
        sbNearbyParams.append("&longitude=").append(longitude);

        return sbNearbyParams.toString();
    }

    public String prepareParam(LinkedHashMap<String, String> hashParam) {
        StringBuilder strBuilder = new StringBuilder();

        if(hashParam != null && hashParam.size() > 0) {
            Set<String> keyset = hashParam.keySet();
            strBuilder.append("?");
            int i = 0;
            for (String key : keyset) {
                strBuilder.append(key + "=" + hashParam.get(key));
                if(i < keyset.size() - 1)
                    strBuilder.append("&");
                i++;
            }
        }
        return strBuilder.toString();
    }
}
