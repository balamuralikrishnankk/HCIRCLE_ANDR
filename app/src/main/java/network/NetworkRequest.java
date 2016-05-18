package network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yashesh on 6/7/2015.
 */
public class NetworkRequest {

    protected  Map<String,String> headers,parameters;
    protected String url;
    protected ContentType contentType;
    protected MethodType type;
    protected int requestCode;

    public MethodType getType() {
        return type;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setType(MethodType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        if(headers==null){
            headers=new HashMap<String,String>();
        }
        return headers;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public Map<String, String> getParameters() {
        if(parameters==null){
            parameters=new HashMap<String,String>();
        }
        return parameters;
    }

    public String getParameterAsString(){
        if(getParameters()!=null) {
            switch (getContentType()) {


                case FORM_ENCODED:

                    return getQuery(getParameters());
                case JSON:


                    return getJsonQuery(getParameters());
                case MULTIPART_FORM:
                    break;
            }


}
        return  null;
    }


    public static enum ContentType{

        JSON("application/json"),MULTIPART_FORM("multipart/form-data"),FORM_ENCODED("application/x-www-form-urlencoded"),TEXT_XML("text/xml");

        private final String type;
        private ContentType(String type){
            this.type=type;
        }


        @Override
        public String toString() {
            return type;
        }
    }

    public static enum MethodType{

        GET("GET"),POST("POST");

        private final String type;

        private  MethodType(String type){
            this.type=type;
        }


    }


    public static class Builder{

        private  NetworkRequest request;

        public Builder(MethodType methodType,String url,int requestCode){

            request=new NetworkRequest();
            request.setUrl(url);
            request.setType(methodType);
            request.setRequestCode(requestCode);
        }


        public Builder addHeader(String key,String value){

            request.getHeaders().put(key,value);

            return this;

        }
        public Builder addParameter(String key,String value){

            request.getParameters().put(key, value);

            return this;

        }


        public NetworkRequest build(){
            return  request;
        }

        public Builder setContentType(ContentType type){

            request.getHeaders().put("content-type",type.toString());
            request.setContentType(type);
            return this;
        }



    }

    private String getQuery(Map<String,String> values)
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Object key : values.keySet().toArray())
        {
            if (first)
                first = false;
            else
                result.append("&");

         //   try {
              //  result.append(URLEncoder.encode(key.toString(), "UTF-8"));
                result.append(key.toString());

            result.append("=");
            result.append(values.get(key));
           /* } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
        }

        return result.toString();
    }

    private String getJsonQuery(Map<String,String> values)
    {
       // StringBuilder result = new StringBuilder();
        boolean first = true;
        JSONObject json=new JSONObject();
        for (Object key : values.keySet().toArray())
        {


            try {
                json.put(URLEncoder.encode(key.toString(), "UTF-8"), URLEncoder.encode(values.get(key), "UTF-8"));


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json.toString();
    }




}
