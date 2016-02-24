package com.nganthoi.salai.tabgen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import chattingEngine.ChatAdapter;
import chattingEngine.ChatMessage;
import readData.ReadFile;
import sharePreference.SharedPreference;
import connectServer.ConnectAPIs;

public class ConversationActivity extends AppCompatActivity {
    //ImageButton sendMessage;
    ImageView backButton,conv_Icon;
    ListView messagesContainer;
    EditText messageEditText;
    ImageButton sendMessage;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    SharedPreference sp;
    Context context=this;
    String channel_id="",user_id,token,last_timetamp=null,extra_info;
    String channelDetails=null,file_path=null;
    int receiver_responseCode;
    String ip,responseMessage,errorMessage;
    HttpURLConnection conn=null;
    URL api_url=null;
    Thread thread;
    public Boolean interrupt=false;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy' at 'h:mm a");
    JSONArray filenames=null;// A JSON variable that contains list of file names returned from the mattermost APIs
    ProgressDialog progressDialog;
    JSONObject extraInfoObj;
    JSONArray members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);
        /*** labels in the action abar of the activity_conversation ***/
        TextView no_of_members = (TextView) toolbar.findViewById(R.id.no_of_members);
        TextView conversationLabel = (TextView) toolbar.findViewById(R.id.conversation_Label);

        /*Setting progress dialog for uploading a file*/
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("File Upload");
        progressDialog.setMessage("Uploading your file.....");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        /*********************************************/

        messagesContainer = (ListView) findViewById(R.id.chatListView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);

        //setting Chat adapter
        adapter = new ChatAdapter(ConversationActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        /********************************************/

        backButton = (ImageView) toolbar.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//move back to the previous activity (screen)
            }
        });
        Intent intent = getIntent();
        String title = intent.getStringExtra(ChatFragment.TITLE);
        conversationLabel.setText(title);
        conv_Icon = (ImageView) toolbar.findViewById(R.id.conv_icon);
        if(title.equals("Laboratory Group")){
            conv_Icon.setImageResource(R.drawable.laboratory_group);
        }else if(title.equals("Cardiology Dept")){
            conv_Icon.setImageResource(R.drawable.cardiology_dept);
        }
        else if(title.equals("Town Square")){
            conv_Icon.setImageResource(R.drawable.laboratory_group);
        }
        else if(title.equals("Off-Topic")){
            conv_Icon.setImageResource(R.drawable.laboratory_group);
        }
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
        channel_id=OrganisationDetails.getChannelId(title,context);
        System.out.println("Title: "+title+" ---> Channel Id: "+channel_id+"\nToken Id: "+token);
        String user_details=sp.getPreference(context);
        try{
            JSONObject jObj = new JSONObject(user_details);
            user_id=jObj.getString("id");
        }catch(Exception e){
            System.out.println("Unable to read user ID: "+e.toString());
        }
        ip = sp.getServerIP_Preference(context);//getting ip

        /*** Getting extra information about the current channel ***/
        ConnectAPIs connApis = new ConnectAPIs("http://"+ip+":8065//api/v1/channels/"+channel_id+"/extra_info",token);
        extra_info = convertInputStreamToString(connApis.getData());
        System.out.println("Extra Information: "+extra_info);

        try{
            extraInfoObj = new JSONObject(extra_info);
            int n = extraInfoObj.getInt("member_count");
            no_of_members.setText((n>1?n+" Members":n+" Member"));
            members=extraInfoObj.getJSONArray("members");
        }catch(Exception e){
            System.out.println("unable to get user extra information");
        }
        /*************************************************************/
        loadHistory();//for loading entire chat history
        //last_timetamp="1456185600000";
        thread = new Thread(){
            @Override
            public void run(){
                try{
                    while(!isInterrupted() || !interrupt){
                        Thread.sleep(5000);
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
        sendMessage = (ImageButton) findViewById(R.id.chatSendButton);
        sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageText = messageEditText.getText().toString();
                    if (TextUtils.isEmpty(messageText)||messageText.equals(" ")||messageText.length()==0) {
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
                        filenames=null;
                    } catch (Exception e) {
                        System.out.print("Message Sending failed: " + e.toString());
                        Snackbar.make(v, "Oops! Message Sending failed", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        }
        //initControls();

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
        if (id == R.id.attach_file){
            Intent intent = new Intent();
            //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select a file from the gallary"),1);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
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
                    messageEditText.setText("");
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

    private void loadHistory(){
        InputStream inputStream = getData("http://"+ip+"/TabGen/getPost.php?channel_id="+channel_id);
        String res = convertInputStreamToString(inputStream);
        if(receiver_responseCode==200 && res!=null) {
            chatHistory = new ArrayList<ChatMessage>();
            //ChatMessage[] msg=new ChatMessage[100];
            try {
                JSONArray jsonArray = new JSONArray(res);
                JSONObject jsonObject;
                int i;
                for (i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    ChatMessage msg = new ChatMessage();

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

    public InputStream getData(String api_link){
        InputStream isr=null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            api_url = new URL(api_link);
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            receiver_responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            System.out.println("Response Code: " + receiver_responseCode + "\nResponse message: " + responseMessage);
            if(receiver_responseCode == 200/*HttpURLConnection.HTTP_OK*/){
                isr = new BufferedInputStream(conn.getInputStream());
            }
            else {
                isr = new BufferedInputStream(conn.getErrorStream());
            }
        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            receiver_responseCode=-1;
            System.out.println("Exception occurs here: " + e.toString());
        }
        return isr;
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


    public class UploadFile extends AsyncTask<Void,Void,String>{
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
            progressDialog.setMessage("Uploading your file: "+fileLocation);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... v){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
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
                        dos.write(buffer,0,bufferSize);
                        bytesAvailable = fis.available();
                        //publishProgress(" "+((total-bytesAvailable)/total)*100);
                        publishProgress();
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
                    System.out.println("File Upload Exception here: "+e.toString());
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
                            if(!messageDate.equals(last_timetamp)) {//it means if the message is new, which is indicated by the last timestamp
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
}

