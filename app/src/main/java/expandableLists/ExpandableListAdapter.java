package expandableLists;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.OrganisationDetails;
import com.nganthoi.salai.tabgen.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import connectServer.ConnectAPIs;
import sharePreference.SharedPreference;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    //private ExpandableListView expandableListView;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;


    public ExpandableListAdapter(Context context,List<String> expandableListTitle,
                                 HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        //this.expandableListView = expListView;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_items, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        //ImageView imageList = (ImageView) convertView.findViewById(R.id.imageView2);
        //TextView count = (TextView) convertView.findViewById(R.id.count);//count the number of members for each channel in organisation unit
        expandedListTextView.setText(expandedListText);
        //count.setText("0");
        /*
        SharedPreference sp = new SharedPreference();
        String token = sp.getTokenPreference(context);
        String channel_id = OrganisationDetails.getChannelId(expandedListText, context);
        String ip = sp.getServerIP_Preference(context);
        System.out.println("Title: "+expandedListText+" ---> Channel Id: "+channel_id+"\nToken Id: "+token);
        //*** Getting extra information about the current channel ***
        ConnectAPIs connApis = new ConnectAPIs("http://"+ip+":8065//api/v1/channels/"+channel_id+"/extra_info",token);
        String extra_info = connApis.convertInputStreamToString(connApis.getData());
        System.out.println("Extra Information: "+extra_info);

        try{
            JSONObject extraInfoObj = new JSONObject(extra_info);
            int n = extraInfoObj.getInt("member_count");
            count.setText(""+n);
        }catch(Exception e){
            System.out.println("unable to get user extra information");
        }
        /*************************************************************/
        /*if(expandedListText.equals("Town Square")){
            imageList.setImageResource(R.drawable.laboratory_group);
        }
        else if(expandedListText.equals("Off-Topic")){
            imageList.setImageResource(R.drawable.cardiology_dept);
        }*/
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);//channel Title
        //ImageView imageTitleView = (ImageView) convertView.findViewById(R.id.teamLogo);//channel Icon
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);//setting channel title
        //imageTitleView.setImageResource(R.drawable.hiranandani_hospital);
        /*if(listTitle.equals("Hiranandani Hospital")){


        }
        else if(listTitle.equals("Lilavati Hospital")){
            imageTitleView.setImageResource(R.drawable.lilavati_hospital);
        }
        else if(listTitle.equals("neworgunit")){
            imageTitleView.setImageResource(R.drawable.lilavati_hospital);
        }*/
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


}
