package com.nganthoi.salai.tabgen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Channel.Channel;
import Channel.GetChannelDetails;
import chattingEngine.ChatAdapter;
import chattingEngine.ChatMessage;
import customDialogManager.CustomDialogManager;
import readData.ReadFile;
import sharePreference.SharedPreference;
import connectServer.ConnectAPIs;

public class ConversationActivity extends AppCompatActivity {
    //ImageButton sendMessage;
    ImageView backButton;//,conv_Icon;
    ListView messagesContainer;
    EditText messageEditText;
    ImageView pickImageFile;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    SharedPreference sp;
    Context context=this;
    String channel_id="",user_id,token,last_timetamp="000000000",extra_info,copied_msg=null,channel_title;
    String file_path=null;
    String ip;
    HttpURLConnection conn=null;
    Thread thread;
    public Boolean interrupt=false;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' at 'h:mm a");
    JSONArray filenames=null;// A JSON variable that contains list of file names returned from the mattermost APIs
    ProgressDialog progressDialog;
    JSONObject extraInfoObj;
    JSONArray members;
    /****for contextual action Bar ****/
    Activity activity=this;
    private ActionMode mActionMode;
    /******************************************/

    /*****************Writing message task*******************/
    ImageView writeImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading messages....");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        /*** labels in the action abar of the activity_conversation ***/
        //TextView no_of_members = (TextView) toolbar.findViewById(R.id.no_of_members);
        TextView channel_label = (TextView) toolbar.findViewById(R.id.channel_name);
        //TextView team_label = (TextView) findViewById(R.id.teamName);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        messageEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(copied_msg!=null){
                    //CustomDialogManager cdm = new CustomDialogManager(context,null,"Paste ?",true);
                    //if(cdm.showCustomDialogWithYesNoButton()){
                    messageEditText.setText(copied_msg);
                    Toast.makeText(context, "message pasted", Toast.LENGTH_SHORT).show();
                    //}
                    return true;
                }
                return false;
            }
        });
        /*Setting progress dialog */
        progressDialog = new ProgressDialog(context);
        /*********************************************/

        messagesContainer = (ListView) findViewById(R.id.chatListView);
        //setting Chat adapter
        adapter = new ChatAdapter(ConversationActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        messagesContainer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //adapter.remove(position);
                //adapter.notifyDataSetChanged();
                //scroll();
                adapter.toggleSelection(position);
                boolean hasCheckedItems = adapter.getSelectedCount() > 0;
                if (hasCheckedItems && mActionMode == null) {
                    //if there are some selected items, then start the action mode
                    mActionMode = ConversationActivity.this.startActionMode(new ActionModeCallback(position));
                } else if (!hasCheckedItems && mActionMode != null) {
                    //if there are no selecte items then finish the action mode
                    mActionMode.finish();
                }

                if (mActionMode != null) {
                    //mActionMode.setTitle(String.valueOf(adapter.getSelectedCount())+ " selected");
                    if (adapter.getSelectedCount() > 1) {
                        mActionMode.finish();
                    }
                }
                view.setSelected(true);
                return true;
            }
        });
        messagesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    if(adapter.getSelectedCount()>1){
                        mActionMode.finish();
                    }
                }
            }
        });

        /********************************************/
        backButton = (ImageView) toolbar.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//move back to the previous activity (screen)
            }
        });
        Intent intent = getIntent();
        channel_title = intent.getStringExtra(ChatFragment.CHANNEL_NAME);
        channel_label.setText(channel_title);
        String team_name = intent.getStringExtra(ChatFragment.TEAM_NAME);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        sp = new SharedPreference();
        //channelDetails = sp.getChannelPreference(context);
        token=sp.getTokenPreference(context);
        GetChannelDetails channelDetails = new GetChannelDetails();
        Channel channel = channelDetails.getChannel(team_name,channel_title,context);
        channel_id=channel.getChannel_id();
        System.out.println("Team Name: "+team_name+" Channel Title: "+channel_title+" ---> Channel Id: "+channel_id+"\nToken Id: "+token);
        String user_details=sp.getPreference(context);
        try{
            JSONObject jObj = new JSONObject(user_details);
            user_id=jObj.getString("id");
        }catch(Exception e){
            System.out.println("Unable to read user ID: "+e.toString());
        }
        ip = sp.getServerIP_Preference(context);//getting ip

        //last_timetamp="1456185600000";
        Thread loadHistory = new Thread(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*** Getting extra information about the current channel ***/
                        ConnectAPIs connApis = new ConnectAPIs("http://"+ip+":8065//api/v1/channels/"+channel_id+"/extra_info",token);
                        extra_info = convertInputStreamToString(connApis.getData());
                        System.out.println("Extra Information: "+extra_info);

                        try{
                            extraInfoObj = new JSONObject(extra_info);
                            //int n = extraInfoObj.getInt("member_count");
                            //no_of_members.setText((n>1?n+" Members":n+" Member"));
                            members=extraInfoObj.getJSONArray("members");
                        }catch(Exception e){
                            System.out.println("unable to get user extra information");
                        }
                        /*************************************************************/
                        /*LoadChatHistory loadChatHistory = new LoadChatHistory("http://"+ip+
                                "/TabGenAdmin/getPost.php?channel_id="+channel_id,context);
                        loadChatHistory.execute("");*/
                        new GetMessageHistoryTask().execute("http://"+ip+
                                ":8065//api/v1/channels/"+channel_id+
                                "/posts/0/60");
                    }
                });
            }
        };
        //loadHistory.start();
        thread = new Thread(){
            @Override
            public void run(){
                try{
                    while(!isInterrupted() || !interrupt){
                        Thread.sleep(6000);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){

                                if(last_timetamp!=null) {
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
        thread.start();

        writeImageButton = (ImageView) findViewById(R.id.writeImageButton);
        writeImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                        jsonObject.put("parent_id","");
                        jsonObject.put("Message", messageText);
                        sendMyMessage(jsonObject);
                        messageEditText.setText(" ");
                        filenames=null;
                    } catch (Exception e) {
                        System.out.print("Message Sending failed: " + e.toString());
                        Snackbar.make(v, "Oops! Message Sending failed", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
            pickImageFile = (ImageView) findViewById(R.id.pickImageFile);
            pickImageFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select a file from the gallary"),1);
                }
            });
        loadHistory.start();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        progressDialog.setCancelable(true);
        try{
            if(thread!=null){
                thread.interrupt();
                interrupt=true;
            }
        }catch(Exception e){
            System.out.println("Interrupt Exception: "+e.toString());
        }
        super.onBackPressed();
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode!=RESULT_OK || data==null) return;
        Uri fileUri = data.getData();
        //ReadFile readFile = new ReadFile();
        switch(requestCode){
            case 1: //file_path = readFile.getFilePath(fileUri,context);
                file_path = ReadFile.getPath(fileUri,context);
                if(file_path!=null){
                    //System.out.println("File has been selected: "+file_path);
                    Toast.makeText(context, "You have selected: "+file_path, Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable(){
                            public void run(){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        UploadFile uploadFile = new UploadFile(file_path,"http://"+ip+":8065/api/v1/files/upload");
                                        uploadFile.execute();
                                    }
                                });
                            }
                        }).start();
                }
                break;
            default:
                Toast.makeText(context, "Invalid request code. You haven't selected any file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUsernameById(String user_id){
        String username=null;
        if(members!=null){
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
        }
        return username;
    }
    private void sendMyMessage(JSONObject jsonMsg) {
        String link = "http://"+ip+":8065/api/v1/channels/"+channel_id+"/create";
        String response;
        try{
            ConnectAPIs messageAPI = new ConnectAPIs(link,token);
            response=convertInputStreamToString(messageAPI.sendData(jsonMsg));
            if(response!=null ){
                if(messageAPI.responseCode==200){
                    ChatMessage chatMessage = new ChatMessage();
                    //chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(true);
                    chatMessage.setSenderName("Me");
                    //messageEditText.setText("");
                    System.out.println("Sending result: "+response);
                    try{
                        JSONObject json_obj= new JSONObject(response);
                        chatMessage.setId(json_obj.getString("id"));
                        chatMessage.setMessage(json_obj.getString("message"));
                        last_timetamp = json_obj.getString("create_at");
                        Long timestamp = Long.parseLong(last_timetamp);
                        Date date = new Date(timestamp);
                        chatMessage.setDate(simpleDateFormat.format(date));
                        JSONArray files = json_obj.getJSONArray("filenames");
                        chatMessage.setFileList(files);
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

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
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
        //InputStream inputStream = getData("http://"+ip+"/TabGenAdmin/getPost.php?channel_id="+channel_id);
        //String res = convertInputStreamToString(inputStream);
        if(/*receiver_responseCode==200 &&*/ res!=null) {
            //System.out.println(res);
            chatHistory = new ArrayList<ChatMessage>();
            try {
                JSONArray jsonArray = new JSONArray(res);
                JSONObject jsonObject;
                int i=jsonArray.length()-1;
                for (; i >= 0; i--) {
                    jsonObject = jsonArray.getJSONObject(i);
                    ChatMessage msg = new ChatMessage();
                    msg.setId(jsonObject.getString("postId"));
                    if (user_id.equals(jsonObject.getString("UserId"))) {
                        msg.setMe(true);
                        msg.setSenderName("Me");
                    } else {
                        msg.setMe(false);
                        msg.setSenderName(jsonObject.getString("messaged_by"));
                    }
                    msg.setMessage(jsonObject.getString("Message"));
                    Long chatTime = Long.parseLong(jsonObject.getString("CreateAt"));
                    Date date = new Date(chatTime);
                    msg.setDate(simpleDateFormat.format(date));
                    //msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    last_timetamp = jsonObject.getString("LastPostAt");
                    chatHistory.add(msg);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }

            adapter.add(chatHistory);
            adapter.notifyDataSetChanged();
            scroll();
            /*for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage message = chatHistory.get(i);
                adapter.add(message);
                adapter.notifyDataSetChanged();
                scroll();
            }*/
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
            //Toast.makeText(context,"Sending your file now...",Toast.LENGTH_LONG).show();
            progressDialog.setTitle("File Upload");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Uploading your file: "+fileLocation);
            progressDialog.show();
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

                    /*
                    // Send parameter #1
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"param1\"" + lineEnd + lineEnd);
                    dos.writeBytes("foo1" + lineEnd);
                    // Send parameter #2
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"param2\"" + lineEnd + lineEnd);
                    dos.writeBytes("foo2" + lineEnd);*/

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
                        //Toast.makeText(context,"Your file upload is successfully completed",Toast.LENGTH_LONG).show();
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
                    e.printStackTrace();
                    System.out.println("File Upload Exception here: " + e.toString());
                    progressDialog.setCancelable(true);
                    return null;
                }//end try catch
            }
            return convertInputStreamToString(isr);
        }
        protected void onProgressUpdate(String... progress){
            //setProgressPercent(progress[0]);
            progressDialog.setProgress(Integer.parseInt(progress[0]));
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(String result){
            if(result!=null){
                System.out.println("Result: "+result);//printing out the server results

                try{
                    JSONObject fileObject = new JSONObject(result);
                    if(serverRespCode==200) {
                        //assign the list of filenames in the global JSON array filename
                        filenames=fileObject.getJSONArray("filenames");
                        for (int i = 0; i < filenames.length(); i++) {
                            System.out.println("file name: " + filenames.getString(i));
                        }
                        progressDialog.setMessage("Upload Completed, send the file with a message");
                        progressDialog.show();
                    }//end if statement
                    else{
                        progressDialog.setCancelable(true);
                        progressDialog.setMessage("Upload failed, please try again.");
                        progressDialog.show();
                    }
                }catch(Exception e){
                    System.out.println("Unable to read file details: "+e.toString());
                    progressDialog.setMessage("Upload failed, please try again.");
                    progressDialog.show();
                }
            }
            else {
                System.out.println("Response is null");
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Upload failed, please try again.");
                progressDialog.show();
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
                    if (jsonArray.length() > 0) {
                        jObj2 = jObj1.getJSONObject("posts");
                        int i = 0;
                        String messageDate;
                        while (i < jsonArray.length()) {
                            //System.out.println(jsonArray.getString(i));
                            JSONObject jObj3 = jObj2.getJSONObject(jsonArray.getString(i));
                            System.out.println("Id: " + jObj3.getString("id") + " Message: " + jObj3.getString("message"));
                            messageDate = "" + jObj3.getString("create_at");
                            System.out.println("Message Date: " + messageDate);
                            //!messageDate.equals(last_timetamp)
                            if(Long.parseLong(messageDate)>Long.parseLong(last_timetamp) && jObj3.getLong("delete_at")==0) {//it means if the message is new, which is indicated by the last timestamp
                                ChatMessage currentMsg = new ChatMessage();
                                currentMsg.setId(jObj3.getString("id"));
                                currentMsg.setMessage("" + jObj3.getString("message"));
                                Long timeStamp = Long.parseLong(messageDate);
                                Date date = new Date(timeStamp);
                                currentMsg.setDate(simpleDateFormat.format(date));

                                /*If the post contains files*/
                                JSONArray files = jObj3.getJSONArray("filenames");
                                currentMsg.setFileList(files);

                                if (user_id.equals("" + jObj3.getString("user_id"))) {
                                    currentMsg.setMe(true);
                                    currentMsg.setSenderName("Me");
                                } else {
                                    currentMsg.setMe(false);
                                    getUsernameById(jObj3.getString("user_id"));
                                    currentMsg.setSenderName(getUsernameById(jObj3.getString("user_id")));
                                }
                                displayMessage(currentMsg);
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
    }//end of GetCurrentMessageTask class

    //class for getting instant message
    class GetMessageHistoryTask extends AsyncTask<String,Void,String>{
        InputStream isr=null;
        HttpURLConnection conn;
        URL api_url;
        int responseCode=-1;
        String respMsg;
        String resp=null;

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
        protected void onPostExecute(String resp){
            if(resp!=null && responseCode==200) {
                try {

                    JSONObject jObj1 = new JSONObject(resp);
                    JSONArray jsonArray = jObj1.getJSONArray("order");
                    JSONObject jObj2;
                    if (jsonArray.length() > 0) {
                        jObj2 = jObj1.getJSONObject("posts");
                        int i = jsonArray.length()-1;
                        String messageDate;
                        while (i >=0) {
                            //System.out.println(jsonArray.getString(i));
                            JSONObject jObj3 = jObj2.getJSONObject(jsonArray.getString(i));
                            System.out.println("Id: " + jObj3.getString("id") + " Message: " + jObj3.getString("message"));
                            messageDate = "" + jObj3.getString("create_at");
                            System.out.println("Message Date: " + messageDate);
                            //!messageDate.equals(last_timetamp)
                            if(jObj3.getLong("delete_at")==0) {//it means if the message is new, which is indicated by the last timestamp
                                ChatMessage currentMsg = new ChatMessage();
                                currentMsg.setId(jObj3.getString("id"));
                                currentMsg.setMessage("" + jObj3.getString("message"));
                                Long timeStamp = Long.parseLong(messageDate);
                                Date date = new Date(timeStamp);
                                currentMsg.setDate(simpleDateFormat.format(date));

                                /*If the post contains files*/
                                JSONArray files = jObj3.getJSONArray("filenames");
                                currentMsg.setFileList(files);

                                if (user_id.equals("" + jObj3.getString("user_id"))) {
                                    currentMsg.setMe(true);
                                    currentMsg.setSenderName("Me");
                                } else {
                                    currentMsg.setMe(false);
                                    getUsernameById(jObj3.getString("user_id"));
                                    currentMsg.setSenderName(getUsernameById(jObj3.getString("user_id")));
                                }
                                displayMessage(currentMsg);
                            }//otherwise dont create the message
                            if(Long.parseLong(last_timetamp)< Long.parseLong(messageDate))
                                last_timetamp = messageDate;
                            i--;
                        }//end while loop
                    }
                } catch (Exception e) {
                    System.out.println("Error in parsing JSON: " + e.toString());
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
}

