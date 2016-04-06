package com.nganthoi.salai.tabgen;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import expandableLists.CME_ExpandableListAdapter;
import expandableLists.ExpandableCME_ListDataPump;

public class CmeFragment extends Fragment
{
    ExpandableListView expandableListView;
    CME_ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    View cmeView;
    public CmeFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState){

        cmeView = layoutInflater.inflate(R.layout.cme_layout,container,false);
        expandableListView = (ExpandableListView) cmeView.findViewById(R.id.expandableListViewCME);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        expandableListView.setIndicatorBounds(width - 120, width);
        showCMEList();
        return cmeView;

    }

    public void showCMEList(){
        expandableListDetail= ExpandableCME_ListDataPump.getData(cmeView.getContext());
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter= new CME_ExpandableListAdapter(cmeView.getContext(),expandableListTitle,expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
    }

}
