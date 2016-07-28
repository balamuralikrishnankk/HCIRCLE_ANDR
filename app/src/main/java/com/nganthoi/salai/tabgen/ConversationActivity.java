package com.nganthoi.salai.tabgen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Channel.Channel;
import Channel.GetChannelDetails;
import ListenerClasses.ListviewListeners;
import Utils.EndlessRecyclerOnScrollListener;
import Utils.FileUtils;
import Utils.Methods;
import Utils.PreferenceHelper;
import chattingEngine.ChatConversationAdapter;
import chattingEngine.ChatMessage;
import customDialogManager.CustomDialogManager;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import readData.ReadFile;
import sharePreference.SharedPreference;
import connectServer.ConnectAPIs;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener,ListviewListeners{
    private static final int REQUEST_CODE=111;
    public static final int UPLOAD_REQUEST_CODE=112;
    public static final int RESULT_CODE=113;
    public static final int REQUEST_CODE_CAMERA=114;
    SupportAnimator animator_reverse;
    String users_info;
    RevealFrameLayout revealFrameLayout;
    LinearLayout reveal_items,llAudios,llVideos,llPhotos;
    private Toolbar toolbar;
    private ImageView writeImageButton,imgSentMessages;
    private TextView channel_label;
    private ImageView backButton,pickImageFile,imgPhotos,imgVideos,imgAudios,
            imgDocuments,imgCamera,cameraImageButton,imgContact;//,imgCamera,conv_Icon;
    private RecyclerView messagesContainerRecyclerview;
    private EditText messageEditText;
    static final int PICK_CONTACT=1;
    private ArrayList<ChatMessage> chatHistory;
    private SharedPreference sharedPreference;
    private Context context=this;
    private String channel_id="",user_id,token,last_timetamp="000000000",extra_info,copied_msg=null,channel_title;
    private String first_post_id=null;
    private String file_path=null;
    private String ip;
    private HttpURLConnection conn=null;
    Uri cameraUri;
    JSONObject all_users;
    private Thread currentMessageTaskThread;
    public Boolean interrupt=false;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' at 'h:mm a");
    private JSONArray filenames=null;// A JSON variable that contains list of file names returned from the mattermost APIs
    private ProgressDialog progressDialog;
    private JSONObject extraInfoObj;
    private JSONArray members;
    private Activity activity=this;
    private ActionMode mActionMode;
    ChatConversationAdapter adapter;
    boolean hidden=true;
    int visibleItemCount,totalItemCount,pastVisiblesItems;
    int firstVisibleInListview;
    PreferenceHelper preferenceHelper;
    InputMethodManager inputManager;
    SwipeRefreshLayout swipeRefreshLayout;
    WebSocketClient mWebSocketClient=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        preferenceHelper=new PreferenceHelper(this);
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        connectWebSocket();
        channel_title = intent.getStringExtra(ChatFragment.CHANNEL_NAME);
        preferenceHelper.addString("CHANNEL_NAME",channel_title+"");
        String team_name = intent.getStringExtra(ChatFragment.TEAM_NAME);
        preferenceHelper.addString("TEAM_NAME",team_name);
        initComponent();
        sharedPreference = new SharedPreference();
        token=sharedPreference.getTokenPreference(context);
        GetChannelDetails channelDetails = new GetChannelDetails();
        Channel channel = channelDetails.getChannel(team_name,channel_title,context);
        channel_id=channel.getChannel_id();
        preferenceHelper.addString("CHANNEL_ID",channel_id);
        String user_details=sharedPreference.getPreference(context);
        try{
            JSONObject jObj = new JSONObject(user_details);
            user_id=jObj.getString("id");
            Log.v("USER_ID","USER_ID:::"+user_id);
            preferenceHelper.addString("USER_ID",user_id);
        }catch(Exception e){
        }
        ip = sharedPreference.getServerIP_Preference(context);//getting ip
        preferenceHelper.addString("APPLICATION_IP",ip);

        //connect with websocket
        //last_timetamp="1456185600000";
        Thread loadHistory = new Thread(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*Joining the Channel for the first time*/
                        ConnectAPIs joinChannel = new ConnectAPIs("http://"+ip+":8065/api/v1/channels/"+channel_id+"/join",token);
                        String joinChannelInfo = convertInputStreamToString(joinChannel.getData());
                        System.out.println("Join Channel Result: "+joinChannelInfo);
                        ConnectAPIs getUsers = new ConnectAPIs("http://"+ip+":8065//api/v1/users/profiles",token);
                        users_info = convertInputStreamToString(getUsers.getData());
                        preferenceHelper.addString("all_users",users_info);
                        System.out.println("Users: "+users_info);
                        try{
                            all_users = new JSONObject(users_info);

                        }
                        catch(Exception e){
                            System.out.println("unable to get user information");
                        }
                        String url= "http://"+ip+"/TabGenAdmin/getPost.php?channel_id="+channel_id+"&token="+token+"&user_id="+user_id;
                        /********************************************************************/
                        new GetMessageHistoryTask(true).execute(url+"");
                    }
                });
            }
        };
        //loadHistory.start();
        currentMessageTaskThread = new Thread(){
            @Override
            public void run(){
                try{
                    while(!isInterrupted() || !interrupt){
                        Thread.sleep(6000);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){

                                if(last_timetamp!=null||last_timetamp=="000000000") {
                                    System.out.println("Last timestamp: "+last_timetamp);
                                    new GetCurrentMessageTask().execute("http://"+ip+
                                            ":8065//api/v1/channels/"+channel_id+
                                            "/posts/"+last_timetamp);
                                }else
                                    System.out.println("latest timestamp is null, no chat history for this channel");
                            }
                        });
                    }
                }catch(InterruptedException e){
                    System.out.println("Interrupted Exception: "+e.toString());
                }
            }
        };
        currentMessageTaskThread.start();
        loadHistory.start();
    }
    //-----initializing the xml components
    public void initComponent(){
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        imgSentMessages=(ImageView)findViewById(R.id.imgSentMessages);
        imgSentMessages.setOnClickListener(this);
        reveal_items=(LinearLayout)findViewById(R.id.reveal_items);
        getSupportActionBar().setTitle("" + channel_title);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        progressDialog = new ProgressDialog(context);
        messagesContainerRecyclerview = (RecyclerView) findViewById(R.id.messagesContainerRecyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messagesContainerRecyclerview.setLayoutManager(layoutManager);
        writeImageButton = (ImageView) findViewById(R.id.writeImageButton);
        writeImageButton.setOnClickListener(this);
        cameraImageButton=(ImageView)findViewById(R.id.cameraImageButton);
        cameraImageButton.setOnClickListener(this);
        //setting Chat adapter
        adapter = new ChatConversationAdapter(ConversationActivity.this, chatHistory);
        messagesContainerRecyclerview.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://" + ip + "/TabGenAdmin/getPreviousPost.php?channel_id=" + channel_id +
                                "&token=" + token + "&user_id=" + user_id + "&post_id=" + first_post_id;//  preferenceHelper.getString("LAST_POST_ID");
                        Log.v("URL", "URL::::" + url);
//                                /********************************************************************/
                        new GetMessageHistoryTask(false).execute(url + "");
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
        messageEditText.setOnLongClickListener(this);
        pickImageFile = (ImageView) findViewById(R.id.pickImageFile);
        pickImageFile.setOnClickListener(this);
        imgPhotos=(ImageView)findViewById(R.id.imgPhotos);
        imgPhotos.setOnClickListener(this);
        imgContact=(ImageView)findViewById(R.id.imgContact);
        imgContact.setOnClickListener(this);
        imgDocuments=(ImageView)findViewById(R.id.imgDocuments);
        imgDocuments.setOnClickListener(this);
        imgCamera=(ImageView)findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length()>0){
                    if(isWhitespace(s.toString())){
                        writeImageButton.setVisibility(View.VISIBLE);
                        imgSentMessages.setVisibility(View.GONE);
                    }else{
                        imgSentMessages.setVisibility(View.VISIBLE);
                        writeImageButton.setVisibility(View.GONE);
                    }
                }else{
                    writeImageButton.setVisibility(View.VISIBLE);
                    imgSentMessages.setVisibility(View.GONE);
                }

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()>0){
                    if(isWhitespace(s.toString())){
                        writeImageButton.setVisibility(View.VISIBLE);
                        imgSentMessages.setVisibility(View.GONE);
                    }else{
                        imgSentMessages.setVisibility(View.VISIBLE);
                        writeImageButton.setVisibility(View.GONE);
                    }
                }else{
                    writeImageButton.setVisibility(View.VISIBLE);
                    imgSentMessages.setVisibility(View.GONE);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    if(isWhitespace(s.toString())){
                        writeImageButton.setVisibility(View.VISIBLE);
                        imgSentMessages.setVisibility(View.GONE);
                    }else{
                        imgSentMessages.setVisibility(View.VISIBLE);
                        writeImageButton.setVisibility(View.GONE);
                    }
                }else{
                    writeImageButton.setVisibility(View.VISIBLE);
                    imgSentMessages.setVisibility(View.GONE);
                }

            }
        });
    }

    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
    //----OnClick Listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                    jsonObject.put("root_id", "");
                    jsonObject.put("parent_id", "");
                    jsonObject.put("Message", messageText);
                    jsonObject.put("user_id",""+user_id);
                    sendMyMessage(jsonObject);
                    messageEditText.setText("");

                    filenames=null;
                } catch (Exception e) {
                    System.out.print("Message Sending failed: " + e.toString());
                    Snackbar.make(v, "Oops! Message Sending failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                hidekeyboard();
                break;
            case R.id.writeImageButton:

                break;
            case R.id.cameraImageButton:
                hasPermissionInManifest(this, "android.permission.CAMERA");
                Intent intent5 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file1 =FileUtils.getImageFile();
                cameraUri=Uri.fromFile(file1);
                intent5.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file1));
                startActivityForResult(intent5, REQUEST_CODE_CAMERA);
                break;
            case R.id.pickImageFile:
                if (hidden) {
                    reveal_items.setVisibility(View.VISIBLE);
                    hidden=false;
                }else{
                    reveal_items.setVisibility(View.GONE);
                    hidden=true;
                }
                break;
            case R.id.imgContact:
                hidePopupWindow();
                hasPermissionInManifest(this, "android.permission.READ_CONTACTS");
                hasPermissionInManifest(this, "android.permission.WRITE_CONTACTS");
                final Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
                Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
                startActivityForResult(intentPickContact, PICK_CONTACT);;
                break;
            case R.id.imgPhotos:
                hidePopupWindow();
                hasPermissionInManifest(this, "android.permission.READ_EXTERNAL_STORAGE");
                hasPermissionInManifest(this, "android.permission.WRITE_EXTERNAL_STORAGE");
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("*/*");
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
                hasPermissionInManifest(this, "android.permission.CAMERA");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = FileUtils.getImageFile();
                cameraUri=Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
        }
    }

    public void hidekeyboard(){

        if(inputManager.isActive()){
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
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
    //-----OnLongClick Listener for EditText
    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()){
            case R.id.messageEditText:
                if(copied_msg!=null){
                    messageEditText.setText(copied_msg);
                    Toast.makeText(context, "message pasted", Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;
        }
        return false;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.home:
                startActivity(new Intent(this,UserLandingActivity.class));
                finish();
                break;
            case R.id.aeroplane:

//                int cy = (reveal_items.getTop() + reveal_items.getBottom())/2;
                break;
            case R.id.bookmark:
                startActivity(new Intent(this,BoomkarkActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void opentheAttachmentMenu(){

        int cx = (reveal_items.getLeft() + reveal_items.getRight());
        int cy = reveal_items.getBottom();
        int radius = Math.max(reveal_items.getWidth(), reveal_items.getHeight());
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(reveal_items, cx, cy, radius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        animator_reverse = animator.reverse();
        if (hidden) {
            reveal_items.setVisibility(View.VISIBLE);
            animator.start();
            hidden = false;
        } else {
            animator_reverse.addListener(new SupportAnimator.AnimatorListener() {

                @Override
                public void onAnimationStart() {
                    Methods.toastShort("ANIMATION START", ConversationActivity.this);
                }

                @Override
                public void onAnimationEnd() {
                    Methods.toastShort("ANIMATION END", ConversationActivity.this);
                    reveal_items.setVisibility(View.INVISIBLE);
                    hidden = true;
                }

                @Override
                public void onAnimationCancel() {
                    Methods.toastShort("ANIMATION CANCEL", ConversationActivity.this);
                }

                @Override
                public void onAnimationRepeat() {
                    Methods.toastShort("ANIMATION REPEAT", ConversationActivity.this);
                }
            });
            animator_reverse.start();
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
    @Override
    public void onBackPressed(){
        progressDialog.setCancelable(true);
        try{
            if(currentMessageTaskThread!=null){
                currentMessageTaskThread.interrupt();
                interrupt=true;
            }
        }catch(Exception e){
            System.out.println("Interrupt Exception: "+e.toString());
        }
        super.onBackPressed();
        finish();
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
                case REQUEST_CODE: //file_path = readFile.getFilePath(fileUri,context);
//                    try {
                        if (data.getData() != null) {
                            fileUri = data.getData();
                            file_path = ReadFile.getPath(fileUri, context);
                            String mimetype = getMimeType(file_path);
                           if(mimetype!=null) {
                               if (file_path != null) {

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

                                   } else if (mimetype.contains("image/")) {

                                       Intent intent = new Intent(ConversationActivity.this, UploadActivity.class);
                                       Bundle bundle = new Bundle();
                                       bundle.putString("IP_VALUE", ip);
                                       bundle.putString("FILE_PATH", file_path);
                                       bundle.putString("TOKEN", token);
                                       bundle.putString("CHANNEL_ID", channel_id);
                                       bundle.putString("TYPE", "IMAGE");
                                       intent.putExtras(bundle);
                                       startActivityForResult(intent, UPLOAD_REQUEST_CODE);
                                   } else if (mimetype.contains("video/")) {
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
                                   } else {
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
                                   }
                               }
                           }else {
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
                           }
                        }else{
                            Log.v("ELSE","ELSE:::");
                        }
//                    }catch (Exception e){
//                        Log.v("Exception","Exception::::"+e.toString());
//                    }
                    break;
                case PICK_CONTACT:
                    String phone=null,name=null;
                    fileUri=data.getData();
                    Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
                    if (cursor.moveToNext()) {
                        int columnIndex_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                        String contactID = cursor.getString(columnIndex_ID);
                        int columnIndex_HASPHONENUMBER = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                        String stringHasPhoneNumber = cursor.getString(columnIndex_HASPHONENUMBER);

                        if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                            Cursor cursorNum = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID,
                                    null,
                                    null);

                            //Get the first phone number
                            if (cursorNum.moveToNext()) {
                                int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                int columnIndex_name = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                                phone = cursorNum.getString(columnIndex_number);
                                name = cursorNum.getString(columnIndex_name);

                            }
                        }
                        Methods.toastShort("CONTACT:::"+phone+" NAME::"+name,this);
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    if (cameraUri != null) {
                        Intent intent = new Intent(ConversationActivity.this, UploadActivity.class);
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
                    try {
                        String result = data.getStringExtra("RESULT_STRING");
                        String caption = data.getStringExtra("CAPTION");
                        JSONObject fileObject = new JSONObject(result);
                        filenames = fileObject.getJSONArray("filenames");
                        JSONObject jsonObject = new JSONObject();
                        if (filenames != null && filenames.length() > 0) {
                            jsonObject.put("filenames", filenames);
                        }
                        jsonObject.put("channel_id", channel_id);
                        jsonObject.put("root_id", "");
                        jsonObject.put("parent_id", "");
                        jsonObject.put("Message", caption);
                        jsonObject.put("user_id",""+user_id);
                        sendMyMessage(jsonObject);
                        filenames = null;
                    } catch (Exception e) {
                        System.out.print("Message Sending failed: " + e.toString());
                    }
                    break;
                default:
                    Toast.makeText(context, "Invalid request code. You haven't selected any file", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

            Methods.toastShort("Oops! Something went wrong.."+e.toString(),this);
        }
    }

    private String getUsernameById(String user_id){
        String username=null;
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
        return username;

    }
    private void sendMyMessage(JSONObject jsonMsg) {

        String link = "http://"+ip+":8065/api/v1/channels/"+channel_id+"/create";
        String response;
        try{
            ConnectAPIs messageAPI = new ConnectAPIs(link,token);
            response=convertInputStreamToString(messageAPI.sendData(jsonMsg));
            Log.v("USERRESPONSE","USERRESPONSE:::"+response);
            if(response!=null ){
                if(messageAPI.responseCode==200){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMe(true);
//                    chatMessage.setSenderName("Me");
                    System.out.println("Sending result: " + response);
                    Log.v("MESSAGE","RESPONSE::"+response);
                    try{
                        JSONObject json_obj= new JSONObject(response);
                        chatMessage.setId(json_obj.getString("id"));
                        chatMessage.setUserId(json_obj.getString("user_id"));
                        chatMessage.setMessage(json_obj.getString("message"));
//                        chatMessage.setNo_of_reply("0");
                        last_timetamp = json_obj.getString("create_at");
                        Long timestamp = Long.parseLong(last_timetamp);
                        Date date = new Date(timestamp);
                        chatMessage.setDate(simpleDateFormat.format(date));
                        JSONArray files = json_obj.getJSONArray("filenames");
                        if(files.length()!=0){
                            chatMessage.setFileList(files.getString(0));
                        }
                        displayMessage(chatMessage);
                    }catch(Exception e){
                        System.out.print("Chat Exception: "+e.toString());
                    }
                    file_path=null;
                }
                else{
                    try{
                        JSONObject json_obj= new JSONObject(response);
                        Toast.makeText(context,""+json_obj.get("message"),Toast.LENGTH_LONG).show();
                    }catch(Exception e){
                        System.out.print("Chat Exception: "+e.toString());
                    }
                }

            }else
                Toast.makeText(context,"Failed to send message",Toast.LENGTH_LONG).show();

        }catch(Exception e){
            System.out.println("Sending error: "+e.toString());
        }
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    public void displayMessage(List<ChatMessage> msgList){
        adapter.add(msgList);
        adapter.notifyDataSetChanged();
        scroll();
    }

    public void displayMessageHistory(List<ChatMessage> msgList){
        adapter.addAbove(msgList);
        adapter.notifyDataSetChanged();
    }

    private void scroll() {
        messagesContainerRecyclerview.scrollToPosition(adapter.getItemCount()-1);
    }

    @Override
    public void updateListview(String filename, boolean flag) {
        if(flag){
            Methods.toastShort("CALLBACK",this);
            try{
                JSONObject fileObject = new JSONObject(filename);
                filenames=fileObject.getJSONArray("filenames");
                for (int i = 0; i < filenames.length(); i++) {
                }
                sendMyMessage(fileObject);
            }catch(Exception e){
//
            }
        }
    }



    class LoadChatHistory extends AsyncTask<String,Void,InputStream>{
        URL api_url;
        String response_message;
        int response_code;
        HttpURLConnection conn;
        ProgressDialog progressDialog;
        InputStream isr=null;
        Context context;
        LoadChatHistory(String api_link,Context _context){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try{
                api_url = new URL(api_link);
                context=_context;
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading messages....");
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }catch(MalformedURLException e){
                System.out.println("Inappropriate URL: "+e.toString());
            }
        }
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }
        @Override
        protected InputStream doInBackground(String... str){
            try{
                conn = (HttpURLConnection) api_url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                response_code = conn.getResponseCode();
                response_message = conn.getResponseMessage();
                System.out.println("Response Code: " + response_code + "\nResponse message: " + response_message);
                if(response_code == 200/*HttpURLConnection.HTTP_OK*/){
                    isr = new BufferedInputStream(conn.getInputStream());
                }
                else {
                    isr = new BufferedInputStream(conn.getErrorStream());
                }
            }catch(IOException e){
                System.out.println("IOException in loading message");
                isr=null;
            }
            return isr;
        }
        @Override
        protected void onPostExecute(InputStream inputStream){
            String result;
            if(inputStream!=null){
                result = convertInputStreamToString(inputStream);
                if(response_code==200){
                    readChatHistory(result);
                }
            }
            progressDialog.dismiss();
        }

    }//end class LoadChatHistory


    private void readChatHistory(String res){
        if(res!=null) {
            String temp="";
            chatHistory = new ArrayList<ChatMessage>();
            try {
                JSONArray jsonArray = new JSONArray(res);
                JSONObject jsonObject;
                int i=jsonArray.length()-1;

                for (; i >= 0; i--) {
                    jsonObject = jsonArray.getJSONObject(i);
                    ChatMessage msg = new ChatMessage();
                    msg.setId(jsonObject.getString("postId"));

                    if(temp.equalsIgnoreCase(getUsernameById(jsonObject.getString("UserId")))){
                        msg.setSenderName("");
                    }else{
                        msg.setSenderName(getUsernameById(jsonObject.getString("UserId")));
                        temp = getUsernameById(jsonObject.getString("user_id"));
                    }
                    try {
                        if (jsonObject.getString("filenames") != null) {
                            String file=jsonObject.getString("filenames");
                            JSONArray fileArray=new JSONArray(file);
                            if(fileArray.length()>0)
                                msg.setFileList(fileArray.getString(0));
                            Log.e("ARRAY", "ARRAY:::" + fileArray.get(0));
                        }
                    }catch (Exception e){
                        Log.e("ERROR","ERROR:::"+e.getMessage());
                    }
                    msg.setParent_id(""+jsonObject.getString("parent_id"));
                    msg.setMessage(jsonObject.getString("Message"));
                    Long chatTime = Long.parseLong(jsonObject.getString("CreateAt"));
                    Date date = new Date(chatTime);
                    msg.setDate(simpleDateFormat.format(date));
                    last_timetamp = jsonObject.getString("LastPostAt");
                    chatHistory.add(msg);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }

            adapter.add(chatHistory);
            adapter.notifyDataSetChanged();
            scroll();
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
            fileLocation = sourceFileUri;
            file_upload_uri = serverUploadPath;
        }
        @Override
        protected void onPreExecute(){
            Methods.showProgressDialog(ConversationActivity.this,"Please wait..");
        }
        @Override
        protected String doInBackground(Void... v){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            progressDialog.setCancelable(true);
            File sourceFile = new File(fileLocation);
            if(!sourceFile.isFile()){
                Toast.makeText(context, "Source file does not exist", Toast.LENGTH_SHORT).show();
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
                    //create a buffer of maximum size
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
                        progressDialog.setCancelable(true);
                    }
                    else{
                        System.out.println("Oops! Your file upload is failed");
                        isr = new BufferedInputStream(httpURLConn.getErrorStream());
                        progressDialog.setCancelable(true);
                    }
                    fis.close();
                    dos.close();
                    osw.close();

                }catch(Exception e){
                    Log.v("UPLOAD","UPLOAD FILE:::"+e.toString());

                    e.printStackTrace();

                    System.out.println("File Upload Exception here: " + e.toString());
                    progressDialog.setCancelable(true);
                    return null;
                }//end try catch
            }
            return convertInputStreamToString(isr);
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
                        filenames=fileObject.getJSONArray("filenames");
                        if(file_path.contains(".mp3")){
                            try{
                                filenames=fileObject.getJSONArray("filenames");
                                JSONObject jsonObject = new JSONObject();

                                if(filenames!=null && filenames.length()>0){
                                    jsonObject.put("filenames",filenames);
                                }
                                jsonObject.put("channel_id", channel_id);
                                jsonObject.put("root_id", "");
                                jsonObject.put("parent_id","");
                                jsonObject.put("Message", " ");

                                sendMyMessage(jsonObject);
                                filenames=null;
                            } catch (Exception e) {
                                Log.v("MESSAGE","Message Sending failed");
                                System.out.print("Message Sending failed: " + e.toString());
                            }
                        }else if(file_path.contains(".mp4")){
                            try{
                                filenames=fileObject.getJSONArray("filenames");
                                JSONObject jsonObject = new JSONObject();

                                if(filenames!=null && filenames.length()>0){
                                    jsonObject.put("filenames",filenames);
                                }
                                jsonObject.put("channel_id", channel_id);
                                jsonObject.put("root_id", "");
                                jsonObject.put("parent_id","");
                                jsonObject.put("Message", " ");

                                sendMyMessage(jsonObject);
                                filenames=null;
                            } catch (Exception e) {
                                Log.v("MESSAGE","Message Sending failed");
                                System.out.print("Message Sending failed: " + e.toString());
                            }
                        }else{
                            try{
                                filenames=fileObject.getJSONArray("filenames");
                                JSONObject jsonObject = new JSONObject();

                                if(filenames!=null && filenames.length()>0){
                                    jsonObject.put("filenames",filenames);
                                }
                                jsonObject.put("channel_id", channel_id);
                                jsonObject.put("root_id", "");
                                jsonObject.put("parent_id","");
                                jsonObject.put("Message", " ");

                                sendMyMessage(jsonObject);
                                filenames=null;
                            } catch (Exception e) {
                                Log.v("MESSAGE","Message Sending failed");
                                System.out.print("Message Sending failed: " + e.toString());
                            }
                        }
                        for (int i = 0; i < filenames.length(); i++) {

                            Log.e("FILE::::","file name: " + filenames.getString(i));
                        }

                    }else {
                        Methods.toastShort("Sorry! File is too large..",context);
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

    //class for getting instant message
    class GetCurrentMessageTask extends AsyncTask<String,Void,String>{
        InputStream isr=null;
        HttpURLConnection conn;
        URL api_url;
        int responseCode=-1;
        String respMsg;
        String resp=null;
        @Override
        protected String doInBackground(String... messageUrl){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try{
                api_url = new URL(messageUrl[0]);
                conn = (HttpURLConnection) api_url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                responseCode = conn.getResponseCode();
                respMsg = conn.getResponseMessage();
                System.out.println("Response Code: " + responseCode + "\nResponse message: " + respMsg);
                if(responseCode == 200)/*HttpURLConnection.HTTP_OK*/{
                    isr = new BufferedInputStream(conn.getInputStream());
                }
                else {
                    isr = new BufferedInputStream(conn.getErrorStream());
                }
                resp = convertInputStreamToString(isr);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Exception in getMessage(): " + e.toString());
                return null;
            }
            System.out.println(resp);
            return resp;
        }
        @Override
        protected void onPostExecute(String resp){
            if(resp!=null && responseCode==200) {
                try {
                    JSONObject jObj1 = new JSONObject(resp);
                    JSONArray jsonArray = jObj1.getJSONArray("order");
                    JSONObject jObj2;
                    String temp="";
                    if (jsonArray.length() > 0) {
                        jObj2 = jObj1.getJSONObject("posts");
                        int i = 0;
                        String messageDate;
                        while (i < jsonArray.length()) {
                            JSONObject jObj3 = jObj2.getJSONObject(jsonArray.getString(i));
                            System.out.println("Id: " + jObj3.getString("id") + " Message: " + jObj3.getString("message"));
                            messageDate = "" + jObj3.getString("create_at");
                            System.out.println("Message Date: " + messageDate);
                            if(Long.parseLong(messageDate)>Long.parseLong(last_timetamp) && jObj3.getLong("delete_at")==0) {//it means if the message is new, which is indicated by the last timestamp
                                if(jObj3.getString("root_id").equals("")) {
                                    ChatMessage currentMsg = new ChatMessage();
                                    currentMsg.setId(jObj3.getString("id"));
                                    currentMsg.setMessage("" + jObj3.getString("message"));
                                    Long timeStamp = Long.parseLong(messageDate);
                                    Date date = new Date(timeStamp);
                                    currentMsg.setDate(simpleDateFormat.format(date));
                                    /*If the post contains files*/
                                    currentMsg.setUserId(jObj3.getString("user_id"));
                                    JSONArray files = jObj3.getJSONArray("filenames");
                                    if (files.length() > 0)
                                        currentMsg.setFileList(files.getString(0));
                                    if (temp.equalsIgnoreCase(getUsernameById(jObj3.getString("user_id")))) {
                                        currentMsg.setSenderName("");
                                        currentMsg.setProfile(false);
                                    } else {
                                        currentMsg.setSenderName(getUsernameById(jObj3.getString("user_id")));
                                        temp = getUsernameById(jObj3.getString("user_id"));
                                        currentMsg.setProfile(true);
                                    }
                                    displayMessage(currentMsg);
                                }else{

                                }
                            }//otherwise dont create the message
                            if(Long.parseLong(last_timetamp)< Long.parseLong(messageDate))
                                last_timetamp = messageDate;
                            i++;
                        }//end while loop
                    }
                } catch (Exception e) {
                    System.out.println("Error in parsing JSON: " + e.toString());
                }
            }//end if
        }//end on post execution
    }
    class GetMessageHistoryTask extends AsyncTask<String,Void,String>{
        InputStream isr=null;
        HttpURLConnection conn;
        URL api_url;
        int responseCode=-1;
        String respMsg;
        String resp=null;
        Boolean flag=true;//if flag is true then it is first time loading
        //else it is loading more history

        public GetMessageHistoryTask(Boolean loadType){
            flag=loadType;
        }

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... messageUrl){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try{
                api_url = new URL(messageUrl[0]);
                conn = (HttpURLConnection) api_url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                responseCode = conn.getResponseCode();
                respMsg = conn.getResponseMessage();
                System.out.println("Response Code: " + responseCode + "\nResponse message: " + respMsg);
                if(responseCode == 200)/*HttpURLConnection.HTTP_OK*/{
                    isr = new BufferedInputStream(conn.getInputStream());
                }
                else {
                    isr = new BufferedInputStream(conn.getErrorStream());
                }
                resp = convertInputStreamToString(isr);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Exception in getMessage(): " + e.toString());
                return null;
            }
            System.out.println(resp);
            return resp;
        }
        @Override
        protected void onPostExecute(String resp) {
            Log.v("HISTORY","HISTORY:::"+resp);
            List<ChatMessage> msgList = new ArrayList<ChatMessage>();
            if(resp!=null && responseCode==200) {
                try {
                    JSONObject jObj1 = new JSONObject(resp);
                    JSONArray jsonArray = jObj1.getJSONArray("order");
                    JSONObject jObj2;
                    String temp="";
                    if (jsonArray.length() > 0) {
                        jObj2 = jObj1.getJSONObject("posts");
                        int i = jsonArray.length()-1;
                        String messageDate;
                        first_post_id=jObj2.getJSONObject(jsonArray.getString(i)).getString("id");
                        System.out.print("length : " + i);
                        while (i>=0) {
                            //System.out.println(jsonArray.getString(i));
                            JSONObject jObj3 = jObj2.getJSONObject(jsonArray.getString(i));
                            System.out.println("Id: " + jObj3.getString("id") + " Message: " + jObj3.getString("message"));
                            messageDate = "" + jObj3.getString("create_at");
                            System.out.println("Message Date: " + messageDate);
                            //!messageDate.equals(last_timetamp)
                            if(jObj3.getLong("delete_at")==0) {//it means if the message is new, which is indicated by the last timestamp
                                JSONObject jsoData= jObj2.getJSONObject(jsonArray.getString(0));
                                preferenceHelper.addString("LAST_POST_ID",jsoData.getString("id"));
                                if(jObj3.getString("root_id").equals("")) {
                                    ChatMessage currentMsg = new ChatMessage();
                                    currentMsg.setId(jObj3.getString("id"));
                                    currentMsg.setMessage("" + jObj3.getString("message"));
                                    Long timeStamp = Long.parseLong(messageDate);
                                    Date date = new Date(timeStamp);
                                    currentMsg.setDate(simpleDateFormat.format(date));
                                    currentMsg.setUserId(jObj3.getString("user_id"));
                                    currentMsg.setNo_of_likes(jObj3.getString("no_of_likes"));
                                    currentMsg.setIsBookmarkedByYou(jObj3.getBoolean("isBookmarkedByYou"));
                                    currentMsg.setIsLikedByYou(jObj3.getBoolean("isLikedByYou"));
                                    currentMsg.setNo_of_reply("" + jObj3.getInt("no_of_reply"));
                                /*If the post contains files*/
                                    JSONArray files = jObj3.getJSONArray("filenames");
                                    if (files.length() > 0) {
                                        Log.v("FILE", "FILE::" + files.getString(0));
                                        currentMsg.setFileList(files.getString(0));
                                    }
                                    if (temp.equalsIgnoreCase(getUsernameById(jObj3.getString("user_id")))) {
                                        currentMsg.setSenderName("");
                                        currentMsg.setProfile(false);
                                    } else {
                                        currentMsg.setSenderName(getUsernameById(jObj3.getString("user_id")));
                                        temp = getUsernameById(jObj3.getString("user_id"));
                                        currentMsg.setProfile(true);
                                    }
                                    msgList.add(currentMsg);
                                    //displayMessage(currentMsg);
                                }else{

                                }
                                scroll();
                            }//otherwise dont create the message
                            if(Long.parseLong(last_timetamp)< Long.parseLong(messageDate))
                                last_timetamp = messageDate;
//                            }
                            i--;
                        }//end while loop
                        try {
                            if(flag==true)
                                displayMessage(msgList);
                            else
                                displayMessageHistory(msgList);
                        }
                        catch(Exception e){
                            System.out.println("Error in displaying message history: "+e.toString());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Something is wrong error in parsing JSON: " + e.toString());
                }
            }//end if
            progressDialog.dismiss();
        }//end on post execution
    }//end of GetMessageHistoryTask class

    //class for contextual action bar
    private class ActionModeCallback implements ActionMode.Callback{
        ChatMessage msg;
        int position;
        private ActionModeCallback(int msgPosition){
            msg=adapter.getItem(msgPosition);
            position = msgPosition;
        }
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.copy: copied_msg = msg.getMessage();
                    if(copied_msg!=null) Toast.makeText(getBaseContext(),"Message copied",Toast.LENGTH_SHORT).show();
                    mActionMode.finish();
                    break;
                case R.id.delete:ConnectAPIs deleteMsg =
                        new ConnectAPIs("http://"+ip+":8065/api/v1/channels/"+channel_id+"/post/"+msg.getId()+"/delete",token);
                    InputStream isr = deleteMsg.getData();
                    String result = deleteMsg.convertInputStreamToString(isr);
                    if(deleteMsg.responseCode==200){
                        Toast.makeText(getApplicationContext(),"Message deleted",Toast.LENGTH_LONG).show();
                        adapter.remove(position);
                    }
                    else{
                        try{
                            JSONObject jobj = new JSONObject(result);
                            Toast.makeText(getApplicationContext(),jobj.getString("message"),Toast.LENGTH_LONG).show();
                        }catch(Exception e){
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                    mActionMode.finish();
                    break;
                case R.id.forward:
                default:
                    CustomDialogManager customDialogManager = new CustomDialogManager(context,"Under Development",
                            "We are developing the appropriate action for this button",false);
                    customDialogManager.showCustomDialog();
                    mActionMode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.removeSelection();
            mActionMode=null;
        }
    }

    public class SampleScrollListener extends RecyclerView.OnScrollListener {
        private final Context context;

        public SampleScrollListener(Context context) {
            this.context = context;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            final Picasso picasso = Picasso.with(context);
            if(recyclerView.getScrollState()==newState){
                picasso.resumeTag(context);
            }else{
                picasso.pauseTag(context);
            }

        }

    }
    private void connectWebSocket() {
        URI uri;
        try {

            uri = new URI("ws://"+ip+":8065/api/v1/users/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.v("URISyntaxException","URISyntaxException::"+e.toString());
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.v("Websocket", "Opened::::"+serverHandshake.getHttpStatusMessage());

                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(final String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Log.v("OnMAESSAGE","OnMAESSAGE::"+s);
//                        adapter.add();
//                        TextView textView = (TextView)findViewById(R.id.messages);
//                        textView.setText(textView.getText() + "\n" + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.v("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.v("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
