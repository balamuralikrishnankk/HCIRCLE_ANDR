package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.R;

import java.util.ArrayList;

import activity.CmeFlipperActivity;
import activity.ViewFlipperActivity;
import models.cmetabmodel.OrgUnit;

/**
 * Created by atul on 7/7/16.
 */
public class CmeAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<OrgUnit> _listDataHeader; // header titles

    public CmeAdapter(Context context, ArrayList<OrgUnit> listDataHeader) {
        this._context = context;
        this._listDataHeader =listDataHeader;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataHeader.get(groupPosition).getTabs().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_items, null);
        }
        TextView txtChild = (TextView) convertView
                .findViewById(R.id.expandedListItem);

        txtChild.setText(""+_listDataHeader.get(groupPosition).getTabs().get(childPosition).getTabName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context, CmeFlipperActivity.class);
                intent.putExtra("TAB_ID",""+_listDataHeader.get(groupPosition).getTabs().get(childPosition).getId());
                _context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        if(_listDataHeader.get(groupPosition).getTabs()!=null)
        {
            return this._listDataHeader.get(groupPosition).getTabs().size();
        }else{
            return 0;
        }

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        TextView txtHeader = (TextView) convertView
                .findViewById(R.id.listTitle);
        txtHeader.setText(""+_listDataHeader.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}