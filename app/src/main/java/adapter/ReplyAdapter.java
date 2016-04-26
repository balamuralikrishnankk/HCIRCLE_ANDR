package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
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

import com.nganthoi.salai.tabgen.BuildConfig;
import com.nganthoi.salai.tabgen.ImageviewerActivity;
import com.nganthoi.salai.tabgen.R;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ListenerClasses.DownloadListeners;
import Utils.AlertHelper;
import Utils.InpuStreamConversion;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import connectServer.DownloadFiles;
import readData.ReadFile;
import reply_pojo.ReplyInnerObject;
import sharePreference.SharedPreference;

/**
 * Created by atul on 22/4/16.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ChatViewHolder> implements DownloadListeners {
    private List<ReplyInnerObject> replyMessages;
    PreferenceHelper preferenceHelper;
    private Activity context;
    private String fileInfo;
    String memberresponse;
    JSONArray members;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' at 'h:mm a");
    boolean flagValue=false,flagDownload=false;
    DownloadListeners downloadListeners;
    private String ip,token,only_filename;
    private SparseBooleanArray selectedItemIds;
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private static String fileExtnPtrn = "([^\\s]+(\\.(?i)(txt|doc|csv|pdf))$)";
    public ReplyAdapter(Activity context, List<ReplyInnerObject> replyMessages) {
        this.context=context;
        this.replyMessages=replyMessages;
        preferenceHelper=new PreferenceHelper(context);
        selectedItemIds = new SparseBooleanArray();
        SharedPreference sp = new SharedPreference();
        ip = sp.getServerIP_Preference(context);
        token = sp.getTokenPreference(context);
    }
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llContent,llcontentWithBackground;
        public ImageView imgProfile,imgReply,imgMessages,imgFavorite,imgBookmark,imgImages,imgDownloads,imgDocDownloads;
        public TextView txtsender,txtMessage,txtdateInfo,txtDocName;
        public RelativeLayout rlFile,loadingPanel,rlDocumentFile,llDocloadingPanel;

        public ChatViewHolder(View view) {
            super(view);
            try {
                memberresponse = preferenceHelper.getString("members");
                members = new JSONArray(memberresponse);
            }catch (Exception e){

            }
            rlFile=(RelativeLayout)view.findViewById(R.id.rlFile);
            llContent=(LinearLayout)view.findViewById(R.id.llContent);
            llcontentWithBackground=(LinearLayout)view.findViewById(R.id.llcontentWithBackground);
            imgProfile=(ImageView)view.findViewById(R.id.imgProfile);
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
        Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(txt|doc|csv|pdf))$)");
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
    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        if(replyMessages.get(position).getParentId()!=null){

        }
        Long timestamp = Long.parseLong(replyMessages.get(position).getCreateAt());
        Date date = new Date(timestamp);
//        chatMessage.setDate(simpleDateFormat.format(date));
        holder.txtsender.setText(getUsernameById(replyMessages.get(position).getUserId())+"");
        holder.txtMessage.setText(replyMessages.get(position).getMessage()+"");
        holder.txtdateInfo.setText(simpleDateFormat.format(date)+"");
        if(replyMessages.get(position).getFilenames()!=null && replyMessages.get(position).getFilenames().size()>0){
            String[] name=replyMessages.get(position).getFilenames().get(0).split("/");
            if(validateFileExtn(replyMessages.get(position).getFilenames().get(0))){
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

                Picasso.with(context).setIndicatorsEnabled(true);
                new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                        .indicatorsEnabled(BuildConfig.DEBUG)
                        .downloader(new OkHttpDownloader(picassoClient)).build()
                        .load("http://"+ip+":8065/api/v1/files/get"+replyMessages.get(position).getFilenames().get(0))
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
//        holder.imgReply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context,ReplyDialogActivity.class);
//                intent.putExtra("POST_ID",chatMessages.get(position).getId());
//                intent.putExtra("TOKEN",""+token);
//                intent.putExtra("IP",ip+"");
//                context.startActivity(intent);
//            }
//        });
        holder.imgDocDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(NetworkHelper.isOnline(context)){
                    String response=getFileInfo("http://" + ip + ":8065/api/v1/files/get_info/" + replyMessages.get(position).getFilenames().get(0), token);
                    if(response!=null){
                        try {

                            JSONObject jsonfileInfo = new JSONObject(response);
                            String file_name = jsonfileInfo.getString("filename");
                            int lastOccurance = file_name.lastIndexOf('/');
                            only_filename = file_name.substring(lastOccurance + 1);
                            String fileData[]=replyMessages.get(position).getFilenames().get(0).split("/");
                            File SDCardRoot = new File(Environment.getExternalStorageDirectory()+"/HCircle");
                            if(SDCardRoot.exists()){
                                File file = new File(SDCardRoot,fileData[4]);
                                if(file.exists()){
                                    openFolder(context,fileData[4]);
                                }else{
                                    holder.llDocloadingPanel.setVisibility(View.VISIBLE);
                                    fileInfo = only_filename + " \n" + InpuStreamConversion.humanReadableByteCount(Long.parseLong(jsonfileInfo.getString("size")), true);
                                    String filePath = "http://"+ip+":8065/api/v1/files/get/"+replyMessages.get(position).getFilenames().get(0)+"?session_token_index=0";
                                    downloadFiles(filePath,context,token);
                                }
                            }

                        }catch (Exception e){
                            AlertHelper.Alert("Something went wrong.." + e.toString(), context);
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
                    String response=getFileInfo("http://" + ip + ":8065/api/v1/files/get_info/" + replyMessages.get(position).getFilenames().get(0), token);
                    if(response!=null){
                        try {

                            JSONObject jsonfileInfo = new JSONObject(response);
                            String file_name = jsonfileInfo.getString("filename");
                            int lastOccurance = file_name.lastIndexOf('/');
                            only_filename = file_name.substring(lastOccurance + 1);
                            String fileData[]=replyMessages.get(position).getFilenames().get(0).split("/");
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
                                    String filePath = "http://"+ip+":8065/api/v1/files/get/"+replyMessages.get(position).getFilenames().get(0)+"?session_token_index=0";
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
                if(NetworkHelper.isOnline(context)){
                    String response=getFileInfo("http://" + ip + ":8065/api/v1/files/get_info/" + replyMessages.get(position).getFilenames().get(0), token);
                    if(response!=null){
                        try {

                            JSONObject jsonfileInfo = new JSONObject(response);
                            String file_name = jsonfileInfo.getString("filename");
                            int lastOccurance = file_name.lastIndexOf('/');
                            only_filename = file_name.substring(lastOccurance + 1);
                            String fileData[]=replyMessages.get(position).getFilenames().get(0).split("/");
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
                                    String filePath = "http://"+ip+":8065/api/v1/files/get/"+replyMessages.get(position).getFilenames().get(0)+"?session_token_index=0";
                                    downloadFiles(filePath,context,token);
                                }
                            }

                        }catch (Exception e){
                            AlertHelper.Alert("Something went wrong..", context);
                            holder.loadingPanel.setVisibility(View.GONE);
                        }
                    }
                }

            }
        });

    }

    public void openFolder(final Context context,String filename)
    {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Log.v("PATH", "PATH::" + Environment.getExternalStorageDirectory().getPath()
                + "/HCircle/");
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/HCircle/"+filename);
        intent.setDataAndType(uri, "*/*");
        Activity my_activity = new Activity(){
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data){
                if(data==null) return;
                Uri fileUri = data.getData();
                switch(requestCode){
                    case 1:
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
        if (replyMessages != null) {
            return replyMessages.size();
        } else {
            return 0;
        }
    }

    public ReplyInnerObject getItem(int position) {
        if (replyMessages != null) {
            return replyMessages.get(position);
        } else {
            return null;
        }
    }
    public void removeSelection() {
        selectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public void add(ReplyInnerObject message) {
        replyMessages.add(message);
    }

    public void add(List<ReplyInnerObject> messages) {
        replyMessages.addAll(messages);
    }

    public void remove(int position){
        replyMessages.remove(position);
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
}
