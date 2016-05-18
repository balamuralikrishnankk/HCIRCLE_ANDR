package AsyncClasses;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import Utils.ConstantValues;
import Utils.LikeAndDislikeListener;
import Utils.Methods;
import Utils.PreferenceHelper;
import models.BookMarkModels;
import models.LikeModels;
import network.NetworkJob;
import network.NetworkRequest;
import network.NetworkResponse;
import threading.BackgroundJobClient;

/**
 * Created by atul on 3/5/16.
 */
public class WebServiceHelper implements BackgroundJobClient {

    Context context;
    String selectedPid;
    String action;
    boolean flag=false;
    PreferenceHelper preferenceHelper;
    LikeAndDislikeListener likeAndDislikeListener;
    public void getReplyDetails(Context context,String url,LikeAndDislikeListener likeAndDislikeListener){

        this.context=context;
        this.likeAndDislikeListener=likeAndDislikeListener;
        preferenceHelper=new PreferenceHelper(context);
        Methods.showProgressDialog(context, "Please wait..");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.POST,url, ConstantValues.GET_REPLY_RESULT);
        NetworkRequest networkRequest=builder.build();
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }

    public void updateLike(Context context,String ip,String user_id,String post_id,LikeAndDislikeListener likeAndDislikeListener)
    {
        this.context=context;
        this.likeAndDislikeListener=likeAndDislikeListener;
        preferenceHelper=new PreferenceHelper(context);
        Methods.showProgressDialog(context,"Please wait..");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.POST,"http://"+ip+"/TabGenAdmin/like_a_post.php", ConstantValues.LIKE_RESPONSE);
        builder.addParameter("user_id",user_id);
        builder.addParameter("post_id", post_id);
        NetworkRequest networkRequest=builder.build();
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }
    public void updateBookMark(Context context,String ip,String action,String user_id,String post_id,LikeAndDislikeListener likeAndDislikeListener)
    {
        this.context=context;
        action=action;
        this.likeAndDislikeListener=likeAndDislikeListener;
        preferenceHelper=new PreferenceHelper(context);
        Methods.showProgressDialog(context, "Please wait..");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.POST,"http://"+ip+"/TabGenAdmin/bookmark.php", ConstantValues.BOOKMARK_RESPONSE);
        builder.addParameter("action",action);
        builder.addParameter("user_id", user_id);
        builder.addParameter("post_id", post_id);
        NetworkRequest networkRequest=builder.build();
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }

    @Override
    public void onBackgroundJobComplete(int requestCode, Object result) {
        Log.v("RESPONSE", "LIKE RESULT:::" + ((NetworkResponse) result).getResponseString());
        Methods.closeProgressDialog();
        if(requestCode==ConstantValues.LIKE_RESPONSE)
        {
            try {
                LikeModels likeModels = new Gson().fromJson(((NetworkResponse) result).getResponseString(), LikeModels.class);
                if (likeModels.getLikedStatus().equals("liked")) {
                    selectedPid = likeModels.getPost_id();
                    likeAndDislikeListener.addAndRemoveLike(true, selectedPid,likeModels.getNo_of_likes());
                    Methods.toastLong(likeModels.getMessage(), context);
//
                }else{
                    selectedPid = likeModels.getPost_id();
                    likeAndDislikeListener.addAndRemoveLike(false, selectedPid,likeModels.getNo_of_likes());
                    Methods.toastLong(likeModels.getMessage(), context);
                }
            }catch (Exception e)
            {

            }
        }else if(requestCode==ConstantValues.GET_REPLY_RESULT){

                try{
                    likeAndDislikeListener.getResponse(((NetworkResponse) result).getResponseString(),true);
                }catch (Exception e){
                    likeAndDislikeListener.getResponse("",false);
                }
        }else if(requestCode==ConstantValues.BOOKMARK_RESPONSE) {
            Log.v("RESPONSE", "POSTO:::" + ((NetworkResponse) result).getResponseString());
                try {
                    BookMarkModels bookMarkModels = new Gson().fromJson(((NetworkResponse) result).getResponseString(), BookMarkModels.class);
                    if (bookMarkModels.getStatus()) {
                        if(bookMarkModels.getBookmark_type().equalsIgnoreCase("remove")){
                            likeAndDislikeListener.addAndRemoveBookMark(false, bookMarkModels.getPostId());
                            Methods.toastLong(bookMarkModels.getMessage(), context);
                        }else if(bookMarkModels.getBookmark_type().equalsIgnoreCase("add")){
                            likeAndDislikeListener.addAndRemoveBookMark(true, bookMarkModels.getPostId());
                            Methods.toastLong(bookMarkModels.getMessage(), context);
                        }else{
                            Methods.toastLong(bookMarkModels.getMessage(), context);
                        }

                    }else {
                        Methods.toastLong(bookMarkModels.getMessage(), context);
                    }
                } catch (Exception e) {
                    likeAndDislikeListener.getResponse("", false);
                }

        }
    }

    @Override
    public void onBackgroundJobAbort(int requestCode, Object reason) {
        Log.v("POSTO","RESPONSE:::"+((NetworkResponse) reason).getResponseString());
        Methods.closeProgressDialog();
    }

    @Override
    public void onBackgroundJobError(int requestCode, Object error) {
        Log.v("POSTO","RESPONSE:::"+((NetworkResponse) error).getResponseString());
        Methods.closeProgressDialog();
    }

    @Override
    public boolean needAsyncResponse() {
        return true;
    }

    @Override
    public boolean needResponse() {
        return true;
    }
}
