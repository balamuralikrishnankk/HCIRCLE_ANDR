package Utils;

/**
 * Created by atul on 3/5/16.
 */
public interface LikeAndDislikeListener {

    public abstract void addAndRemoveBookMark(boolean flag,String post_id);
    public abstract void addAndRemoveLike(boolean flag,String post_id,String no_of_likes);
    public abstract void getResponse(String response,boolean flg);
}
