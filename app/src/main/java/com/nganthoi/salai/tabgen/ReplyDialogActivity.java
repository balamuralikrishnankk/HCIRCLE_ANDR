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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import ListenerClasses.AsyncCallback;
import Utils.FileUtils;
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
public class ReplyDialogActivity extends AppCompatActivity implements View.OnClickListener, AsyncCallback{
    private static final int REQUEST_CODE=111;
    public static final int UPLOAD_REQUEST_CODE=112;
    public static final int RESULT_CODE=113;
    public static final int REQUEST_CODE_CAMERA=114;
    private static final int REPLY_REQUEST_CODE=111;
    private ArrayList<ReplyInnerObject> innerObjectsList=new ArrayList<>();
    LinearLayout reveal_items;
    String last_timetamp="000000000";
    private JSONArray filenames=null;
    boolean flag;
    Uri cameraUri;
    String resultComment;
    private String file_path=null;
    String token,channel_id,post_id,ip,channel_name;
    private Toolbar toolbar;
    private ReplyAdapter replyAdapter;
    private RelativeLayout rltaskbar;
    private PreferenceHelper preferenceHelper;
    private ImageView imgbackButton,imgAttachFile;
    private RecyclerView replyRecyclerview;
    private TextView txtchannelname;
    private Button btnAddComment;
    ImageView imgPhotos,imgAudios,imgVideos,imgDocuments,imgCamera;
    boolean hidden=true;

    EditText messageEditText;
    ImageView writeImageButton,imgSentMessages,cameraImageButton,pickImageFile;

    ImageView imgParentMsg,imParentAttachment;
    TextView txtParentsender,txtParentMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_dialog);
        preferenceHelper=new PreferenceHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        imgParentMsg=(ImageView)findViewById(R.id.imgParentMsg);
        imParentAttachment=(ImageView)findViewById(R.id.imParentAttachment);
        txtParentsender=(TextView)findViewById(R.id.txtParentsender);
        txtParentMessage=(TextView)findViewById(R.id.txtParentMessage);


        writeImageButton=(ImageView)findViewById(R.id.writeImageButton);
        writeImageButton.setOnClickListener(this);
        imgSentMessages=(ImageView)findViewById(R.id.imgSentMessages);
        imgSentMessages.setOnClickListener(this);
        cameraImageButton=(ImageView)findViewById(R.id.cameraImageButton);
        cameraImageButton.setOnClickListener(this);
        pickImageFile=(ImageView)findViewById(R.id.pickImageFile);
        pickImageFile.setOnClickListener(this);
        reveal_items=(LinearLayout)findViewById(R.id.reveal_items);
//        imgAttachFile=(ImageView)findViewById(R.id.imgAttachFile);

        messageEditText=(EditText)findViewById(R.id.messageEditText);
//        rltaskbar=(RelativeLayout)findViewById(R.id.rltaskbar);
        replyRecyclerview=(RecyclerView)findViewById(R.id.replyRecyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        replyRecyclerview.setLayoutManager(layoutManager);
        replyAdapter = new ReplyAdapter(this,innerObjectsList);
        replyRecyclerview.setAdapter(replyAdapter);
        getSupportActionBar().setTitle(""+channel_name);
//        btnAddComment=(Button)findViewById(R.id.btnAddComment);
//        btnAddComment.setOnClickListener(this);
        imgPhotos=(ImageView)findViewById(R.id.imgPhotos);
        imgPhotos.setOnClickListener(this);
        imgAudios=(ImageView)findViewById(R.id.imgAudios);
        imgAudios.setOnClickListener(this);
        imgVideos=(ImageView)findViewById(R.id.imgVideos);
        imgVideos.setOnClickListener(this);
        imgDocuments=(ImageView)findViewById(R.id.imgDocuments);
        imgDocuments.setOnClickListener(this);
        imgCamera=(ImageView)findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);


        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    imgSentMessages.setVisibility(View.VISIBLE);
                    writeImageButton.setVisibility(View.GONE);
                } else {
                    writeImageButton.setVisibility(View.VISIBLE);
                    imgSentMessages.setVisibility(View.GONE);
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    imgSentMessages.setVisibility(View.VISIBLE);
                    writeImageButton.setVisibility(View.GONE);
                } else {
                    writeImageButton.setVisibility(View.VISIBLE);
                    imgSentMessages.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imgSentMessages.setVisibility(View.VISIBLE);
                    writeImageButton.setVisibility(View.GONE);
                } else {
                    writeImageButton.setVisibility(View.VISIBLE);
                    imgSentMessages.setVisibility(View.GONE);
                }

            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pickImageFile:
                if (hidden) {
                    reveal_items.setVisibility(View.VISIBLE);
                    hidden=false;
                }else{
                    reveal_items.setVisibility(View.GONE);
                    hidden=true;
                }
                break;
            case R.id.cameraImageButton:
//                hidePopupWindow();

                Intent intent5 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file1 =FileUtils.getImageFile();
                cameraUri=Uri.fromFile(file1);
                intent5.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file1));
                startActivityForResult(intent5, REQUEST_CODE_CAMERA);
                break;
            case R.id.imgSentMessages:
                String messageText = messageEditText.getText().toString();
                if (TextUtils.isEmpty(messageText)||messageText.trim().length()==0) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject();

                    if(filenames!=null && filenames.length()>0){
                        jsonObject.put("filenames",filenames);
                    }
                    jsonObject.put("channel_id", channel_id);

                    jsonObject.put("root_id", ""+post_id);
                    jsonObject.put("parent_id", "" + post_id);
                    jsonObject.put("Message", messageEditText.getText().toString() + "");
                    jsonObject.put("user_id", "" + preferenceHelper.getString("USER_ID"));
                    sendMyMessage(jsonObject);
                    messageEditText.setText("");
                    filenames=null;
                } catch (Exception e) {
                    System.out.print("Message Sending failed: " + e.toString());
                    Snackbar.make(v, "Oops! Message Sending failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
//                messageText
//                imgSentMessages.setVisibility(View.GONE);

                break;
            case R.id.imgAudios:
                hidePopupWindow();
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("audio/*");
                intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1, "Select a file from music player"), REQUEST_CODE);
                break;
            case R.id.imgVideos:
                hidePopupWindow();
                Intent intent3 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent3.setType("video/*");
                startActivityForResult(Intent.createChooser(intent3, "Select a file from vidoes"), REQUEST_CODE);
                break;
            case R.id.imgPhotos:
                hidePopupWindow();
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                startActivityForResult(Intent.createChooser(intent2, "Select a file photos"), REQUEST_CODE);
                break;
            case R.id.imgDocuments:
                hidePopupWindow();
                Intent intent4 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Files.getContentUri("application/*"));
                intent4.setType("application/*");
                intent4.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent4, "Select a file from documents"), REQUEST_CODE);
                break;
            case R.id.imgCamera:
                hidePopupWindow();
//                ((ConversationActivity)getApplicationContext()).hasPermissionInManifest(this, "android.permission.CAMERA");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = FileUtils.getImageFile();
                cameraUri=Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
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
        Log.v("ONACTIVITYRESULT","ONACTIVITYRESULT");
        try {
            Uri fileUri;
            switch (requestCode) {
                case REQUEST_CODE: //file_path = readFile.getFilePath(fileUri,context);

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
                                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
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
                                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
                            }
                            Log.e("CONVERSATION", "ONACTIVITY_RESULT.");
                        }
                    }
                    break;
                case REQUEST_CODE_CAMERA:
//                    hidePopupWindow();
                    Log.v("PATH","IMAGE:::"+cameraUri.getPath());
                    if (cameraUri != null) {
                        Intent intent = new Intent(this, UploadActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("IP_VALUE", ip);
                        bundle.putString("FILE_PATH", cameraUri.getPath());
                        bundle.putString("TOKEN", token);
                        bundle.putString("CHANNEL_ID", channel_id);
                        bundle.putString("TYPE", "CAMERA");
                        intent.putExtras(bundle);
                        startActivityForResult(intent, UPLOAD_REQUEST_CODE);
                    }
                    break;
                case UPLOAD_REQUEST_CODE:
//                    hidePopupWindow();
                    try {
                        String result = data.getStringExtra("RESULT_STRING");
                        String caption = data.getStringExtra("CAPTION");
                        JSONObject fileObject = new JSONObject(result);
                        filenames = fileObject.getJSONArray("filenames");
//                String messageText = messageEditText.getText().toString();
                        JSONObject jsonObject = new JSONObject();

                        if (filenames != null && filenames.length() > 0) {
                            jsonObject.put("filenames", filenames);
                        }
                        jsonObject.put("channel_id", channel_id);
                        jsonObject.put("root_id", ""+post_id);
                        jsonObject.put("parent_id", ""+post_id);
                        jsonObject.put("Message", caption);
                        jsonObject.put("user_id",""+preferenceHelper.getString("USER_ID"));
                        sendMyMessage(jsonObject);
                        filenames = null;
                    } catch (Exception e) {
                        System.out.print("Message Sending failed: " + e.toString());
                    }
                    break;
                default:
                    Toast.makeText(this, "Invalid request code. You haven't selected any file", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Methods.toastShort("Oops! Something went wrong..",this);
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
            for(int i=0;i<innerObjectsList.size();i++){
                if(innerObjectsList.get(i).getId().equals(post_id)){
                    Collections.swap(innerObjectsList,0,i);
                }
            }
            if(innerObjectsList.get(0).getUserId()!=null){
                OkHttpClient picassoClient1 = new OkHttpClient();
                picassoClient1.networkInterceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                });

                Picasso.with(this).setIndicatorsEnabled(true);
                new Picasso.Builder(this).loggingEnabled(BuildConfig.DEBUG)
                        .indicatorsEnabled(BuildConfig.DEBUG)
                        .downloader(new OkHttpDownloader(picassoClient1)).build()
                        .load("http://"+ip+":8065/api/v1/users/"+innerObjectsList.get(0).getUserId()+"/image")
                        .error(R.drawable.username)
                        .into(imgParentMsg);
            }
            txtParentsender.setText(""+getUsernameById(innerObjectsList.get(0).getUserId()));
            txtParentMessage.setText(""+innerObjectsList.get(0).getMessage()+"");
            OkHttpClient picassoClient = new OkHttpClient();
            picassoClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                }
            });

            Picasso.with(this).setIndicatorsEnabled(true);
            new Picasso.Builder(this).loggingEnabled(BuildConfig.DEBUG)
                    .indicatorsEnabled(BuildConfig.DEBUG)
                    .downloader(new OkHttpDownloader(picassoClient)).build()
                    .load("http://"+ip+":8065/api/v1/files/get"+innerObjectsList.get(0).getFilenames().get(0))
                    .resize(300,300)
                    .error(R.drawable.place_holder)
                    .into(imParentAttachment);
            innerObjectsList.remove(0);
            replyAdapter.notifyDataSetChanged();
        }catch (Exception e){
                Log.v("ERROR", "ERROR::" + e.toString());
        }
    }
    private String getUsernameById(String user_id){
        String users= preferenceHelper.getString("all_users");

        String username=null;
        try{
            JSONObject all_users=new JSONObject(users);
            Log.v("USERS","USERS OBJECT:::"+all_users);
        /*if(members!=null){
            try{
                for(int i=0;i<members.length();i++){
                    JSONObject users = members.getJSONObject(i);
                    if(user_id.equals(users.getString("id"))){
                        username = users.getString("username");
                        break;
                    }
                }
            }catch(JSONException e){
                System.out.println("Unable to get Username in getUsernameById: "+e.toString());
                username=null;
            }
        }*/
            if(all_users!=null){
                try{
                    JSONObject jobj = all_users.getJSONObject(user_id);

                    if(jobj.getString("first_name").length()!=0 && jobj.getString("first_name")!=null){
                        username = jobj.getString("first_name");
                    }
                    else{
                        username = jobj.getString("username");
                    }
                }
                catch(Exception e){
                    username = null;
                }
            }
            else username="Unknown";
        }catch (Exception e){

        }

        return username;

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

    @Override
    public void getResult(String result, boolean flag,int responsecode) {
        if(flag){
            if(result!=null ){
                if(responsecode==200){
                    ReplyInnerObject replyInnerObject=new ReplyInnerObject();
                    try{
                        JSONObject json_obj= new JSONObject(result);
                        replyInnerObject.setId(json_obj.getString("id"));
                        replyInnerObject.setMessage(json_obj.getString("message"));
                        last_timetamp = json_obj.getString("create_at");
                        replyInnerObject.setUserId(json_obj.getString("user_id"));
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
                        JSONObject json_obj= new JSONObject(result);
                        Toast.makeText(this, "" + json_obj.get("message"), Toast.LENGTH_LONG).show();
                    }catch(Exception e){
                        System.out.print("Chat Exception: "+e.toString());
                    }
                }

            }else
                Toast.makeText(this,"Failed to send message",Toast.LENGTH_LONG).show();

        }else{
            flag=false;
            resultComment=null;
        }
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

    private void sendMyMessage(JSONObject jsonMsg) {
        String link = "http://"+ip+":8065/api/v1/channels/"+channel_id+"/create";
        String response;
        try{
            ConnectAPIs messageAPI = new ConnectAPIs(link,token);
            messageAPI.sendComment(jsonMsg,this,this);
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
