package com.nganthoi.salai.tabgen;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LatestNewsFragment extends Fragment {
    public LatestNewsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState){

        return layoutInflater.inflate(R.layout.latest_news_layout,container,false);

    }
}