package template;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.nganthoi.salai.tabgen.R;
import com.nganthoi.salai.tabgen.UserActivity;

import java.util.List;

import chattingEngine.ChatMessage;

/*
 * Created by SALAI on 2/20/2016.
 */
public class TemplateAdapter extends BaseAdapter{
    private final List<String> templateList;
    private Activity activity;

    public TemplateAdapter(Activity _activity,List<String> list){
        this.templateList = list;
        this.activity = _activity;
    }

    @Override
    public int getCount(){
        if(templateList!=null){
            return templateList.size();
        }
        else return 0;
    }
    @Override
    public String getItem(int position){
        if(templateList!=null){
            return templateList.get(position);
        }
        else return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        TemplateHolder templateHolder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = inflater.inflate(R.layout.template_layout, null);
            templateHolder = new TemplateHolder(convertView);
            convertView.setTag(templateHolder);
        }
        else{
            templateHolder = (TemplateHolder)convertView.getTag();
        }
        String template_name = getItem(position);
        switch(template_name){
            case "Chat Template"://check if Chat template exist
                templateHolder.templateButton.setImageResource(R.drawable.landing_chat);
                //iv.setBackgroundDrawable(context.getResources().getDrawable(i.img));
                //templateHolder.templateButton.setBackgroundDrawable(convertView.getContext().getDrawable(R.drawable.landing_chat));

                break;
            case "Reference Template":
                templateHolder.templateButton.setImageResource(R.drawable.landing_reference);
                //templateHolder.templateButton.setBackgroundDrawable(convertView.getContext().getDrawable(R.drawable.landing_reference));
                break;
            case "CME Template":
                templateHolder.templateButton.setImageResource(R.drawable.landing_cme);
                //templateHolder.templateButton.setBackgroundDrawable(convertView.getContext().getDrawable(R.drawable.landing_cme));
                break;
            case "Latest News Template":
                templateHolder.templateButton.setImageResource(R.drawable.landing_news);
                //templateHolder.templateButton.setBackgroundDrawable(convertView.getContext().getDrawable(R.drawable.landing_news));
                break;
        }
        return convertView;
    }

    private static class TemplateHolder {
        public ImageView templateButton;
        public LinearLayout template_content;

        private TemplateHolder(View v){
            templateButton = (ImageView) v.findViewById(R.id.user_templates);
            template_content = (LinearLayout) v.findViewById(R.id.template_content);
        }
    }
}
