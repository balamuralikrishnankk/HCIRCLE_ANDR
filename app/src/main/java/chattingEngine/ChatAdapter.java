package chattingEngine;

import android.app.Activity;
import android.content.Context;
/*import android.graphics.Color;
import android.support.annotation.NonNull;*/
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import com.nganthoi.salai.tabgen.R;
import com.squareup.picasso.Picasso;

/*import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;*/
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import Utils.InpuStreamConversion;
import connectServer.ConnectAPIs;
import connectServer.DownloadFiles;
import sharePreference.SharedPreference;
//import java.util.ListIterator;

/**
 * Created by SALAI on 1/26/2016.
 */
public class ChatAdapter extends BaseAdapter implements View.OnClickListener{

    private final List<ChatMessage> chatMessages;
    private Activity context;
    String ip,token,only_filename;
    private SparseBooleanArray selectedItemIds;

    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        selectedItemIds = new SparseBooleanArray();
        SharedPreference sp = new SharedPreference();
        ip = sp.getServerIP_Preference(context);
        token = sp.getTokenPreference(context);
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.chat_list_layout, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.sender_name.setText(chatMessage.getSenderName());
        holder.txtMessage.setText(chatMessage.getMessage());
        String fileInfo=null;

//        final List<String> files = chatMessage.getFileList();
        if(chatMessage.getFileList()!=null) {
            ConnectAPIs connectAPIs = new ConnectAPIs("http://" + ip + ":8065/api/v1/files/get_info/" + chatMessage.getFileList(), token);
            InputStream isr = connectAPIs.getData();
            Log.v("ISR", "ISR:::" + isr);
            try {
                holder.txtAttachmentDetails.setVisibility(View.VISIBLE);
                holder.imgDownloads.setVisibility(View.VISIBLE);
//                Log.v("VALUE"+)
                //System.out.println("File Information: "+convertInputStreamToString(isr));
                JSONObject jsonfileInfo = new JSONObject(connectAPIs.convertInputStreamToString(isr));
                Log.v("JSONFILENAME", "JSONFILENAME::::" + jsonfileInfo);
                String file_name = jsonfileInfo.getString("filename");
                int lastOccurance = file_name.lastIndexOf('/');
                only_filename = file_name.substring(lastOccurance + 1);
                fileInfo = only_filename + " \n" + InpuStreamConversion.humanReadableByteCount(Long.parseLong(jsonfileInfo.getString("size")), true);
                holder.txtAttachmentDetails.setText(fileInfo);
//                Picasso.with(context).load("http://128.199.111.18:8065/api/v1/files/get/8uq5js8z6tdjmnwk7mw55uuhko/j7r9nopox7rrtfqdbcr3w66zih/9hixmccmsjnkdeoy6f3yo7kxbo/IMG_20160402_201804.jpg?session_token_index=1").resize(200,200).into(holder.imgDetails);
            } catch (JSONException e) {
                System.out.println("JSON Exception in FileAdapter: " + e.toString());
                holder.txtAttachmentDetails.setText(null);
            }
            holder.imgDownloads.setTag(chatMessage.getFileList());
            holder.imgDownloads.setOnClickListener(this);
        }else{
            holder.txtAttachmentDetails.setVisibility(View.GONE);
            holder.imgDownloads.setVisibility(View.GONE);
        }
//            FileAdapter fileAdapter = new FileAdapter(files,context);
//            //ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,files);
//            holder.fileList.setAdapter(fileAdapter);


        //holder.txtMessage.setPadding(10, 5, 10, 5);
        //holder.txtMessage.setGravity(Gravity.CENTER_VERTICAL);
        holder.dateInfo.setText(chatMessage.getDate());
        convertView.setBackgroundColor(selectedItemIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);
        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    public void remove(int position){
        chatMessages.remove(position);
        notifyDataSetChanged();
    }
    /***********************************************************/
    public void toggleSelection(int position) {
        selectView(position, !selectedItemIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            selectedItemIds.put(position, value);
        else
            selectedItemIds.delete(position);

        notifyDataSetChanged();
    }

    public void removeSelection() {
        selectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemIds;
    }
    /****************************************************************************/

    /*
    private void setAlignment(ViewHolder holder){
        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
        layoutParams.gravity = Gravity.LEFT;
        holder.contentWithBG.setLayoutParams(layoutParams);
        holder.contentWithBG.setGravity(Gravity.CENTER_VERTICAL);
        holder.sender_name.setGravity(Gravity.LEFT);
        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        holder.content.setLayoutParams(lp);
        layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
        layoutParams.gravity = Gravity.LEFT;
        holder.txtMessage.setLayoutParams(layoutParams);
        layoutParams = (LinearLayout.LayoutParams) holder.dateInfo.getLayoutParams();
        layoutParams.gravity = Gravity.LEFT;
        holder.dateInfo.setLayoutParams(layoutParams);
    }*/

    /*private void setAlignment(ViewHolder holder, boolean isMe) {
        if (isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.msg_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);
            holder.contentWithBG.setGravity(Gravity.CENTER_VERTICAL);
            holder.contentWithBG.setPadding(10, 0, 40, 0);
            holder.txtMessage.setPadding(15,0,20,0);
            holder.sender_name.setGravity(Gravity.RIGHT);
            //holder.fileList.setBackgroundResource(R.drawable.msg_bg);
            //holder.sender_name.setTextColor(Color.GREEN);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);
            layoutParams = (LinearLayout.LayoutParams) holder.dateInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.dateInfo.setLayoutParams(layoutParams);
        }
        else {
            holder.contentWithBG.setBackgroundResource(R.drawable.msg_reply_bg);
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);
            holder.contentWithBG.setGravity(Gravity.CENTER_VERTICAL);
            holder.contentWithBG.setPadding(40, 0, 10, 0);
            holder.txtMessage.setPadding(20, 0, 15, 0);
            holder.sender_name.setGravity(Gravity.LEFT);
            //holder.sender_name.setTextColor(Color.RED);
            //holder.fileList.setBackgroundResource(R.drawable.msg_reply_bg);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);
            layoutParams = (LinearLayout.LayoutParams) holder.dateInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.dateInfo.setLayoutParams(layoutParams);
        }
    }
    */
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.sender_name = (TextView) v.findViewById(R.id.sender);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.imgDownloads=(ImageView)v.findViewById(R.id.imgDownloads);
        holder.txtAttachmentDetails=(TextView)v.findViewById(R.id.txtAttachmentDetails);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.dateInfo = (TextView) v.findViewById(R.id.dateInfo);
//        holder.fileList = (ListView) v.findViewById(R.id.fileList);
        return holder;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgDownloads:
                Log.e("VALUE","TOKEN:::"+token);
                String filePath = "http://"+ip+":8065/api/v1/files/get/"+v.getTag()+"?session_token_index=0";

                DownloadFiles downloadFiles = new DownloadFiles(filePath,context,token);
                downloadFiles.execute(only_filename);
                break;
        }

    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView sender_name;
        public TextView dateInfo,txtAttachmentDetails;
        public LinearLayout contentWithBG;
        public LinearLayout content;
        public ImageView imgDownloads;
        public ListView fileList;
    }
}
