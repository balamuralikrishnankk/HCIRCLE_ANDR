package com.nganthoi.salai.tabgen;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import Utils.InpuStreamConversion;
import Utils.Methods;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import adapter.ReplyAdapter;
import connectServer.ConnectAPIs;
import readData.ReadFile;
import reply_pojo.ReplyInnerObject;

/**
 * Created by atul on 19/4/16.
 */
public class ReplyDialogActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REPLY_REQUEST_CODE=111;
    private ArrayList<ReplyInnerObject> innerObjectsList=new ArrayList<>();
    LinearLayout reveal_items;
    private String file_path=null;
    String token,channel_id,post_id,ip,channel_name;
    private Toolbar toolbar;
    private EditText messageEditText;
    private ReplyAdapter replyAdapter;
    private RelativeLayout rltaskbar;
    private PreferenceHelper preferenceHelper;
    private ImageView imgbackButton,imgAttachFile;
    private RecyclerView replyRecyclerview;
    private TextView txtchannelname;
    private Button btnAddComment;
    ImageView imgPhotos,imgAudios,imgVideos,imgDocuments;
    boolean hidden=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_dialog);
        preferenceHelper=new PreferenceHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferenceHelper=new PreferenceHelper(this);
        channel_name=preferenceHelper.getString("CHANNEL_NAME");
        channel_id=preferenceHelper.getString("CHANNEL_ID");
        post_id=getIntent().getStringExtra("POST_ID");
        ip=getIntent().getStringExtra("IP");
        token=getIntent().getStringExtra("TOKEN");
        if(NetworkHelper.isOnline(this)){
          Thread thread=new Thread(){
              @Override
              public void run() {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          ConnectAPIs connectAPIs = new ConnectAPIs("http://" + ip + ":8065/api/v1/channels/" + channel_id + "/post/" + post_id, token);
                          InputStream inputStream = connectAPIs.getReply();
                          String resp = connectAPIs.convertInputStreamToString(inputStream);
                          parseJson(resp);
                      }
                  });
              }
          };
            thread.start();
        }else{
            Methods.toastShort("Please check your internet connection..", this);
        }
        initComponents();
    }
    private void initComponents() {
        reveal_items=(LinearLayout)findViewById(R.id.reveal_items);
        imgAttachFile=(ImageView)findViewById(R.id.imgAttachFile);
        imgAttachFile.setOnClickListener(this);
        messageEditText=(EditText)findViewById(R.id.messageEditText);
        rltaskbar=(RelativeLayout)findViewById(R.id.rltaskbar);
        replyRecyclerview=(RecyclerView)findViewById(R.id.replyRecyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        replyRecyclerview.setLayoutManager(layoutManager);
        replyAdapter = new ReplyAdapter(this,innerObjectsList);
        replyRecyclerview.setAdapter(replyAdapter);
        getSupportActionBar().setTitle(""+channel_name);
        btnAddComment=(Button)findViewById(R.id.btnAddComment);
        btnAddComment.setOnClickListener(this);
        imgPhotos=(ImageView)findViewById(R.id.imgPhotos);
        imgPhotos.setOnClickListener(this);
        imgAudios=(ImageView)findViewById(R.id.imgAudios);
        imgAudios.setOnClickListener(this);
        imgVideos=(ImageView)findViewById(R.id.imgVideos);
        imgVideos.setOnClickListener(this);
        imgDocuments=(ImageView)findViewById(R.id.imgDocuments);
        imgDocuments.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgAttachFile:
                if (hidden) {
                    reveal_items.setVisibility(View.VISIBLE);
                    hidden=false;
                }else{
                    reveal_items.setVisibility(View.GONE);
                    hidden=true;
                }
                break;
            case R.id.btnAddComment:
                String messageText = messageEditText.getText().toString();
                if (TextUtils.isEmpty(messageText)||messageText.trim().length()==0) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("filenames",null);
                    jsonObject.put("channel_id", channel_id);
                    if(innerObjectsList.get(0).getParentId()==null){
                        jsonObject.put("root_id", ""+post_id);
                        jsonObject.put("parent_id", ""+post_id);
                    }else{
                        jsonObject.put("root_id", ""+innerObjectsList.get(0).getParentId());
                        jsonObject.put("parent_id", ""+innerObjectsList.get(0).getParentId());
                    }
                    jsonObject.put("Message", messageEditText.getText().toString()+"");
                    jsonObject.put("user_id", "");
                    sendMyMessage(jsonObject);
                    messageEditText.setText("");
                } catch (Exception e) {
                    System.out.print("Message Sending failed: " + e.toString());
                    Snackbar.make(v, "Oops! Message Sending failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            case R.id.imgAudios:
                hidePopupWindow();
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("audio/*");
                intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1, "Select a file from music player"), REPLY_REQUEST_CODE);
                break;
            case R.id.imgVideos:
                hidePopupWindow();
                Intent intent3 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent3.setType("video/*");
                startActivityForResult(Intent.createChooser(intent3, "Select a file from vidoes"), REPLY_REQUEST_CODE);
                break;
            case R.id.imgPhotos:
                hidePopupWindow();
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                startActivityForResult(Intent.createChooser(intent2, "Select a file photos"), REPLY_REQUEST_CODE);
                break;
            case R.id.imgDocuments:
                hidePopupWindow();
                Intent intent4 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Files.getContentUri("application/*"));
                intent4.setType("application/*");
                intent4.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent4, "Select a file from documents"), REPLY_REQUEST_CODE);
                break;
        }
    }
    public void hidePopupWindow(){
        if(!hidden) {
            reveal_items.setVisibility(View.GONE);
            hidden=false;
        }else{
            reveal_items.setVisibility(View.VISIBLE);
            hidden=true;
        }
    }

    private static String getMimeType(String fileUrl) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        try {
            Uri fileUri;
            switch (requestCode) {
                case REPLY_REQUEST_CODE: //file_path = readFile.getFilePath(fileUri,context);

                    if (data.getData() != null) {
                        fileUri = data.getData();
                        file_path = ReadFile.getPath(fileUri, this);
                        String mimetype = getMimeType(file_path);

                        Methods.toastShort(mimetype, this);
                        if (file_path != null) {
                            //System.out.println("File has been selected: "+file_path);
//                    Toast.makeText(context, "You have selected: "+file_path, Toast.LENGTH_SHORT).show();
                            if (file_path.contains(".mp3")) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                UploadFile uploadFile = new UploadFile(file_path, "http://" + ip + ":8065/api/v1/files/upload");
                                                uploadFile.execute();

                                            }
                                        });
                                    }
                                }).start();

                            } else if (mimetype.contains("application/")) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                UploadFile uploadFile = new UploadFile(file_path, "http://" + ip + ":8065/api/v1/files/upload");
                                                uploadFile.execute();

                                            }
                                        });
                                    }
                                }).start();
                            } else if (mimetype.contains("image/")) {

                                Intent intent = new Intent(this, UploadActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("IP_VALUE", ip);
                                bundle.putString("FILE_PATH", file_path);
                                bundle.putString("TOKEN", token);
                                bundle.putString("CHANNEL_ID", channel_id);
                                bundle.putString("TYPE", "IMAGE");
                                intent.putExtras(bundle);
//                                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
                            } else if (mimetype.contains("video/")) {
//
                                Intent intent = new Intent(this, UploadActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("IP_VALUE", ip);
                                bundle.putString("FILE_PATH", file_path);
                                bundle.putString("TOKEN", token);
                                bundle.putString("CHANNEL_ID", channel_id);
                                bundle.putString("TYPE", "VIDEO");
                                intent.putExtras(bundle);
//                                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
                            }
                            Log.e("CONVERSATION", "ONACTIVITY_RESULT.");
                        }
                    }
                    break;
//                case REQUEST_CODE_CAMERA:
////                    hidePopupWindow();
//                    Log.v("PATH","IMAGE:::"+cameraUri.getPath());
//                    if (cameraUri != null) {
//                        Intent intent = new Intent(this, UploadActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("IP_VALUE", ip);
//                        bundle.putString("FILE_PATH", cameraUri.getPath());
//                        bundle.putString("TOKEN", token);
//                        bundle.putString("CHANNEL_ID", channel_id);
//                        bundle.putString("TYPE", "CAMERA");
//                        intent.putExtras(bundle);
//                        startActivityForResult(intent, UPLOAD_REQUEST_CODE);
//                    }
//                    break;
//                case UPLOAD_REQUEST_CODE:
////                    hidePopupWindow();
//                    try {
//                        String result = data.getStringExtra("RESULT_STRING");
//                        String caption = data.getStringExtra("CAPTION");
//                        JSONObject fileObject = new JSONObject(result);
//                        filenames = fileObject.getJSONArray("filenames");
////                String messageText = messageEditText.getText().toString();
//                        JSONObject jsonObject = new JSONObject();
//
//                        if (filenames != null && filenames.length() > 0) {
//                            jsonObject.put("filenames", filenames);
//                        }
//                        jsonObject.put("channel_id", channel_id);
//                        jsonObject.put("root_id", "");
//                        jsonObject.put("parent_id", "");
//                        jsonObject.put("Message", caption);
//                        sendMyMessage(jsonObject);
//                        filenames = null;
//                    } catch (Exception e) {
//                        System.out.print("Message Sending failed: " + e.toString());
//                    }
//                    break;
                default:
                    Toast.makeText(this, "Invalid request code. You haven't selected any file", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Methods.toastShort("Oops! Something went wrong..", this);
        }
    }

    private void parseJson(String resp) {
        Log.v("RESPONSE","RESPONSE:::"+resp);
        try {
            JSONObject jsonObject = new JSONObject(resp);
            JSONObject jsonObject1=jsonObject.getJSONObject("posts");
            Log.v("OBJECT","OBJECT::"+jsonObject1);
            Iterator<String> keys = jsonObject1.keys();
            while( keys.hasNext() )
            {
                String key = keys.next();
                Log.v("category key", key);
                JSONObject innerJObject = jsonObject1.getJSONObject(key);
                Log.v("inner","inner::"+innerJObject.toString());
                ReplyInnerObject replyInnerObject=new Gson().fromJson(innerJObject.toString(), ReplyInnerObject.class);
                innerObjectsList.add(replyInnerObject);
            }
            replyAdapter.notifyDataSetChanged();
        }catch (Exception e){
                Log.v("ERROR","ERROR::"+e.toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class UploadFile extends AsyncTask<Void, String, String> {
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
            Methods.showProgressDialog(ReplyDialogActivity.this,"Please wait..");
        }
        @Override
        protected String doInBackground(Void... v){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            File sourceFile = new File(fileLocation);
            if(!sourceFile.isFile()){
                Toast.makeText(ReplyDialogActivity.this, "Source file does not exist", Toast.LENGTH_SHORT).show();
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
                        System.out.println("Your file upload is successfully completed");
                        isr = new BufferedInputStream(httpURLConn.getInputStream());
                    }
                    else{
                        System.out.println("Oops! Your file upload is failed");
                        isr = new BufferedInputStream(httpURLConn.getErrorStream());
                    }
                    fis.close();
                    dos.close();
                    osw.close();
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("File Upload Exception here: " + e.toString());
                    return null;
                }
            }
            return InpuStreamConversion.convertInputStreamToString(isr);
        }
        protected void onProgressUpdate(String... progress){

        }
        @Override
        protected void onPostExecute(String result){
            Methods.closeProgressDialog();
            if(result!=null){
                try{
                    JSONObject fileObject = new JSONObject(result);
                    if(serverRespCode==200) {
//                        filenames=fileObject.getJSONArray("filenames");
//                        if(file_path.contains(".mp3")){
//                            try{
//                                filenames=fileObject.getJSONArray("filenames");
//                                JSONObject jsonObject = new JSONObject();
//
//                                if(filenames!=null && filenames.length()>0){
//                                    jsonObject.put("filenames",filenames);
//                                }
//                                jsonObject.put("channel_id", channel_id);
//                                jsonObject.put("root_id", "");
//                                jsonObject.put("parent_id","");
//                                jsonObject.put("Message", " ");
//                                sendMyMessage(jsonObject);
//                                filenames=null;
//                            } catch (Exception e) {
//                                Log.v("MESSAGE","Message Sending failed");
//                                System.out.print("Message Sending failed: " + e.toString());
//                            }
//                        }
//                        for (int i = 0; i < filenames.length(); i++) {
//
//                            Log.e("FILE::::","file name: " + filenames.getString(i));
//                        }

                    }else {
                        Methods.toastShort("Sorry! File is too large..",ReplyDialogActivity.this);
                    }
                }catch(Exception e){
                    System.out.println("Unable to read file details: " + e.toString());

                }
            }
            else {
                System.out.println("Response is null");

            }
            //Toast.makeText(context,result,Toast.LENGTH_LONG).show();
        }
    }//end of class UploadFile


    private void createJson(){
//        try {
//
//            JSONObject fileObject = new JSONObject(result);
//            filenames = fileObject.getJSONArray("filenames");
////                String messageText = messageEditText.getText().toString();
//            JSONObject jsonObject = new JSONObject();
//
//            if (filenames != null && filenames.length() > 0) {
//                jsonObject.put("filenames", filenames);
//            }
//            jsonObject.put("channel_id", channel_id);
//            jsonObject.put("root_id", "");
//            jsonObject.put("parent_id", "");
//            jsonObject.put("Message", caption);
//            sendMyMessage(jsonObject);
//            filenames = null;
//        } catch (Exception e) {
//            System.out.print("Message Sending failed: " + e.toString());
//        }
    }
    private void sendMyMessage(JSONObject jsonMsg) {
        String last_timetamp;
        String link = "http://"+ip+":8065/api/v1/channels/"+channel_id+"/create";
        String response;
        try{
            ConnectAPIs messageAPI = new ConnectAPIs(link,token);
            response=InpuStreamConversion.convertInputStreamToString(messageAPI.sendData(jsonMsg));
            if(response!=null ){
                if(messageAPI.responseCode==200){
                    ReplyInnerObject replyInnerObject=new ReplyInnerObject();
                    System.out.println("Sending result: " + response);
                    Log.v("MESSAGE","RESPONSE::"+response);
                    try{
                        JSONObject json_obj= new JSONObject(response);
                        replyInnerObject.setId(json_obj.getString("id"));
                        replyInnerObject.setMessage(json_obj.getString("message"));
                        last_timetamp = json_obj.getString("create_at");
                        Long timestamp = Long.parseLong(last_timetamp);
                        Date date = new Date(timestamp);
//                        replyInnerObject.setDate(simpleDateFormat.format(date));

                        JSONArray files = json_obj.getJSONArray("filenames");
                        if(files.length()!=0){
//                            replyInnerObject.sefiles.getString(0));
                        }

                        displayMessage(replyInnerObject);
                    }catch(Exception e){
                        System.out.print("Chat Exception: "+e.toString());
                    }
                    file_path=null;
                }
                else{
                    try{
                        JSONObject json_obj= new JSONObject(response);
                        Toast.makeText(this, "" + json_obj.get("message"), Toast.LENGTH_LONG).show();
                    }catch(Exception e){
                        System.out.print("Chat Exception: "+e.toString());
                    }
                }

            }else
                Toast.makeText(this,"Failed to send message",Toast.LENGTH_LONG).show();

        }catch(Exception e){
            System.out.println("Sending error: " + e.toString());
        }
    }

    private void displayMessage(ReplyInnerObject replyInnerObject) {
        innerObjectsList.add(replyInnerObject);
        replyAdapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        replyRecyclerview.scrollToPosition(innerObjectsList.size()-1);
    }



}
