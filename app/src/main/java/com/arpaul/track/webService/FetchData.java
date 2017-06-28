package com.arpaul.track.webService;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;


import com.arpaul.track.common.ApplicationInstance;
import com.arpaul.track.dataObjects.TrackDO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by Aritra on 01-08-2016.
 */
public class FetchData extends AsyncTaskLoader {

    private Context context;
    private String param;
    private WEBSERVICE_TYPE type;
    private WEBSERVICE_CALL call;
    private WebServiceResponse response;
    private Gson mGson;
    private Bundle bundle;
    String authorisation = "7077a87616779beedb57c6b231ebd906ce78c2178887c48e74905e6d327417f9";

    public FetchData(Context context, WEBSERVICE_TYPE type, WEBSERVICE_CALL call){
        super(context);
        this.context = context;
        this.type = type;
        this.call = call;
        mGson = new Gson();
    }

    public FetchData(Context context, String param, WEBSERVICE_TYPE type, WEBSERVICE_CALL call){
        super(context);
        this.context = context;
        this.type = type;
        this.call = call;
        this.param = param;
        mGson = new Gson();
    }

    public FetchData(Context context, Bundle bundle, WEBSERVICE_TYPE type, WEBSERVICE_CALL call){
        super(context);
        this.context = context;
        this.bundle = bundle;
        this.type = type;
        this.call = call;

        mGson = new Gson();
    }

    @Override
    public Object loadInBackground() {

        Object objReturn = response;
        switch (call){
            case REV_GEO:
                if(bundle != null) {
                    LinkedHashMap<String, String> listLatlng = (LinkedHashMap<String, String>) bundle.get("currentLatLng");
                    objReturn = getRevGeoData(listLatlng);
                }
                break;
        }

        return objReturn;
    }

    private TrackDO getRevGeoData(LinkedHashMap<String, String> listLatlng) {
        TrackDO address = null;
        try{

            response = new RestServiceCalls(ApplicationInstance.BASE_URL+ ApplicationInstance.REV_GEO_URL, authorisation, listLatlng, WEBSERVICE_TYPE.POST).getData();//2nd data

            if(response.getResponseCode() == WebServiceResponse.SUCCESS){
                JSONObject jsonObject;
                JSONObject responseObj = new JSONObject(response.getResponseMessage());
                if(responseObj.has(ApplicationInstance.addresses)) {
                    try {
                        JSONArray array = (JSONArray) responseObj.getJSONArray(ApplicationInstance.addresses);
                        jsonObject = array.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        jsonObject = new JSONObject();
                    }
                }else{
                    jsonObject = new JSONObject();
                }
                address = mGson.fromJson(jsonObject.toString(), new TypeToken<TrackDO>(){}.getType());
            } else
                address = null;
        } catch (Exception ex){
            ex.printStackTrace();
            address = null;
        } finally {

            return address;
        }
    }
}
