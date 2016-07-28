package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.BoomkarkActivity;
import com.nganthoi.salai.tabgen.BuildConfig;
import com.nganthoi.salai.tabgen.R;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import AsyncClasses.WebServiceHelper;
import Utils.LikeAndDislikeListener;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import chattingEngine.ChatConversationAdapter;
import chattingEngine.ChatMessage;
import models.BookMark;
import models.BookMarkListModel;
import reply_pojo.ReplyInnerObject;

/**
 * Created by atul on 19/5/16.
 */
@SuppressWarnings("ALL")
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ChatViewHolder> implements LikeAndDislikeListener {

    public BoomkarkActivity context;
    PreferenceHelper preferenceHelper;
    public List<ReplyInnerObject> bookMarkList;
    public BookmarkAdapter(BoomkarkActivity context, List<ReplyInnerObject> bookMarkList){
        this.context=context;
        this.bookMarkList= bookMarkList;
        Log.v("LIST","LIST:::"+bookMarkList.size());
        preferenceHelper=new PreferenceHelper(context);
    }
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmark_row_item, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void addAndRemoveBookMark(boolean flag, String post_id) {
        if(flag){
            for(ReplyInnerObject chatMessage : bookMarkList){
                if(chatMessage.getId().equalsIgnoreCase(post_id)){
                    chatMessage.setBookmarkedByYou(true);
                    notifyDataSetChanged();
                    break;
                }
            }
        }else{
            for(ReplyInnerObject chatMessage : bookMarkList){
                if(chatMessage.getId().equalsIgnoreCase(post_id)){
                    chatMessage.setBookmarkedByYou(false);
                    bookMarkList.remove(chatMessage);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void addAndRemoveLike(boolean flag, String post_id, String no_of_likes) {
        if(flag){
            for(ReplyInnerObject replyInnerObject : bookMarkList){
                if(replyInnerObject.getId().equalsIgnoreCase(post_id)){
                    replyInnerObject.setIsLikedByYou(flag);
                    replyInnerObject.setNo_of_likes(Integer.parseInt(no_of_likes));
                    notifyDataSetChanged();
                    break;
                }
            }
        }else{
            for(ReplyInnerObject replyInnerObject : bookMarkList){
                if(replyInnerObject.getId().equalsIgnoreCase(post_id)){
                    replyInnerObject.setIsLikedByYou(flag);
                    replyInnerObject.setNo_of_likes(Integer.parseInt(no_of_likes));
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
        public RelativeLayout rlDocumentFile;
        public ImageView imgProfile,imgDetail,imgLike,imgBookmark,imgShare;
        public TextView txtTitle,txtDate,txtDetails,txtDocName;
        public CardView cdCardView;
        public ChatViewHolder(View view) {
            super(view);
            cdCardView=(CardView)view.findViewById(R.id.cdCardView);
            rlDocumentFile=(RelativeLayout)view.findViewById(R.id.rlDocumentFile);
            imgProfile=(ImageView)view.findViewById(R.id.imgProfile);
            imgDetail=(ImageView)view.findViewById(R.id.imgDetail);
            imgLike=(ImageView)view.findViewById(R.id.imgLike);
            imgBookmark=(ImageView)view.findViewById(R.id.imgBookmark);
            imgShare=(ImageView)view.findViewById(R.id.imgShare);
            txtTitle=(TextView) view.findViewById(R.id.txtTitle);
            txtDate=(TextView) view.findViewById(R.id.txtDate);
            txtDetails=(TextView) view.findViewById(R.id.txtDetails);
            txtDocName=(TextView) view.findViewById(R.id.txtDocName);

        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    @Override
    public void onBindViewHolder(final BookmarkAdapter.ChatViewHolder holder, final int position) {
            holder.imgProfile.setVisibility(View.VISIBLE);
            if(bookMarkList.get(position).getUserId()!=null){
                OkHttpClient picassoClient1 = new OkHttpClient();
                picassoClient1.networkInterceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", "Bearer " + preferenceHelper.getString("TOKEN_ID"))
                                .build();
                        return chain.proceed(newRequest);
                    }
                });
                new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                        .indicatorsEnabled(BuildConfig.DEBUG)
                        .downloader(new OkHttpDownloader(picassoClient1)).build()
                        .load("http://"+preferenceHelper.getString("APPLICATION_IP")+":8065/api/v1/users/"+bookMarkList.get(position).getUserId()+"/image")
                        .error(R.drawable.username)
                        .into(holder.imgProfile);
            }else {
            holder.imgProfile.setVisibility(View.GONE);
            }
                holder.txtTitle.setText(""+getUsernameById(bookMarkList.get(position).getUserId()));
                holder.txtDetails.setText(""+bookMarkList.get(position).getMessage());
                if(bookMarkList.get(position).getFilenames()!=null && bookMarkList.get(position).getFilenames().size()>0) {
                    String[] name = bookMarkList.get(position).getFilenames().get(0).split("/");
                    if (validateFileExtn(bookMarkList.get(position).getFilenames().get(0))) {
                        holder.imgDetail.setVisibility(View.GONE);
                        holder.rlDocumentFile.setVisibility(View.VISIBLE);
                        holder.txtDocName.setText("" + name[4]);
                    } else {
                        holder.rlDocumentFile.setVisibility(View.GONE);
                        holder.imgDetail.setVisibility(View.VISIBLE);
                        OkHttpClient picassoClient = new OkHttpClient();
                        picassoClient.networkInterceptors().add(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request newRequest = chain.request().newBuilder()
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", "Bearer " + preferenceHelper.getString("TOKEN_ID"))
                                        .build();
                                return chain.proceed(newRequest);
                            }
                        });
                        new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                                .downloader(new OkHttpDownloader(picassoClient)).build()
                                .load("http://" + preferenceHelper.getString("APPLICATION_IP") + ":8065/api/v1/files/get" + bookMarkList.get(position).getFilenames().get(0))
                                .error(R.drawable.place_holder)
                                .into(holder.imgDetail);
                    }

        }else{
                    holder.rlDocumentFile.setVisibility(View.GONE);
                    holder.imgDetail.setVisibility(View.GONE);
        }
            if(bookMarkList.get(position).isLikedByYou()){
                holder.imgLike.setImageResource(R.drawable.icon_love);
            }else{
                holder.imgLike.setImageResource(R.drawable.icon_love_gray);
            }
        if(bookMarkList.get(position).isBookmarkedByYou()){
            holder.imgBookmark.setImageResource(R.drawable.icon_my_bookmark);
        }else {
            holder.imgBookmark.setImageResource(R.drawable.icon_my_bookmark_gray);
        }
        holder.imgBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkHelper.isOnline(context)) {
                    String action="";
                    if(bookMarkList.get(position).isBookmarkedByYou()){
                        action="removeBookmark";
                        WebServiceHelper webServiceHelper = new WebServiceHelper();
                        webServiceHelper.updateBookMark(context, preferenceHelper.getString("APPLICATION_IP"),action, preferenceHelper.getString("USER_ID"), bookMarkList.get(position).getId(), BookmarkAdapter.this);
                    }else{
                        action="addBookmark";
                        WebServiceHelper webServiceHelper = new WebServiceHelper();
                        webServiceHelper.updateBookMark(context, preferenceHelper.getString("APPLICATION_IP"), action,preferenceHelper.getString("USER_ID"), bookMarkList.get(position).getId(), BookmarkAdapter.this);
                    }

                }
            }
        });

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkHelper.isOnline(context)) {
                    WebServiceHelper webServiceHelper = new WebServiceHelper();
                    webServiceHelper.updateLike(context, preferenceHelper.getString("APPLICATION_IP"), preferenceHelper.getString("USER_ID"), bookMarkList.get(position).getId(), BookmarkAdapter.this);
                }
            }
        });
        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot(holder);
//                loadBitmapFromView(holder.cdCardView,200,200);

            }
        });

    }
    private void takeScreenshot(ChatViewHolder holder) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = holder.cdCardView;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
    private void openScreenshot(File imageFile) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
        context.startActivity(Intent.createChooser(shareIntent, "Share image using"));
//        return shareIntent;
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        Uri uri = Uri.fromFile(imageFile);
//        intent.setDataAndType(uri, "image/*");
//        context.startActivity(intent);
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
    @Override
    public int getItemCount() {
        if (bookMarkList != null) {
            return bookMarkList.size();
        } else {
            return 0;
        }
    }

    private String getUsernameById(String user_id){
        String users= preferenceHelper.getString("all_users");
        String username=null;
        try{
            JSONObject all_users=new JSONObject(users);
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
}
