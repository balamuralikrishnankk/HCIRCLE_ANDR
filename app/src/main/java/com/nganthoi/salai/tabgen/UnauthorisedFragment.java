package com.nganthoi.salai.tabgen;

/**
 * Created by SALAI on 1/15/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import expandableLists.ExpandableListAdapter;
import expandableLists.ExpandableListDataPump;

public class UnauthorisedFragment extends Fragment {

    public UnauthorisedFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState){
       return layoutInflater.inflate(R.layout.unauthorize_layout,container,false);
    }

}
