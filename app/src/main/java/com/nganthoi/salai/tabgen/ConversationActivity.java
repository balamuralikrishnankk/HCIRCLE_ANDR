package com.nganthoi.salai.tabgen;

import android.content.Context;
import android.content.Intent;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import chattingEngine.ChatAdapter;
import chattingEngine.ChatMessage;
import connectServer.ConnectServer;
import sharePreference.SharedPreference;

public class ConversationActivity extends AppCompatActivity {
    //ImageButton sendMessage;
    ImageView backButton,conv_Icon;
    ListView messagesContainer;
    EditText messageEditText;
    ImageButton sendBtn,sendMessage;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    SharedPreference sp;
    Context context=this;
    String channel_id="",user_id,token,last_timetamp=null;
    String channelDetails=null;
    ConnectServer connMessage;
    int sender_responseCode=0,receiver_responseCode;
    String ip,responseMessage,errorMessage;
    HttpURLConnection conn=null;
    URL api_url=null;
    Thread thread;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss a");
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
                        Thread.sleep(7000);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                //loadHistory();
                                if(last_timetamp!=null) {
                                    getMessage();
                                }else System.out.println("latest timestamp is null, no chat history for this channel");
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

        return super.onOptionsItemSelected(item);
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
                        Toast.makeText(context,"Message sent...",Toast.LENGTH_LONG).show();
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
    public void getMessage(){
        //loadHistory();
        //HttpURLConnection conn;
        InputStream isr=null;
        int responseCode=-1;
        String respMsg;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            //String tempSite = "http://128.199.111.18:8065/api/v1/channels/tws3kgoqcfdtfjpgq5ash3zdqo/posts/1454579256871";
            api_url = new URL("http://"+ip+":8065/api/v1/channels/"+channel_id+"/posts/"+last_timetamp);
            conn = (HttpURLConnection) api_url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            responseCode = conn.getResponseCode();
            respMsg = conn.getResponseMessage();
            System.out.println("Response Code: " + responseCode + "\nResponse message: " + respMsg);
            if(responseCode == 200)/*HttpURLConnection.HTTP_OK*/{
                isr = new BufferedInputStream(conn.getInputStream());
                try{
                    String resp = convertInputStreamToString(isr);
                    System.out.println(resp);
                    JSONObject jObj1 = new JSONObject(resp);
                    JSONArray jsonArray = jObj1.getJSONArray("order");
                    JSONObject jObj2 = new JSONObject();
                    if(jsonArray.length()>0) {
                        jObj2 = jObj1.getJSONObject("posts");
                        int i=0;
                        String messageDate=null;
                        while(i<jsonArray.length()){
                            //System.out.println(jsonArray.getString(i));
                            JSONObject jObj3 = jObj2.getJSONObject(jsonArray.getString(i));
                            System.out.println("Id: " + jObj3.getString("id") + " Message: " + jObj3.getString("message"));
                            messageDate = ""+jObj3.getString("create_at");
                            System.out.println("Message Date: "+messageDate);
                            ChatMessage currentMsg = new ChatMessage();
                            //currentMsg.setId(777);
                            currentMsg.setMessage(""+jObj3.getString("message"));
                            Long timeStamp = Long.parseLong(messageDate);
                            Date date = new Date(timeStamp);
                            currentMsg.setDate(simpleDateFormat.format(date));

                            if(user_id.equals(""+jObj3.getString("user_id"))){
                                currentMsg.setMe(true);
                            }
                            else{
                                currentMsg.setMe(false);
                            }
                            currentMsg.setSenderName(""+jObj3.getString("user_id"));
                            displayMessage(currentMsg);
                            i++;
                        }
                        if(messageDate!=null)
                            last_timetamp = messageDate;
                    }
                    //System.out.println("Size of order i: "+i);
                }catch(Exception e){
                    System.out.println("Error in parsing JSON: " + e.toString());
                }
            }
            else {
                isr = new BufferedInputStream(conn.getErrorStream());
                String resp = convertInputStreamToString(isr);
                System.out.println(resp);
            }

        }catch(Exception e){
            e.printStackTrace();
            errorMessage = e.toString();
            System.out.println("Exception in getMessage(): " + e.toString());
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
                //System.out.println("JSON String: "+result);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("We have found an exception: \n"+e.toString());
            }
        }
        return result;
    }
}

