package connectServer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SALAI on 2/17/2016.
 */
public class DownloadFiles extends AsyncTask<String,Void,String>{
    URL api_url;
    HttpURLConnection conn;
    public Context context;
    public int responseCode;
    public String responseMessage,errorMessage;
    String TokenId;
    private ProgressDialog mProgressDialog;

    public DownloadFiles(String url_path,Context _context,String token_id){
        try{
            api_url = new URL(url_path);
            context = _context;
            TokenId = token_id;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Downloading file..");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
        }catch(Exception e){
            System.out.println("Unable to set URL in DownloadFiles constructor: "+e.toString());
        }
    }

    @Override
    protected void onPreExecute(){mProgressDialog.show();}
    @Override
    protected String doInBackground(String... filename){
        InputStream isr=null;
        int count;
        try{
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + TokenId);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            System.out.println("Response Code: " + responseCode + "\nResponse message: " + responseMessage);
            if(responseCode == 200/*HttpURLConnection.HTTP_OK*/){
                int lenghtOfFile = conn.getContentLength();
                isr = new BufferedInputStream(api_url.openStream());
                File directory = new File(Environment.getExternalStorageDirectory() + "/sdcard/TabGen/files");
                if(!directory.isDirectory())//if directory does not exists
                    directory.mkdirs();//then make the directory
                OutputStream outputStream = new FileOutputStream("/sdcard/TabGen/files"+filename[0]);
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = isr.read(data)) != -1) {
                    total += count;
                    //publishProgress(""+(int)((total*100)/lenghtOfFile));
                    outputStream.write(data, 0, count);
                }

                outputStream.flush();
                outputStream.close();
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
        return "";
    }

    protected void onProgressUpdate(String... progress) {
        //Log.d("ANDRO_ASYNC",progress[0]);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        //mProgressDialog.show();
    }
    @Override
    protected void onPostExecute(String res){
        mProgressDialog.dismiss();
    }

}
