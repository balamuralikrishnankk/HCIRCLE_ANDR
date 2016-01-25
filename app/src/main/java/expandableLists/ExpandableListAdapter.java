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

import com.nganthoi.salai.tabgen.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ExpandableListView expandableListView;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private int[] group_status;

    public ExpandableListAdapter(Context context, ExpandableListView expListView,List<String> expandableListTitle,
                                 HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListView = expListView;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        group_status = new int[expandableListDetail.size()];
        setListEvent();
    }

    public void setListEvent(){

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                group_status[groupPosition]=0;
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                group_status[groupPosition]=1;
            }
        });

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
        ImageView imageList = (ImageView) convertView.findViewById(R.id.imageView2);
        expandedListTextView.setText(expandedListText);
        if(expandedListText=="Laboratory Group"){
            imageList.setImageResource(R.drawable.laboratory_group);
        }
        else if(expandedListText=="Cardiology Dept"){
            imageList.setImageResource(R.drawable.cardiology_dept);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
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
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        ImageView imageTitleView = (ImageView) convertView.findViewById(R.id.imageView1);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        if(listTitle=="Hiranandani Hospital"){
            imageTitleView.setImageResource(R.drawable.hiranandani_hospital);
        }
        else if(listTitle=="Lilavati Hospital"){
            imageTitleView.setImageResource(R.drawable.lilavati_hospital);
        }

        TextView expandableIndicator = (TextView) convertView.findViewById(R.id.expandableIndicator);

        if(group_status[listPosition]==0)
            expandableIndicator.setText("+");
        else
            expandableIndicator.setText("-");
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
