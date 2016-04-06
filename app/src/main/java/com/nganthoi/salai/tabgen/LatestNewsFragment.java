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

import expandableLists.ExpandableNews_ListDataPump;
import expandableLists.News_ExpandableListAdapter;

public class LatestNewsFragment extends Fragment {
    ExpandableListView expandableListView;
    News_ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    View newsView;
    public LatestNewsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState){

        newsView = layoutInflater.inflate(R.layout.latest_news_layout,container,false);
        expandableListView = (ExpandableListView) newsView.findViewById(R.id.News_expandableListView);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        expandableListView.setIndicatorBounds(width - 120, width);
        showNewsList();
        return newsView;

    }
    public void showNewsList(){
        expandableListDetail= ExpandableNews_ListDataPump.getData(newsView.getContext());
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter= new News_ExpandableListAdapter(newsView.getContext(),expandableListTitle,expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
    }
}