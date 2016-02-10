package com.nganthoi.salai.tabgen;

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
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import chattingEngine.ChatAdapter;
import chattingEngine.ChatMessage;
import connectServer.ConnectServer;
import readData.ReadFile;
import sharePreference.SharedPreference;

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
    String channel_id="",user_id,token,last_timetamp=null;
    String channelDetails=null,file_path=null;
    int sender_responseCode=0,receiver_responseCode;
    String ip,responseMessage,errorMessage;
    HttpURLConnection conn=null;
    URL api_url=null;
    Thread thread;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' at 'h:mm a");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConversation);
        setSupportActionBar(toolbar);

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
                try{
                    if(thread!=null)
                        thread.interrupt();
                }catch(Exception e){
                    System.out.println("Interrupt Exception: "+e.toString());
                }
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        String title = intent.getStringExtra(ChatFragment.TITLE);
        TextView conversationLabel = (TextView) toolbar.findViewById(R.id.conversation_Label);
        conversationLabel.setText(title);
        conv_Icon = (ImageView) toolbar.findViewById(R.id.conv_icon);
        if(title.equals("Laboratory Group")){
            conv_Icon.setImageResource(R.drawable.laboratory_group);
        }else if(title.equals("Cardiology Dept")){
            conv_Icon.setImageResource(R.drawable.cardiology_dept);
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
        channelDetails = sp.getChannelPreference(context);
        token=sp.getTokenPreference(context);
        if(channelDetails!=null) System.out.println("Channel is not null: "+channelDetails);
        try{
            JSONArray jsonArray = new JSONArray(channelDetails);
            JSONObject jsonObject;
            for(int i=0;i<jsonArray.length();i++){
               jsonObject = jsonArray.getJSONObject(i);
                System.out.println("Title: "+title+"------->Channel name: "+jsonObject.getString("Channel_name")+" ---->ID: "+
                        jsonObject.getString("Channel_ID"));
               if(title.equals(jsonObject.getString("Channel_name"))) {
                   channel_id = jsonObject.getString("Channel_ID");// setting channel id
                   break;
               }//channel_id = jsonObject.getString("Channel_ID");
            }
            System.out.println("Title: "+title+" Channel Id: "+channel_id+"\nToken Id: "+token);

        }catch(Exception e){
            System.out.println(e.toString());
        }
        String user_details=sp.getPreference(context);
        try{
            JSONObject jObj = new JSONObject(user_details);
            user_id=jObj.getString("id");
        }catch(Exception e){
            System.out.println("Unable to read user ID: "+e.toString());
        }
        ip = sp.getServerIP_Preference(context);//getting ip
        loadHistory();
        thread = new Thread(){
            @Override
            public void run(){
                try{
                    while(!isInterrupted()){
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                //Date dNow = new Date( );
                                /*SimpleDateFormat ft =
                                        new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");*/
                                //System.out.println("Current Date: " + simpleDateFormat.format(dNow));

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

                    try {
                        //System.out.println(ip);
                        sendMyMessage("http://"+ip+":8065/api/v1/channels/"+channel_id+"/create");
                    } catch (Exception e) {
                        System.out.print("Message Sending failed: " + e.toString());
                        Snackbar.make(v, "Message Sending failed", Snackbar.LENGTH_LONG)
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
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            //intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select a file from the gallary"),1);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode!=RESULT_OK || data==null) return;
        Uri fileUri = data.getData();
        ReadFile readFile = new ReadFile();
        switch(requestCode){
            case 1: file_path = readFile.getFilePath(fileUri,context);
                if(file_path!=null){
                    System.out.println("File has been selected: "+file_path);
                    Toast.makeText(context, "File has been selected: "+file_path, Toast.LENGTH_SHORT).show();
                        /*
                        new Thread(new Runnable(){
                            public void run(){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("Uploading your file....: "+file_path);
                                        Toast.makeText(context,"Uploading your file...",Toast.LENGTH_LONG).show();
                                        UploadFile uploadFile = new UploadFile(file_path,"http://"+ip+":8065/api/v1/files/upload");
                                        uploadFile.execute();
                                    }
                                });
                            }
                        }).start();*/
                }
                break;
            default:
                Toast.makeText(context, "Invalid request code. You haven't selected any file", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendMyMessage(String link) {
        String messageText = messageEditText.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channel_id", channel_id);
            jsonObject.put("root_id", "");
            jsonObject.put("parent_id","");
            jsonObject.put("Message", messageText);
            String response=convertInputStreamToString(sendData(jsonObject,link));
            if(response!=null){
                if(sender_responseCode==200){
                //Toast.makeTest(context,"Your message has been sent",Toast.LENGTH_SHORT).show();
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(122);//dummy
                    chatMessage.setMessage(messageText);
                    //chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(true);
                    chatMessage.setSenderName("Me");
                    messageEditText.setText("");
                    System.out.println("Sending result: "+response);

                    try{
                        JSONObject json_obj= new JSONObject(response);
                        last_timetamp = json_obj.getString("create_at");
                        Long timestamp = Long.parseLong(last_timetamp);
                        Date date = new Date(timestamp);
                        chatMessage.setDate(simpleDateFormat.format(date));
                        //+json_obj.get("id")
                        displayMessage(chatMessage);
                    }catch(Exception e){
                        System.out.print("Chat Exception: "+e.toString());
                    }
                }else{
                    try{
                        JSONObject json_obj= new JSONObject(response);
                        Toast.makeText(context,""+json_obj.get("message"),Toast.LENGTH_LONG).show();
                    }catch(Exception e){
                        System.out.print("Chat Exception: "+e.toString());
                    }
                }
            }else
                Toast.makeText(context,"You are not connected to the network",Toast.LENGTH_LONG).show();

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
                    msg.setId(i);
                    if (user_id.equals(jsonObject.getString("UserId"))) {
                        msg.setMe(true);
                        msg.setSenderName("Me");
                    } else {
                        msg.setMe(false);
                        msg.setSenderName(jsonObject.getString("messaged_by"));
                    }
                    msg.setMessage(jsonObject.getString("Message"));
                    //System.out.println("Message" + i + ": " + jsonObject.getString("Message"));
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


            for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage message = chatHistory.get(i);
                adapter.add(message);
                adapter.notifyDataSetChanged();
                scroll();
            }
        }
    }

    public InputStream sendData(JSONObject parameters,String api_link){
        OutputStream os;
        OutputStreamWriter osw;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        InputStream isr;
        try{
            api_url = new URL(api_link);
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer "+token);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            os = conn.getOutputStream();
            osw = new OutputStreamWriter(os);
            osw.write(parameters.toString());
            osw.flush();
            sender_responseCode = conn.getResponseCode(); //it only the code 200
            responseMessage = conn.getResponseMessage();// it is the json response from the mattermost api
            System.out.println("Response Code: "+sender_responseCode+"\nResponse message: "+responseMessage);
            if(sender_responseCode == 200) {
                isr = new BufferedInputStream(conn.getInputStream());
            }
            else{
                isr = new BufferedInputStream(conn.getErrorStream());
            }
            osw.close();
        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            sender_responseCode=-1;
            System.out.println("Server Not Found Exception occurs here: " + e.toString());
            isr = null;
        }
        return isr;
    }

    public InputStream getData(String api_link){
        InputStream isr=null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            api_url = new URL(api_link);
            conn = (HttpURLConnection) api_url.openConnection();
            //conn.setRequestProperty("Content-Type", "application/json");
            //conn.setRequestProperty("Authorization", "Bearer "+token);
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
                String line=null;
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


    private class UploadFile extends AsyncTask<Void,Void,String>{
        URL connectURL;
        String serverRespMsg,server_URI=null;
        HttpURLConnection httpURLConn = null;
        DataOutputStream dos = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        int serverRespCode;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String fileLocation=null;
        InputStream isr=null;
        public UploadFile(String sourceFileUri,String serverUploadPath){
            fileLocation = sourceFileUri;
            server_URI = serverUploadPath;

        }

        @Override
        protected String doInBackground(Void... v){
            File sourceFile = new File(fileLocation);
            if(!sourceFile.isFile()){
                Toast.makeText(context, "Source file does not exist", Toast.LENGTH_SHORT).show();
                return null;
            }
            else{
                try{
                    FileInputStream fis = new FileInputStream(sourceFile);
                    connectURL = new URL(server_URI);
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
                    httpURLConn.connect();
                    dos = new DataOutputStream(httpURLConn.getOutputStream());
                    dos.writeBytes(twoHyphens+boundary+lineEnd);
                    dos.writeBytes("Content-Description: form-data; name=\"files\";filename=\""+fileLocation+"\""+lineEnd);
                    dos.writeBytes(lineEnd);
                    //create a buffer of maximum size
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    buffer=new byte[bufferSize];

                    bytesRead = fis.read(buffer,0,bufferSize);
                    while(bytesRead>0){
                        dos.write(buffer,0,bufferSize);
                        bytesAvailable = fis.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fis.read(buffer,0,bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);
                    serverRespCode = httpURLConn.getResponseCode();
                    serverRespMsg = httpURLConn.getResponseMessage();
                    System.out.println("File Upload Response: " + serverRespMsg);
                    if(serverRespCode==200){
                        Toast.makeText(context,"Your file upload is successfully completed",Toast.LENGTH_LONG).show();
                        System.out.println("Your file upload is successfully completed");
                        isr = new BufferedInputStream(httpURLConn.getInputStream());
                    }
                    else{
                        Toast.makeText(context,"Oops! Your file upload is failed",Toast.LENGTH_LONG).show();
                        System.out.println("Oops! Your file upload is failed");
                        isr = new BufferedInputStream(conn.getErrorStream());
                    }
                    fis.close();
                    dos.flush();
                    dos.close();
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("Exception here: "+e.toString());
                    return null;
                }//end try catch
            }
            return convertInputStreamToString(isr);
        }

        @Override
        protected void onPostExecute(String result){
            if(result!=null){
                System.out.println(result);
            }
        }
    }//end of class UploadFile

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
                conn.setRequestProperty("Content-Type", "application/json");
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
                        String messageDate = null;
                        while (i < jsonArray.length()) {
                            //System.out.println(jsonArray.getString(i));
                            JSONObject jObj3 = jObj2.getJSONObject(jsonArray.getString(i));
                            System.out.println("Id: " + jObj3.getString("id") + " Message: " + jObj3.getString("message"));
                            messageDate = "" + jObj3.getString("create_at");
                            System.out.println("Message Date: " + messageDate);
                            ChatMessage currentMsg = new ChatMessage();
                            //currentMsg.setId(777);
                            currentMsg.setMessage("" + jObj3.getString("message"));
                            Long timeStamp = Long.parseLong(messageDate);
                            Date date = new Date(timeStamp);
                            currentMsg.setDate(simpleDateFormat.format(date));

                            if (user_id.equals("" + jObj3.getString("user_id"))) {
                                currentMsg.setMe(true);
                                currentMsg.setSenderName("Me");
                            } else {
                                currentMsg.setMe(false);
                                currentMsg.setSenderName(""+jObj3.getString("user_id"));
                            }
                            if(!messageDate.equals(last_timetamp))
                                displayMessage(currentMsg);
                            if (messageDate != null)
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

