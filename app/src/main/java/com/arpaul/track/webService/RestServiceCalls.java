package com.arpaul.track.webService;

import android.text.TextUtils;

import com.arpaul.utilitieslib.FileUtils;
import com.arpaul.utilitieslib.LogUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.arpaul.track.webService.WEBSERVICE_TYPE.PUT;

/**
 * Created by Aritra on 01-08-2016.
 */
public class RestServiceCalls {

    private final String TAG = "RestServiceCalls";
    public static final String apiKey = "x-zippr-api-key";
    public static final String contentType = "Content-Type";
    protected static final String CONTENT_TYPE = "application/json";
    private String url = "";
    private String authorize = "";
//    private String params = "";
    private LinkedHashMap<String, String> params = null;
    private WEBSERVICE_TYPE type;
    private final int TIMEOUT = 10000;
    private WebServiceResponse responseDo;

    public RestServiceCalls(String url, String authorize, LinkedHashMap<String, String> params, WEBSERVICE_TYPE type){
        this.url                = url;
        this.authorize          = authorize;
        this.params             = params;
        this.type               = type;

        responseDo = new WebServiceResponse();

        switch (type){
            case GET:
                getOkHttp(url);
                break;

            case PUT:
            case POST:
                postOkHttp(url, params);
                break;

            case DOWNLOAD_FILE:
                String downloadPath = params.get("downloadPath");
                String fileName = params.get("fileName");
                downloadFileOkHttp(url, downloadPath, fileName);
                break;

            case UPLOAD_FILE:
                uploadFileOkHttp(url, params);
                break;
        }
    }

    public WebServiceResponse getData(){
        return responseDo;
    }

    /**
     * GET call using OkHTTP
     * @param URL
     */
    private void getOkHttp(String URL) {
        String param = "";
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            LogUtils.infoLog("getOkHttp", URL);

            if(params != null && params.size() > 0)
                param = new PostParamBuilder().prepareParam(params);

            Request.Builder builder = new Request.Builder();
            builder.url(URL+param);
            if(!TextUtils.isEmpty(authorize))
                builder.header(apiKey, authorize);

            builder.header(contentType, CONTENT_TYPE);

            Request request = builder.build();

            Response response = okHttpClient.newCall(request).execute();
            if(response != null) {

                int status = response.code();
                switch (status) {
                    case WebServiceConstant.STATUS_SUCCESS :
                        responseDo.setResponseCode(WebServiceResponse.SUCCESS);
                        responseDo.setResponseMessage(response.body().string());
                        break;

                    case WebServiceConstant.STATUS_FAILED:
                    default:
                        responseDo.setResponseCode(WebServiceResponse.FAILURE);
                        responseDo.setResponseMessage(response.body().string());
                        break;
                }
                LogUtils.debugLog(TAG, "" + response.code());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return;
        }
    }

    /**
     * Post data in JSON format.
     * @param URL
     * @param params
     */
    private void postOkHttp(String URL, LinkedHashMap<String, String> params) {

        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            LogUtils.infoLog("postOkHttp", URL);
            MultipartBody.Builder multiBuilder = new MultipartBody.Builder();

            multiBuilder.setType(MultipartBody.FORM);
            if(params != null && params.size() > 0) {
                Set<String> keyset = params.keySet();
                for (String key : keyset) {
                    multiBuilder.addFormDataPart(key, params.get(key));
                }
            }

            RequestBody requestBody = multiBuilder.build();

            Request.Builder builder = new Request.Builder();
            builder.url(URL);
            if(!TextUtils.isEmpty(authorize))
                builder.header(apiKey, authorize);

            builder.header(contentType, CONTENT_TYPE);

            if(type == PUT)
                builder.method("PUT", RequestBody.create(null, new byte[0])).put(requestBody);
            else
                builder.method("POST", RequestBody.create(null, new byte[0])).post(requestBody);

            Request request = builder.build();

            Response response = okHttpClient.newCall(request).execute();
            if(response != null) {

                int status = response.code();
                switch (status) {
                    case WebServiceConstant.STATUS_SUCCESS :
                        responseDo.setResponseCode(WebServiceResponse.SUCCESS);
                        responseDo.setResponseMessage(response.body().string());
                        break;

                    case WebServiceConstant.STATUS_FAILED:
                    default:
                        responseDo.setResponseCode(WebServiceResponse.FAILURE);
                        responseDo.setResponseMessage(response.body().string());
                        break;
                }
                LogUtils.debugLog(TAG, "" + response.code());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return;
        }
    }

    private void downloadFileOkHttp(String URL, String downloadPath, String fileName) {

        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(URL);

            Request request = builder.build();

            Response response = okHttpClient.newCall(request).execute();
            if(response != null) {

                int status = response.code();
                switch (status) {
                    case WebServiceConstant.STATUS_SUCCESS :
                    case WebServiceConstant.STATUS_CREATED :
                    case WebServiceConstant.STATUS_ACCEPTED :
                    case WebServiceConstant.STATUS_NO_CONTENT :
                        FileUtils.saveInputStreamAsFile(response.body().byteStream(), downloadPath, fileName);
                        responseDo.setResponseCode(WebServiceResponse.SUCCESS);
                        break;

                    case WebServiceConstant.STATUS_FAILED:
                    default:
                        responseDo.setResponseCode(WebServiceResponse.FAILURE);
                        responseDo.setResponseMessage(response.body().string());
                        break;
                }
                LogUtils.debugLog(TAG, "" + response.code());
//                LogUtils.debug(TAG, response.message());
//                LogUtils.debug(TAG, response.body().string());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return;
        }
    }

    String attachmentFileName = "bitmap.png";
    private void uploadFileOkHttp(String URL, LinkedHashMap<String, String> fileName) {

        attachmentFileName = fileName + ".png";

        try {

            String stFileName = fileName.get("fileName");
            File file = new File(stFileName);
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("Content-type", "image/png")
                    .addFormDataPart("file", attachmentFileName,
                        RequestBody.create(MediaType.parse("image/png"), file))
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(URL);
            if(!TextUtils.isEmpty(authorize))
                builder.header("Authorization", authorize);
            builder.post(formBody);

            Request request = builder.build();

            Response response = okHttpClient.newCall(request).execute();
            if(response != null) {

                int status = response.code();
                switch (status) {
                    case WebServiceConstant.STATUS_SUCCESS :
                        responseDo.setResponseCode(WebServiceResponse.SUCCESS);
                        responseDo.setResponseMessage(response.body().string());
                        break;

                    case WebServiceConstant.STATUS_FAILED:
                    default:
                        responseDo.setResponseCode(WebServiceResponse.FAILURE);
                        responseDo.setResponseMessage(response.body().string());
                        break;
                }
                LogUtils.debugLog(TAG, "" + response.code());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return;
        }
    }
}
