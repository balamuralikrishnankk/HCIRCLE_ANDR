package connectServer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.nganthoi.salai.tabgen.ImageviewerActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ListenerClasses.DownloadListeners;
import readData.ReadFile;

/**
 * Created by SALAI on 2/17/2016.
 */
public class DownloadFiles extends AsyncTask<String,String,String> implements DownloadListeners{
    URL api_url;
    HttpURLConnection conn;
    public Context context;
    public int responseCode;
    public String responseMessage,errorMessage;
    String TokenId;
    private ProgressDialog mProgressDialog;
    public String file_name=null;
    DownloadListeners downloadListeners;

    public DownloadFiles(String url_path,Context _context,String token_id,DownloadListeners downloadListeners){
        try{
            this.downloadListeners=downloadListeners;
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
        InputStream isr;
        int count;
        file_name = filename[0];
        try{
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + TokenId);
            //conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            System.out.println("Response Code: " + responseCode + "\nResponse message: " + responseMessage);
            if(responseCode == 200/*HttpURLConnection.HTTP_OK*/){
                int lenghtOfFile = conn.getContentLength();
                //isr = new BufferedInputStream(api_url.openStream());
                isr = conn.getInputStream();
                /*
                //For accessing internal data storage
                File mydir = context.getDir("TabGen", Context.MODE_PRIVATE); //Creating an internal dir;
                File fileWithinMyDir = new File(mydir, filename[0]); //Getting a file within the dir.
                FileOutputStream output = new FileOutputStream(fileWithinMyDir); //Use the stream as usual to write into the file.
                */
                //For accessing external data storage
                File SDCardRoot = new File(Environment.getExternalStorageDirectory()+"/HCircle");
                if(!SDCardRoot.isDirectory())//if directory does not exists
                    SDCardRoot.mkdirs();//then make the directory
                File file = new File(SDCardRoot,file_name);
                FileOutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;
                while ((count = isr.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
            }
            else {
                //isr = new BufferedInputStream(conn.getErrorStream());
                //Toast.makeText(context,"Failed to download file",Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            responseCode=-1;
            System.out.println("Exception occurs here: " + e.toString());
            //Toast.makeText(context,"Oops! Failed to download file",Toast.LENGTH_LONG).show();
            return errorMessage;
        }
        return responseMessage;
    }

    protected void onProgressUpdate(String... progress) {
        Log.d("ANDRO_ASYNC", progress[0]);
        downloadListeners.onDataUpdate(true);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        //mProgressDialog.show();
    }
    @Override
    protected void onPostExecute(String res){
        if(responseCode==200) {
            mProgressDialog.dismiss();
            String destination_path = Environment.getExternalStorageDirectory()+"/HCircle";

            openFolder(context,destination_path);
        }else{
            mProgressDialog.setMessage(res);
            mProgressDialog.setCancelable(true);
        }
    }

    public void openFolder(final Context context,String path)
    {
        Log.v("IF","PATH:::"+file_name+" PATTERN:::");
        Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(txt|doc|csv|pdf|docx|ppt|pptx|xls|xlsx|rtf|wav|rar|zip|txt|mp3|mp4|3gp))$)");
        Matcher mtch = fileExtnPtrn.matcher(file_name);
        if(mtch.matches()){
            Intent intent=new Intent(context, ImageviewerActivity.class);
            intent.putExtra("FILENAME",""+Environment.getExternalStorageDirectory().getPath()
                    + "/HCircle/"+file_name);
            context.startActivity(intent);

        }else{
            openFile(context,Uri.fromFile(new File(path)),path);

        }

    }
    public static void openFile(Context context, Uri uri,String path) {
        // Create URI
//        File file=url;
//        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (path.toString().contains(".doc") || path.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(path.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(path.toString().contains(".ppt") || path.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(path.toString().contains(".xls") || path.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(path.toString().contains(".zip") || path.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(path.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(path.toString().contains(".wav") || path.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(path.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(path.toString().contains(".jpg") || path.toString().contains(".jpeg") || path.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(path.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(path.toString().contains(".3gp") || path.toString().contains(".mpg") || path.toString().contains(".mpeg") || path.toString().contains(".mpe") || path.toString().contains(".mp4") || path.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onDataUpdate(boolean flag) {

    }

    @Override
    public void donotNeedtoDownload(String path) {

    }
}
