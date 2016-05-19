package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.BoomkarkActivity;
import com.nganthoi.salai.tabgen.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Utils.PreferenceHelper;
import models.BookMark;
import models.BookMarkListModel;

/**
 * Created by atul on 19/5/16.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ChatViewHolder> {

    public BoomkarkActivity context;
    PreferenceHelper preferenceHelper;
    public List<BookMarkListModel> bookMarkList;
    public BookmarkAdapter(BoomkarkActivity context, List<BookMarkListModel> bookMarkList){
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

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlDocumentFile;
        public ImageView imgProfile,imgDetail;
        public TextView txtTitle,txtDate,txtDetails,txtDocName;
        public ChatViewHolder(View view) {
            super(view);
            rlDocumentFile=(RelativeLayout)view.findViewById(R.id.rlDocumentFile);
            imgProfile=(ImageView)view.findViewById(R.id.imgProfile);
            imgDetail=(ImageView)view.findViewById(R.id.imgDetail);
            txtTitle=(TextView) view.findViewById(R.id.txtTitle);
            txtDate=(TextView) view.findViewById(R.id.txtDate);
            txtDetails=(TextView) view.findViewById(R.id.txtDetails);
            txtDocName=(TextView) view.findViewById(R.id.txtDocName);

        }
    }
    @Override
    public void onBindViewHolder(BookmarkAdapter.ChatViewHolder holder, int position) {
        Log.v("TITLE","TITLE:::"+bookMarkList.get(position).getUserId());
                holder.txtTitle.setText(""+getUsernameById(bookMarkList.get(position).getUserId()));
                holder.txtDetails.setText(""+bookMarkList.get(position).getMessage());

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
