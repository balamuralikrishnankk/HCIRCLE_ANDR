package network;

import android.util.Log;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import threading.BackgroundJob;
import threading.BackgroundJobClient;

/**
 * Created by yashesh on 6/11/2015.
 */
public class SOAPNetworkJob extends BackgroundJob {
    SOAPRequest request;

    public SOAPNetworkJob(SOAPRequest request, BackgroundJobClient client) {
        super(client);
        this.request=request;
    }

    @Override
    public void run() {
        callSOAPWebService();
    }


    private boolean callSOAPWebService() {
        OutputStream out = null;
        int respCode = -1;
        boolean isSuccess = false;
        URL url = null;
        HttpURLConnection httpURLConnection = null;

        try {

            url = new URL(request.getBaseUrl());


            httpURLConnection = (HttpURLConnection) url.openConnection();

            do {
                // httpURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
                httpURLConnection.setRequestMethod(request.getType().toString());
                httpURLConnection
                        .setRequestProperty("Connection", "keep-alive");
                httpURLConnection
                        .setRequestProperty("Content-Type", request.getContentType().toString());
                httpURLConnection.setRequestProperty("SendChunked", "True");
                httpURLConnection.setRequestProperty("UseCookieContainer",
                        "True");
                HttpURLConnection.setFollowRedirects(false);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setUseCaches(true);
                httpURLConnection.setRequestProperty("Content-length",
                        getReqData(request).length + "");
                httpURLConnection.setReadTimeout(60 * 1000);
                 httpURLConnection.setConnectTimeout(60 * 1000);
                httpURLConnection.connect();

                out = httpURLConnection.getOutputStream();

                if (out != null) {
                    out.write(getReqData(request));
                    out.flush();
                }

                if (httpURLConnection != null) {
                    respCode = httpURLConnection.getResponseCode();
                    Log.e("respCode", ":" + respCode);

                }
            } while (respCode == -1);

            InputStream responce=null;
            // If it works fine
            if (respCode == 200) {
                String str="";
                try {
                     responce = httpURLConnection.getInputStream();
                    //   String str = convertStreamToString(responce);
                    str=convertStreamToString(responce);
                    //     Log.e(".....data.....", new String(str) + "");


                    Log.e("STRING RESPONSE",str);

                    XmlPullParser parser=    XmlPullParserFactory.newInstance().newPullParser();
                    //parser.setInput(responce, null);
                    parser.setInput(new StringReader(str));
                    int eventType = parser.getEventType();
                    boolean nextRequired=true;
                    while(nextRequired && (eventType!=XmlPullParser.END_DOCUMENT)){


                        Log.e("eventType",eventType+"   " + XmlPullParser.START_TAG);
                        switch (eventType) {
                            case XmlPullParser.START_TAG:

                                String tagname = parser.getName();
                                Log.e(".....data.....", tagname + "");
                                if (tagname.equalsIgnoreCase("soap:Body")) {
                                    // next tag is  response body.
                                    parser.nextTag();
                                    Log.e("TAG", parser.getName());
                                    parser.nextTag();
                                    String response="";
                                    Log.e("NEXT TEXT",(response= parser.nextText()));
                                    //StringBuffer buffer=new StringBuffer();
                                    // buffer.append(parser.nextText());
                                    //  Log.e("NEXT TEXT",parser.nextText());
                                    notifyCompletion(request.getRequestCode(), response);
                                    nextRequired=false;
                                }
                                break;
                        }

                        eventType=parser.next();
                    }



                    // String str
                    // =Environment.getExternalStorageDirectory().getAbsolutePath()+"/sunilwebservice.txt";
                    // File f = new File(str);
                    //
                    // try{
                    // f.createNewFile();
                    // FileOutputStream fo = new FileOutputStream(f);
                    // fo.write(b);
                    // fo.close();
                    // }catch(Exception ex){
                    // ex.printStackTrace();
                    // }
                }
                catch (XmlPullParserException xEx){
                    xEx.printStackTrace();
                    notifyCompletion(request.getRequestCode(),str);
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                    Log.e("ERROR",e1.toString());
                }
            } else {
                isSuccess = false;
                Log.e("ERROR", convertStreamToString(httpURLConnection.getErrorStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                out = null;
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
                httpURLConnection = null;
            }
        }
        return isSuccess;
    }

    public  String createSoapHeader() {
        String soapHeader = null;

        soapHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + ">";
        return soapHeader;
    }

    public  byte[] getReqData(SOAPRequest request) {
        StringBuilder requestData = new StringBuilder();

        requestData.append(createSoapHeader());

        String params=createSoapParameters(request);
        requestData
                .append("<soap:Body>"
                        + "<" + request.getActionName()
                        + " xmlns=\"" + request.getNameSpace() + "\">");
        if(params!=null) {
            requestData.append(params);
        }
        requestData.append("</"+request.getActionName()+"> </soap:Body> </soap:Envelope>");
        Log.e("SOAP PARAMS",requestData.toString()+"");
        return requestData.toString().trim().getBytes();
    }


    private String createSoapParameters(SOAPRequest request){
        if(request.getSoapParameters()!=null) {
            String parameString="";
            for (SOAPRequest.Parameter param : request.getSoapParameters()) {
                parameString+=   "<"+param.getName()+">"+param.getValue()+"</"+param.getName()+">\n";
            }
            return parameString;
        }
        return null;
    }

    private  String convertStreamToString(InputStream is)
            throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }


}
