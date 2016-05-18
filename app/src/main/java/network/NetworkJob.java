package network;

import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Map;

import threading.BackgroundJob;
import threading.BackgroundJobClient;

/**
 * Created by yashesh on 6/7/2015.
 */
public class NetworkJob extends BackgroundJob {

    NetworkRequest request;
    public NetworkJob(BackgroundJobClient client, NetworkRequest request) {
        super(client);
        this.request=request;
    }

    @Override
    public void run() {

        HttpURLConnection httpConn=null;
        try {
//            Authenticator.setDefault(new Authenticator() {
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(ConstantValues.Header.HEADER_EMAIL_VALUE,
//                            ConstantValues.Header.HEADER_PASSWORD_VALUE.toCharArray());
//                }
//            });
            URL url=new URL(request.getUrl());
            httpConn= (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(request.getType().toString());
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(60000);
            httpConn.setReadTimeout(300000);
            if(request.getHeaders()!=null) {
                for (Object key : request.getHeaders().keySet().toArray()) {

                    httpConn.setRequestProperty(key.toString(), request.getHeaders().get(key));
                }
            }

            for(Object key : httpConn.getRequestProperties().keySet().toArray()){
                Log.e("PROP", key+" property "+httpConn.getRequestProperty(key.toString()));
            }
            Log.e("METHOD", httpConn.getRequestMethod()+" method");

            switch(request.getType()){

             case GET :

                break;
             case POST :
                 httpConn.setDoOutput(true);
                 String params=null;
                    if((params=request.getParameterAsString())!=null){
                        Log.e("PARAMS",params);
                        OutputStreamWriter outWriter=new OutputStreamWriter(httpConn.getOutputStream());
                        outWriter.write(params);
                        outWriter.close();
                    }
                break;

            }

            httpConn.connect();

            int responseCode=  httpConn.getResponseCode();
            Log.e("CONNECTION", "STARTED "+responseCode);

            if(responseCode!=200){

                Log.e("ERROR MSG","\n"+readIS(httpConn.getErrorStream(),1024));

            }


            Map<String,List<String>> headers= httpConn.getHeaderFields();
            String resopnseString= readIS(httpConn.getInputStream(), 1024);
            Log.e("RESP STR",resopnseString+"");
            NetworkResponse response=new NetworkResponse();
            response.setResponseCode(responseCode);
            response.setResponseHeaders(headers);
            response.setResponseString(resopnseString);

            notifyCompletion(request.getRequestCode(),response);


        } catch (MalformedURLException e) {
            notifyAbort(request.getRequestCode(),e);
            e.printStackTrace();
        } catch (IOException e) {
            notifyAbort(request.getRequestCode(),e);
            e.printStackTrace();
        }finally {
            if(httpConn!=null){



                httpConn.disconnect();
            }
        }


    }
    // Reads an InputStream and converts it to a String.
    public String readIS(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        String readed = null;

          //  GZIPInputStream gzis = new GZIPInputStream(stream);

            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            StringBuffer respBuffer=new StringBuffer();
            while ((readed = in.readLine()) != null) {
                respBuffer.append(readed);
                Log.e("READ STRING",readed);
            }
            return respBuffer.toString();
    }





}
