package network;

import android.os.Environment;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import threading.BackgroundJob;
import threading.BackgroundJobClient;

/**
 * Created by webwerks on 13/7/15.
 */
public class PDFRequest extends BackgroundJob {
    NetworkRequest request;

    public PDFRequest(BackgroundJobClient client,NetworkRequest request) {
        super(client);
        this.request=request;
    }


    @Override
    public void run() {

        HttpURLConnection httpConn=null;
        try {
            URL url=new URL(request.getUrl());

            httpConn= (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(request.getType().toString());
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setDoInput(true);

            if(request.getHeaders()!=null) {
                for (Object key : request.getHeaders().keySet().toArray()) {

                    httpConn.setRequestProperty(key.toString(), request.getHeaders().get(key));
                }
            }

            for(Object key : httpConn.getRequestProperties().keySet().toArray()){
                Log.e("PROP", key + " property");
            }
            Log.e("METHOD", httpConn.getRequestMethod()+" method");

            switch(request.getType()){

                case GET :

                    break;
                case POST :
                    httpConn.setDoOutput(true);
                    String params=null;
                    if((params=request.getParameterAsString())!=null){

                        OutputStreamWriter outWriter=new OutputStreamWriter(httpConn.getOutputStream());
                        outWriter.write(params);
                        outWriter.close();
                    }
                    break;

            }

            httpConn.connect();

            int responseCode=  httpConn.getResponseCode();
            Log.e("CONNECTION", "STARTED "+responseCode);
            Map<String,List<String>> headers= httpConn.getHeaderFields();

        //    String resopnseString= readIS(httpConn.getInputStream(), 1024);

            File file=new File(Environment.getExternalStorageDirectory()+"/"+"viewer.pdf");
            if(file.exists()){
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                byte[] buffer = new byte[2048];
                int len1 = 0;
                InputStream in=httpConn.getInputStream();
                while ( (len1 = in.read(buffer)) > 0 ) {
                    fileOutputStream.write(buffer,0, len1);
                }
                fileOutputStream.close();
                notifyCompletion(request.getRequestCode(),file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                notifyAbort(request.getRequestCode(),e);
                e.printStackTrace();
            }





           /* NetworkResponse response=new NetworkResponse();
            response.setResponseCode(responseCode);
            response.setResponseHeaders(headers);
            response.setResponseString(resopnseString);*/




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

        GZIPInputStream gzis = new GZIPInputStream(stream);

        InputStreamReader reader = new InputStreamReader(gzis);
        BufferedReader in = new BufferedReader(reader);

        String readed=null;
        while ((readed = in.readLine()) != null) {
            System.out.println(readed);
        }


        return readed;
    }

}
