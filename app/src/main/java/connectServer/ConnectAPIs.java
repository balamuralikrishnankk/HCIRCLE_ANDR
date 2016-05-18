package connectServer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ListenerClasses.AsyncCallback;
import Utils.Methods;

/**
 * Created by SALAI on 2/22/2016.
 */

public class ConnectAPIs {
    InputStream isr=null;
    public int responseCode;
    AsyncCallback asyncCallback;
    Context context;
    public String responseMessage, errorMessage,TokenId=null;
    URL api_url=null;
    public HttpURLConnection conn=null;
    public ConnectAPIs(String web_api,String token){
        try{
            api_url =new URL(web_api);
            TokenId = token;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public InputStream getData(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + TokenId);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            System.out.println("Response Code: " + responseCode + "\nResponse message: " + responseMessage);
            if(responseCode == 200/*HttpURLConnection.HTTP_OK*/){
                isr = new BufferedInputStream(conn.getInputStream());
            }
            else {
                isr = new BufferedInputStream(conn.getErrorStream());
            }
        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            responseCode=-1;
            System.out.println("Exception occurs here: " + e.toString());
        }
        return isr;
    }//end of getData function
    public InputStream sendData(JSONObject parameters){
        OutputStream os;
        OutputStreamWriter osw;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        InputStream isr;
        try{
            //api_url = new URL(api_link);
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer "+TokenId);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            os = conn.getOutputStream();
            osw = new OutputStreamWriter(os);
            osw.write(parameters.toString());
            osw.flush();
            responseCode = conn.getResponseCode(); //it only the code 200
            responseMessage = conn.getResponseMessage();// it is the json response from the mattermost api
            System.out.println("Response Code: "+responseCode+"\nResponse message: "+responseMessage);
            if(responseCode == 200) {
                isr = new BufferedInputStream(conn.getInputStream());
            }
            else{
                isr = new BufferedInputStream(conn.getErrorStream());
            }
            osw.close();
        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            responseCode=-1;
            System.out.println("Unable to send Exception occurs here: " + e.toString());
            isr = null;
        }
        return isr;
    }



    public InputStream getComment(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + TokenId);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            System.out.println("Response Code: " + responseCode + "\nResponse message: " + responseMessage);
            if(responseCode == 200/*HttpURLConnection.HTTP_OK*/){
                isr = new BufferedInputStream(conn.getInputStream());
            }
            else {
                isr = new BufferedInputStream(conn.getErrorStream());
            }
        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            responseCode=-1;
            System.out.println("Exception occurs here: " + e.toString());
        }
        return isr;
    }//end of getData function


    public void sendComment(JSONObject parameters,Context context,AsyncCallback asyncCallback){
        this.context=context;
        this.asyncCallback=asyncCallback;
        LongOperation longOperation=new LongOperation(parameters,asyncCallback);
        longOperation.execute();

    }


    private class LongOperation extends AsyncTask<String, Void, String> implements AsyncCallback{
        JSONObject parameters;
        AsyncCallback asyncCallback;
        LongOperation(JSONObject parameters,AsyncCallback asyncCallback){
            this.parameters=parameters;
            this.asyncCallback=asyncCallback;
        }

        @Override
        protected void onPreExecute() {
            Methods.showProgressDialog(context,"Please wait..");
        }


        @Override
        protected String doInBackground(String... params) {
            OutputStream os;
            OutputStreamWriter osw;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            InputStream isr;
            try{
                //api_url = new URL(api_link);
                conn = (HttpURLConnection) api_url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Bearer "+TokenId);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                os = conn.getOutputStream();
                osw = new OutputStreamWriter(os);
                osw.write(parameters.toString());
                osw.flush();
                responseCode = conn.getResponseCode(); //it only the code 200
                responseMessage = conn.getResponseMessage();// it is the json response from the mattermost api
                System.out.println("Response Code: "+responseCode+"\nResponse message: "+responseMessage);
                if(responseCode == 200) {
                    isr = new BufferedInputStream(conn.getInputStream());
                }
                else{
                    isr = new BufferedInputStream(conn.getErrorStream());
                }
                osw.close();
            }catch(Exception e){
                Log.v("ERROR","ERROR::"+e.toString());
                e.printStackTrace();
                errorMessage = e.toString();
                responseCode=-1;
                System.out.println("Unable to send Exception occurs here: " + e.toString());
                isr = null;
            }
            return convertInputStreamToString(isr);
        }

        @Override
        protected void onPostExecute(String result) {
            Methods.closeProgressDialog();
            if(result!=null) {
                asyncCallback.getResult(result,true,responseCode);
                Log.v("RESULT", "RESULT::" + result);
            }else{
                asyncCallback.getResult(null,false,responseCode);
            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        public void getResult(String result, boolean flag,int code) {

        }
    }



    public String convertInputStreamToString(InputStream inputStream){
        String result=null;
        if(inputStream!=null){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line=reader.readLine())!=null){
                    sb.append(line +"\n");
                }
                inputStream.close();
                result = sb.toString();
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("We have found an exception: \n"+e.toString());
            }
        }
        return result;
    }
    public InputStream getReply(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("Authorization", "Bearer " + TokenId);
            conn.setRequestMethod("GET");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
            conn.connect();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            System.out.println("Response Code: " + responseCode + "\nResponse message: " + responseMessage);
            if(responseCode == 200/*HttpURLConnection.HTTP_OK*/){
                isr = new BufferedInputStream(conn.getInputStream());
                Log.v("POSTO","INPUTSTREAM:::"+isr);
            }
            else {
                isr = new BufferedInputStream(conn.getErrorStream());
            }
        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            responseCode=-1;
            System.out.println("Exception occurs here: " + e.toString());
        }
        return isr;
    }//end of getDa
}//end of ConnApi class