package chattingEngine;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nganthoi.salai.tabgen.BuildConfig;
import com.nganthoi.salai.tabgen.ImageviewerActivity;
import com.nganthoi.salai.tabgen.R;
import com.nganthoi.salai.tabgen.ReplyDialogActivity;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;
import org.ocpsoft.pretty.time.PrettyTime;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import AsyncClasses.WebServiceHelper;
import ListenerClasses.DownloadListeners;
import Utils.AlertHelper;
import Utils.CustomLinearLayoutManager;
import Utils.InpuStreamConversion;
import Utils.LikeAndDislikeListener;
import Utils.Methods;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import adapter.ReplyAdapter;
import connectServer.ConnectAPIs;
import connectServer.DownloadFiles;
import de.hdodenhof.circleimageview.CircleImageView;
import readData.ReadFile;
import reply_pojo.ReplyInnerObject;
import sharePreference.SharedPreference;
/**
 * Created by atul on 13/4/16.
 */
public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ChatViewHolder> implements DownloadListeners, LikeAndDislikeListener {
    private List<ChatMessage> chatMessages;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' at 'h:mm a");
    private Activity context;
    ViewHolder viewHolder;
    private String fileInfo;
    ArrayList<ReplyInnerObject> innerObjectsList=new ArrayList<>();
    boolean flagValue=false,flagDownload=false;
    static ReplyAdapter replyAdapter;
    DownloadListeners downloadListeners;
    static String temp="";
    PreferenceHelper preferenceHelper;
    private String ip,token,only_filename;
    private SparseBooleanArray selectedItemIds;
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private static String fileExtnPtrn = "([^\\s]+(\\.(?i)(txt|doc|csv|pdf))$)";
    public ChatConversationAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context=context;
        this.chatMessages=chatMessages;
        selectedItemIds = new SparseBooleanArray();
        SharedPreference sp = new SharedPreference();
        ip = sp.getServerIP_Preference(context);
        token = sp.getTokenPreference(context);
        preferenceHelper=new PreferenceHelper(context);
        preferenceHelper.addString("TOKEN_ID",token);
    }

    @Override
    public void addAndRemoveBookMark(boolean flag, String post_id) {
        if(flag){
            for(ChatMessage chatMessage : chatMessages){
                if(chatMessage.getId().equalsIgnoreCase(post_id)){
                    chatMessage.setIsBookmarkedByYou(true);
                    notifyDataSetChanged();
                    break;
                }
            }
        }else{
            for(ChatMessage chatMessage : chatMessages){
                if(chatMessage.getId().equalsIgnoreCase(post_id)){
                    chatMessage.setIsBookmarkedByYou(false);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void addAndRemoveLike(boolean flag, String post_id,String no_of_likes) {
        if(flag){
            for(ChatMessage chatMessage : chatMessages){
                if(chatMessage.getId().equalsIgnoreCase(post_id)){
                    chatMessage.setIsLikedByYou(flag);
                    chatMessage.setNo_of_likes(no_of_likes);
                    notifyDataSetChanged();

                    break;
                }
            }
        }else{
            for(ChatMessage chatMessage : chatMessages){
                if(chatMessage.getId().equalsIgnoreCase(post_id)){
                    chatMessage.setIsLikedByYou(flag);
                    chatMessage.setNo_of_likes(no_of_likes);
                    notifyDataSetChanged();

                    break;
                }
            }
        }
    }

    @Override
    public void getResponse(String response, boolean flg) {

    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llContent,llcontentWithBackground;
        public CircleImageView imgProfile;
        public ImageView imgReply,imgMessages,imgFavorite,imgBookmark,imgImages,imgDownloads,imgDocDownloads;
        public TextView txtsender,txtMessage,txtdateInfo,txtDocName,txtReplyCount,txtLikeCount;
        public RelativeLayout rlFile,loadingPanel,rlDocumentFile,llDocloadingPanel;
        public CardView cardView;
        public RecyclerView innerRecyclerview;
        public ChatViewHolder(View view) {
            super(view);
            viewHolder=new ViewHolder();
            cardView=(CardView)view.findViewById(R.id.cardView);
            txtLikeCount=(TextView)view.findViewById(R.id.txtLikeCount);
            innerRecyclerview=(RecyclerView)view.findViewById(R.id.innerRecyclerview);
//            innerRecyclerview=(RecyclerView)view.findViewById(R.id.innerRecyclerview);
            rlFile=(RelativeLayout)view.findViewById(R.id.rlFile);
            txtReplyCount=(TextView)view.findViewById(R.id.txtReplyCount);
            llContent=(LinearLayout)view.findViewById(R.id.llContent);
            llcontentWithBackground=(LinearLayout)view.findViewById(R.id.llcontentWithBackground);
            imgProfile=(CircleImageView)view.findViewById(R.id.imgProfile);
            imgReply=(ImageView)view.findViewById(R.id.imgReply);
            imgMessages=(ImageView)view.findViewById(R.id.imgMessages);
            imgFavorite=(ImageView)view.findViewById(R.id.imgFavorite);
            imgBookmark=(ImageView)view.findViewById(R.id.imgBookmark);
            imgImages=(ImageView)view.findViewById(R.id.imgImages);
            imgDownloads=(ImageView)view.findViewById(R.id.imgDownloads);
            txtsender=(TextView)view.findViewById(R.id.txtsender);
            txtMessage=(TextView)view.findViewById(R.id.txtMessage);
            txtdateInfo=(TextView)view.findViewById(R.id.txtdateInfo);
            loadingPanel=(RelativeLayout)view.findViewById(R.id.loadingPanel);
            rlDocumentFile=(RelativeLayout)view.findViewById(R.id.rlDocumentFile);
            txtDocName=(TextView)view.findViewById(R.id.txtDocName);
            imgDocDownloads=(ImageView)view.findViewById(R.id.imgDocDownloads);
            llDocloadingPanel=(RelativeLayout)view.findViewById(R.id.llDocloadingPanel);
        }
    }
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row_item, parent, false);

        return new ChatViewHolder(itemView);
    }
    public static boolean validateFileExtn(String name){
        Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(txt|doc|csv|pdf|docx|ppt|pptx|xls|xlsx|rtf|wav|rar|zip|txt))$)");
        Matcher mtch = fileExtnPtrn.matcher(name);
        if(mtch.matches()){
            return true;
        }else if(name.contains(".mp3")){
            return true;
        }else if(name.contains(".mp4") || name.contains(".3gp"))
        {
            return true;
        }
        return false;
    }
    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        if(chatMessages.get(position).isProfile()){
            holder.imgProfile.setVisibility(View.VISIBLE);
            if(chatMessages.get(position).getUserId()!=null){
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
                new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                        .indicatorsEnabled(BuildConfig.DEBUG)
                        .downloader(new OkHttpDownloader(picassoClient1)).build()
                        .load("http://"+ip+":8065/api/v1/users/"+chatMessages.get(position).getUserId()+"/image")
                        .error(R.drawable.username)
                        .into(holder.imgProfile);
            }
        }else {
            holder.imgProfile.setVisibility(View.GONE);
        }
        if(chatMessages.get(position).isBookmarkedByYou()){
            holder.imgBookmark.setImageResource(R.drawable.icon_my_bookmark);
        }else{
            holder.imgBookmark.setImageResource(R.drawable.icon_my_bookmark_gray);
        }
        try {
            Date date = simpleDateFormat.parse(chatMessages.get(position).getDate());
            PrettyTime prettyTime = new PrettyTime();
            Log.v("DATE","DATE:::"+prettyTime.format(date));
            holder.txtdateInfo.setText(""+prettyTime.format(date));
        }catch (Exception e){

        }

        if(chatMessages.get(position).getNo_of_reply()!=null){
            if(Integer.parseInt(chatMessages.get(position).getNo_of_reply())>0){
                holder.txtReplyCount.setVisibility(View.VISIBLE);
                holder.txtReplyCount.setTextColor(Color.parseColor("#1a89c6"));
                holder.txtReplyCount.setText(chatMessages.get(position).getNo_of_reply() + "");
                holder.imgMessages.setImageResource(R.drawable.icon_reply);
            }else{
                holder.txtReplyCount.setTextColor(Color.BLACK);
                holder.imgMessages.setImageResource(R.drawable.icon_reply_gray);
                holder.txtReplyCount.setVisibility(View.GONE);
            }
        }
        if(chatMessages.get(position).getNo_of_likes()!=null){
            if(Integer.parseInt(chatMessages.get(position).getNo_of_likes())>0){
                holder.txtLikeCount.setVisibility(View.VISIBLE);
                holder.txtLikeCount.setTextColor(Color.parseColor("#1a89c6"));
                holder.txtLikeCount.setText(""+chatMessages.get(position).getNo_of_likes());
            }else{
                holder.txtLikeCount.setTextColor(Color.BLACK);
                holder.txtLikeCount.setVisibility(View.GONE);
            }
        }

        if(chatMessages.get(position).isLikedByYou()){
            holder.imgFavorite.setImageResource(R.drawable.icon_love);
        }else{
            holder.imgFavorite.setImageResource(R.drawable.icon_love_gray);
        }
        holder.imgBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkHelper.isOnline(context)) {
                    String action="";
                    if(chatMessages.get(position).isBookmarkedByYou()){
                        action="removeBookmark";
                        WebServiceHelper webServiceHelper = new WebServiceHelper();
                        webServiceHelper.updateBookMark(context, ip,action, preferenceHelper.getString("USER_ID"), chatMessages.get(position).getId(), ChatConversationAdapter.this);
                    }else{
                        action="addBookmark";
                        WebServiceHelper webServiceHelper = new WebServiceHelper();
                        webServiceHelper.updateBookMark(context, ip, action,preferenceHelper.getString("USER_ID"), chatMessages.get(position).getId(), ChatConversationAdapter.this);
                    }

                }
            }
        });
        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkHelper.isOnline(context)) {
                    WebServiceHelper webServiceHelper = new WebServiceHelper();
                    webServiceHelper.updateLike(context, ip, preferenceHelper.getString("USER_ID"), chatMessages.get(position).getId(), ChatConversationAdapter.this);
                }
            }
        });
        Log.v("EMOJI","EMOJI::"+chatMessages.get(position).getMessage());
        holder.txtsender.setText(chatMessages.get(position).getSenderName());
        holder.txtMessage.setText(" "+chatMessages.get(position).getMessage());
//        holder.txtdateInfo.setText(chatMessages.get(position).getDate());
        if(chatMessages.get(position).getFileList()!=null){
            Log.v("FILE","FILE:::"+chatMessages.get(position).getFileList());
            String[] name=chatMessages.get(position).getFileList().split("/");
            if(validateFileExtn(chatMessages.get(position).getFileList())){
                holder.rlDocumentFile.setVisibility(View.VISIBLE);
                holder.rlFile.setVisibility(View.GONE);
                holder.txtDocName.setText(""+name[4]);
            }else{
                holder.rlDocumentFile.setVisibility(View.GONE);
                holder.rlFile.setVisibility(View.VISIBLE);
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

                new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                        .downloader(new OkHttpDownloader(picassoClient)).build()
                        .load("http://"+ip+":8065/api/v1/files/get"+chatMessages.get(position).getFileList())
                        .resize(300,200)
                        .error(R.drawable.place_holder)
                        .into(holder.imgImages);
            }

        }else{
            holder.rlDocumentFile.setVisibility(View.GONE);
            holder.rlFile.setVisibility(View.GONE);
        }
        if(flagValue){
            holder.loadingPanel.setVisibility(View.GONE);
            holder.llDocloadingPanel.setVisibility(View.GONE);
        }
        holder.imgMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatMessages.get(position).getNo_of_reply()!=null) {
                    if (Integer.parseInt(chatMessages.get(position).getNo_of_reply()) > 0) {
                        Intent intent = new Intent(context, ReplyDialogActivity.class);
                        intent.putExtra("POST_ID", chatMessages.get(position).getId());
                        intent.putExtra("TOKEN", "" + token);
                        intent.putExtra("IP", ip + "");
                        context.startActivity(intent);
                    } else {
                        Methods.showSnackbar("No messages are available.If you want to reply then click on reply button", context);
                    }
                    Methods.showSnackbar("No messages are available.If you want to reply then click on reply button", context);
                }

            }
        });


        holder.imgReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(Integer.parseInt(chatMessages.get(position).getNo_of_reply())>0){
                Intent intent=new Intent(context,ReplyDialogActivity.class);
                intent.putExtra("POST_ID",chatMessages.get(position).getId());
                intent.putExtra("TOKEN", "" + token);
                intent.putExtra("IP", ip + "");
                context.startActivity(intent);
//                }
            }
        });
        holder.imgDocDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(NetworkHelper.isOnline(context)){
                    String response=getFileInfo("http://" + ip + ":8065/api/v1/files/get_info/" + chatMessages.get(position).getFileList(), token);
                    if(response!=null){
                        try {
                            JSONObject jsonfileInfo = new JSONObject(response);
                            String file_name = jsonfileInfo.getString("filename");
                            int lastOccurance = file_name.lastIndexOf('/');
                            only_filename = file_name.substring(lastOccurance + 1);
                            String fileData[]=chatMessages.get(position).getFileList().split("/");
                            File SDCardRoot = new File(Environment.getExternalStorageDirectory()+"/HCircle");
                            if(SDCardRoot.exists()){
                                File file = new File(SDCardRoot,fileData[4]);
                                if(file.exists()){
                                    openFolder(context,fileData[4]);
                                }else{
                                    holder.llDocloadingPanel.setVisibility(View.VISIBLE);
                                    fileInfo = only_filename + " \n" + InpuStreamConversion.humanReadableByteCount(Long.parseLong(jsonfileInfo.getString("size")), true);
                                    String filePath = "http://"+ip+":8065/api/v1/files/get/"+chatMessages.get(position).getFileList()+"?session_token_index=0";
                                    downloadFiles(filePath,context,token);
                                }
                            }

                        }catch (Exception e){
                            AlertHelper.Alert("Something went wrong.."+e.toString(),context);
                            holder.loadingPanel.setVisibility(View.GONE);
                        }
                    }
                }

            }
        });
        holder.imgImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkHelper.isOnline(context)){
                    String response=getFileInfo("http://" + ip + ":8065/api/v1/files/get_info/" + chatMessages.get(position).getFileList(), token);
                    if(response!=null){
                        try {

                            JSONObject jsonfileInfo = new JSONObject(response);
                            String file_name = jsonfileInfo.getString("filename");
                            int lastOccurance = file_name.lastIndexOf('/');
                            only_filename = file_name.substring(lastOccurance + 1);
                            String fileData[]=chatMessages.get(position).getFileList().split("/");
                            File SDCardRoot = new File(Environment.getExternalStorageDirectory()+"/HCircle");
                            if(SDCardRoot.exists()){
                                File file = new File(SDCardRoot,fileData[4]);
                                if(file.exists()){
                                    Intent intent=new Intent(context, ImageviewerActivity.class);
                                    intent.putExtra("FILENAME",""+Environment.getExternalStorageDirectory().getPath()
                                            + "/HCircle/"+fileData[4]);
                                    context.startActivity(intent);
//                                    openFolder(context,fileData[4]);
                                }else{
                                    holder.loadingPanel.setVisibility(View.VISIBLE);
                                    fileInfo = only_filename + " \n" + InpuStreamConversion.humanReadableByteCount(Long.parseLong(jsonfileInfo.getString("size")), true);
                                    String filePath = "http://"+ip+":8065/api/v1/files/get/"+chatMessages.get(position).getFileList()+"?session_token_index=0";
                                    downloadFiles(filePath,context,token);
                                }
                            }

                        }catch (Exception e){
                            AlertHelper.Alert("Something went wrong..",context);
                            holder.loadingPanel.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        holder.imgDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkHelper.isOnline(context)) {
                    String response = getFileInfo("http://" + ip + ":8065/api/v1/files/get_info/" + chatMessages.get(position).getFileList(), token);
                    if (response != null) {
                        try {
                            JSONObject jsonfileInfo = new JSONObject(response);
                            String file_name = jsonfileInfo.getString("filename");
                            int lastOccurance = file_name.lastIndexOf('/');
                            only_filename = file_name.substring(lastOccurance + 1);
                            String fileData[] = chatMessages.get(position).getFileList().split("/");
                            File SDCardRoot = new File(Environment.getExternalStorageDirectory() + "/HCircle");
                            if (SDCardRoot.exists()) {
                                File file = new File(SDCardRoot, fileData[4]);
                                if (file.exists()) {
                                    Intent intent = new Intent(context, ImageviewerActivity.class);
                                    intent.putExtra("FILENAME", "" + Environment.getExternalStorageDirectory().getPath()
                                            + "/HCircle/" + fileData[4]);
                                    context.startActivity(intent);
                                } else {
                                    holder.loadingPanel.setVisibility(View.VISIBLE);
                                    fileInfo = only_filename + " \n" + InpuStreamConversion.humanReadableByteCount(Long.parseLong(jsonfileInfo.getString("size")), true);
                                    String filePath = "http://" + ip + ":8065/api/v1/files/get/" + chatMessages.get(position).getFileList() + "?session_token_index=0";
                                    downloadFiles(filePath, context, token);
                                }
                            }

                        } catch (Exception e) {
                            AlertHelper.Alert("Something went wrong..", context);
                            holder.loadingPanel.setVisibility(View.GONE);
                        }
                    }
                }

            }
        });

    }
    private void showInnerView(Activity context,ChatViewHolder holder,int position) {
        if(holder.imgImages.getTag()!=null){
            if(Integer.parseInt(holder.imgImages.getTag().toString())==position){
                holder.innerRecyclerview.setVisibility(View.VISIBLE);
            }else{
                holder.innerRecyclerview.setVisibility(View.GONE);
            }
        }else{
            holder.innerRecyclerview.setVisibility(View.GONE);
        }
        String url="http://" + ip + "/TabGenAdmin/get_a_post.php?channel_id=" + preferenceHelper.getString("CHANNEL_ID") + "&token=" + token+"&user_id="+preferenceHelper.getString("USER_ID")+"&post_id="+chatMessages.get(Integer.parseInt(holder.imgImages.getTag().toString())).getId();
        ConnectAPIs connectAPIs = new ConnectAPIs(url, token);
        InputStream inputStream = connectAPIs.getReply();
        String resp = connectAPIs.convertInputStreamToString(inputStream);
        Log.v("RESPONSE", "RESPONSE:::" + resp);
        try {
            JSONObject jsonObject = new JSONObject(resp);
            JSONObject jsonObject1 = jsonObject.getJSONObject("posts");
            Iterator<String> keys = jsonObject1.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject innerJObject = jsonObject1.getJSONObject(key);
                ReplyInnerObject replyInnerObject = new Gson().fromJson(innerJObject.toString(), ReplyInnerObject.class);
                if(replyInnerObject.getParentId()!=null || !replyInnerObject.getParentId().equals("")){
                    Log.v("CALLED","CALLED"+replyInnerObject.getMessage());
                    innerObjectsList.add(replyInnerObject);
                }

            }
//            for (int i = 0; i < innerObjectsList.size(); i++) {
//                if (innerObjectsList.get(i).getId().equals(chatMessages.get(Integer.parseInt(holder.imgImages.getTag().toString())))) {
//                    Collections.swap(innerObjectsList, 0, i);
//                }
//            }
//            if(innerObjectsList.size()>0)
//            innerObjectsList.remove(0);
            RecyclerView.LayoutManager layoutManager = new CustomLinearLayoutManager(context,LinearLayoutManager.VERTICAL,true);
            layoutManager.setAutoMeasureEnabled(true);
            holder.innerRecyclerview.setLayoutManager(layoutManager);
            holder.innerRecyclerview.setNestedScrollingEnabled(false);
            holder.innerRecyclerview.setHasFixedSize(false);
            replyAdapter = new ReplyAdapter(context, innerObjectsList);
            holder.innerRecyclerview.setAdapter(replyAdapter);
        } catch (Exception e) {
            holder.innerRecyclerview.setVisibility(View.GONE);
        }
    }
    public void openFolder(final Context context,String filename)
    {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Log.v("PATH", "PATH::" + Environment.getExternalStorageDirectory().getPath()
                + "/HCircle/");
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/HCircle/" + filename);
        intent.setDataAndType(uri, "*/*");
        Activity my_activity = new Activity(){
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data){
                if(data==null) return;
                Uri fileUri = data.getData();
                //ReadFile readFile = new ReadFile();
                switch(requestCode){
                    case 1: //file_path = readFile.getFilePath(fileUri,context);
                        String file_path = ReadFile.getPath(fileUri, context);
                        if(file_path!=null){

                        }
                        break;
                    default:
                        Toast.makeText(context, "Invalid request code. You haven't selected any file", Toast.LENGTH_SHORT).show();
                }
            }
        };
        context.startActivity(Intent.createChooser(intent, "Open folder"));
    }
    public void downloadFiles(String filePath,Context context,String token){
        DownloadFiles downloadFiles = new DownloadFiles(filePath,context,token,this);
        downloadFiles.execute(only_filename);
    }
    @Override
    public void onDataUpdate(boolean flag) {
        if(flag){
            flagValue=true;
//                loadingPanel.setVisibility(View.GONE);
        }else{
            flagValue=false;
        }
        notifyDataSetChanged();
    }

    @Override
    public void donotNeedtoDownload(String path) {
        if(path!=null){
            flagDownload=true;
        }else{
            flagDownload=false;
        }
    }

    @Override
    public int getItemCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }
    public void removeSelection() {
        selectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages=messages;
    }

    public void addAbove(List<ChatMessage> msgList){
        List<ChatMessage> temp = new ArrayList<ChatMessage>();
        temp.addAll(msgList);
        temp.addAll(chatMessages);
        chatMessages=temp;
    }

    public void remove(int position){
        chatMessages.remove(position);
        notifyDataSetChanged();
    }

    public String getFileInfo(String web_api,String token){
        InputStream isr=null;
        int responseCode;
        String responseMessage, errorMessage,TokenId=null;
        URL api_url=null;
        HttpURLConnection conn=null;
        try{
            api_url =new URL(web_api);
            TokenId = token;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            conn = (HttpURLConnection) api_url.openConnection();
            //conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + TokenId);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            System.out.println("Response Code: " + responseCode + "\nResponse message: " + responseMessage);
            if(responseCode == 200/*HttpURLConnection.HTTP_OK*/){
                isr = new BufferedInputStream(conn.getInputStream());
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
        return InpuStreamConversion.convertInputStreamToString(isr);
    }

    private void parseJson(String resp,int position) {
        Log.v("RESPONSE", "RESPONSE::PARSE ::" + resp);

    }

    static class ViewHolder{
        RecyclerView innerRecyclerview;
    }
}