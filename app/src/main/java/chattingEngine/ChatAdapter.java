package chattingEngine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.R;

import java.util.List;

/**
 * Created by SALAI on 1/26/2016.
 */
public class ChatAdapter extends BaseAdapter {

    private final List<ChatMessage> chatMessages;
    private Activity context;

    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
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
        ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.chat_list_layout, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = chatMessage.getIsme() ;//to check whether it me or other sender
        setAlignment(holder, myMsg);//Setting message allignment according to the type of sender

        holder.sender_name.setText(chatMessage.getSenderName());
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.fileInfo.setText(chatMessage.getFileInfo());
        holder.txtMessage.setPadding(10, 5, 10, 5);
        holder.txtMessage.setGravity(Gravity.CENTER_VERTICAL);
        holder.dateInfo.setText(chatMessage.getDate());

        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
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

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.sender_name = (TextView) v.findViewById(R.id.sender);
        holder.fileInfo = (TextView) v.findViewById(R.id.fileInfo);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.dateInfo = (TextView) v.findViewById(R.id.dateInfo);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView sender_name;
        public TextView dateInfo;
        public TextView fileInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}
