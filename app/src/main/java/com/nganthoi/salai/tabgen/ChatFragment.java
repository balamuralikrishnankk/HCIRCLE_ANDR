package com.nganthoi.salai.tabgen;

/**
 * Created by SALAI on 1/15/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import expandableLists.ExpandableListAdapter;
import expandableLists.ExpandableListDataPump;

public class ChatFragment extends Fragment {

    /*For Chatting List View*/
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    View chatView,layoutGroupHeader;
    TextView expandableIndex;

    public final static String TITLE = "com.nganthoi.salai.tabgen.MESSAGE";
    //Context _context=this;
    public ChatFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState){
        chatView = layoutInflater.inflate(R.layout.chat_layout,container,false);
        expandableListView = (ExpandableListView) chatView.findViewById(R.id.chatExpandableListView);

        layoutGroupHeader = layoutInflater.inflate(R.layout.list_group,null);
        expandableIndex = (TextView) layoutGroupHeader.findViewById(R.id.expandableIndicator);

        showChatLists();
        return chatView;
    }

    public void showChatLists(){
        /*Setting chat list View*/
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter= new ExpandableListAdapter(chatView.getContext(),expandableListView,expandableListTitle,expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

         expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                expandableIndex.setText("-");
              /*
                Toast.makeText(chatView.getContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded "+expandableIndex.getText().toString(),
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                expandableIndex.setText("+");
                /*Toast.makeText(chatView.getContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed: "+expandableButton.getText().toString(),
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                /*Toast.makeText(
                        chatView.getContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(chatView.getContext(), ConversationActivity.class);
                intent.putExtra(TITLE, expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
                startActivity(intent);
                return false;
            }
        });
    }
}
