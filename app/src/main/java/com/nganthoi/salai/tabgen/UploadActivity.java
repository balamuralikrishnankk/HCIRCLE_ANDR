package com.nganthoi.salai.tabgen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import ListenerClasses.ListviewListeners;
import Utils.ImageCompression;
import Utils.InpuStreamConversion;
import Utils.Methods;

/**
 * Created by atul on 4/4/16.
 */
public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int RESULT_CODE=113;
    private JSONArray filenames=null;
    private ImageView imgFile;
    private EditText edtCaption;
    String type;
    String cameraUriPath;
    private Bundle bundle;
    private String ip,filePath,token,channel_id;
    private Button btnSend,btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            ip=bundle.getString("IP_VALUE");
            filePath=bundle.getString("FILE_PATH");
            token=bundle.getString("TOKEN");
            channel_id=bundle.getString("CHANNEL_ID");
            type=bundle.getString("TYPE");
        }
        initComponent();

        showImage(filePath, type);
    }
    public void initComponent(){
        imgFile=(ImageView)findViewById(R.id.imgFile);
        btnSend=(Button)findViewById(R.id.btnSend);
        btnCancel=(Button)findViewById(R.id.btnCancel);
            edtCaption=(EditText)findViewById(R.id.edtCaption);
        btnSend.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSend:
                try {
                    File imgFile1 = new  File(filePath);
                        if(filePath!=null) {
                            UploadFile uploadFile = new UploadFile(imgFile1.getAbsolutePath(), "http://" + ip + ":8065/api/v1/files/upload");
                            uploadFile.execute();
                        }else{
                            Log.v("WRONG","WRONG:::NULL");
                        }
                }catch(Exception e){

                    Methods.toastShort("Something went wrong.."+e.toString(),this);
                }
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }


    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
    public void showImage(String path,String type){
        File imgFile1 = new  File(path);
        if(imgFile1.exists()){
            if(type.contains("IMAGE"))
            {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(imgFile1));
                    imgFile.setImageBitmap(bitmap);
                }catch (Exception e){

                }
            }else if(type.contains("CAMERA")){
                try {
                    Bitmap myBitmap = ThumbnailUtils.createVideoThumbnail(imgFile1.getAbsolutePath(), MediaStore.Images.Thumbnails.MICRO_KIND);
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                    imgFile.setImageBitmap(myBitmap);
                }catch (Exception e){

                }
            }else {
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imgFile1.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                imgFile.setImageBitmap(bMap);
            }
        }
    }

    public class UploadFile extends AsyncTask<Void, String, String>{
        URL connectURL;
        String serverRespMsg,file_upload_uri=null;
        HttpURLConnection httpURLConn = null;
        DataOutputStream dos = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        // "----------------------------13820696122345";
        int bytesRead, bytesAvailable, bufferSize;
        int serverRespCode;
        byte[] buffer;
        int maxBufferSize = 1024*1024;
        String fileLocation=null;
        InputStream isr=null;

        public UploadFile(String sourceFileUri,String serverUploadPath){
            Log.e("CONVERSATION","UPLOAD FILE");
            fileLocation = sourceFileUri;
            file_upload_uri = serverUploadPath;
        }
        @Override
        protected void onPreExecute(){
            Methods.showProgressDialog(UploadActivity.this,"Please wait..");

                ImageCompression imageCompression=new ImageCompression(UploadActivity.this);
                fileLocation=imageCompression.compressImage(fileLocation);


        }
        @Override
        protected String doInBackground(Void... v){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            File sourceFile = new File(fileLocation);
            if(!sourceFile.isFile()){
                Toast.makeText(UploadActivity.this, "Source file does not exist", Toast.LENGTH_SHORT).show();
                return null;
            }
            else{
                try{
                    FileInputStream fis = new FileInputStream(sourceFile);
                    connectURL = new URL(file_upload_uri);
                    httpURLConn = (HttpURLConnection) connectURL.openConnection();
                    httpURLConn.setDoInput(true);
                    httpURLConn.setDoOutput(true);
                    httpURLConn.setRequestMethod("POST");
                    httpURLConn.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    httpURLConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" +boundary);
                    httpURLConn.setRequestProperty("Authorization", "Bearer " + token);
                    httpURLConn.setRequestProperty("files", fileLocation);
                    httpURLConn.setRequestProperty("channel_id", channel_id);
                    System.setProperty("http.keepAlive", "false");
                    httpURLConn.connect();
                    OutputStreamWriter osw = new OutputStreamWriter(httpURLConn.getOutputStream());
                    osw.write("files=" + fileLocation + "&channel_id=" + channel_id);
                    dos = new DataOutputStream(httpURLConn.getOutputStream());

                    dos.writeBytes(twoHyphens+boundary+lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"channel_id\""+lineEnd+lineEnd);
                    dos.writeBytes(channel_id+lineEnd);

                    dos.writeBytes(twoHyphens+boundary+lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\""+fileLocation + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    buffer=new byte[bufferSize];

                    bytesRead = fis.read(buffer,0,bufferSize);
                    int total = bytesAvailable;
                    while(bytesRead>0){
                        publishProgress(""+(int)((total-bytesAvailable)*100)/total);
                        dos.write(buffer,0,bufferSize);
                        bytesAvailable = fis.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fis.read(buffer,0,bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    osw.flush();
                    dos.flush();
                    serverRespCode = httpURLConn.getResponseCode();
                    serverRespMsg = httpURLConn.getResponseMessage();
                    System.out.println("File Upload Response: " + serverRespCode + " " + serverRespMsg);

                    if(serverRespCode==200){
                        //Toast.makeText(context,"Your file upload is successfully completed",Toast.LENGTH_LONG).show();
                        System.out.println("Your file upload is successfully completed");
                        isr = new BufferedInputStream(httpURLConn.getInputStream());
//                        progressDialog.setCancelable(true);
                    }
                    else{
                        System.out.println("Oops! Your file upload is failed");
                        isr = new BufferedInputStream(httpURLConn.getErrorStream());
//                        progressDialog.setCancelable(true);
                    }
                    fis.close();
                    dos.close();
                    osw.close();

                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("File Upload Exception here: " + e.toString());
//                    progressDialog.setCancelable(true);
                    return null;
                }//end try catch
            }
            return InpuStreamConversion.convertInputStreamToString(isr);
        }
        protected void onProgressUpdate(String... progress){
        }
        @Override
        protected void onPostExecute(String result){
//            Methods.closeProgressDialog();
            if(result!=null){
                Log.e("RESULT","Result: "+result);//printing out the server results

                try{
                    Intent intent=new Intent();
                    intent.putExtra("RESULT_STRING", result);
                    intent.putExtra("CAPTION",""+edtCaption.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                    JSONObject fileObject = new JSONObject(result);
                    if(serverRespCode==200) {
                        //assign the list of filenames in the global JSON array filename
                        filenames=fileObject.getJSONArray("filenames");
                        for (int i = 0; i < filenames.length(); i++) {
                            Log.e("FILE::::","file name: " + filenames.getString(i));
                        }
                    }//end if statement
                    else{

                    }
                    Methods.closeProgressDialog();
                }catch(Exception e){
                    System.out.println("Unable to read file details: "+e.toString());
                }
            }
            else {
                System.out.println("Response is null");
            }
            //Toast.makeText(context,result,Toast.LENGTH_LONG).show();
        }
    }//end of class UploadFile



}
