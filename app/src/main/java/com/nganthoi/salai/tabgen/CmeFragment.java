package com.nganthoi.salai.tabgen;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.ConstantValues;
import Utils.Methods;
import Utils.NetworkHelper;
import Utils.PreferenceHelper;
import adapter.CmeAdapter;
import expandableLists.CME_ExpandableListAdapter;
import expandableLists.ExpandableCME_ListDataPump;
import models.cmetabmodel.CmeTabModel;
import models.cmetabmodel.OrgUnit;
import network.NetworkJob;
import network.NetworkRequest;
import network.NetworkResponse;
import reply_pojo.ReplyInnerObject;
import threading.BackgroundJobClient;

public class CmeFragment extends Fragment implements BackgroundJobClient{
    ExpandableListView expandableListView;
    ArrayList<OrgUnit> orgUnitList=new ArrayList<>();
    CmeAdapter cmeAdapter;
    View cmeView;
    PreferenceHelper preferenceHelper;
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
        if(NetworkHelper.isOnline(getActivity())){
            getCmeList();
        }else{
            Methods.toastShort("Please check your internet connection..",getActivity());
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        expandableListView.setIndicatorBounds(width - 120, width);

        return cmeView;

    }

    private void getCmeList() {
        preferenceHelper=new PreferenceHelper(getActivity());
        Methods.showProgressDialog(getActivity(), "Please wait..");
        NetworkRequest.Builder builder=new NetworkRequest.Builder(NetworkRequest.MethodType.GET,ConstantValues.CME_TAB_URL+""+preferenceHelper.getString("LOGIN_USER_ID"), ConstantValues.CME_TAB_RESPONSE);
        NetworkRequest networkRequest=builder.build();
        builder.setContentType(NetworkRequest.ContentType.FORM_ENCODED);
        NetworkJob networkJob=new NetworkJob(this,networkRequest);
        networkJob.execute();
    }
    @Override
    public void onBackgroundJobComplete(int requestCode, Object result) {
        Methods.closeProgressDialog();
        if(result!=null){
            if(ConstantValues.CME_TAB_RESPONSE==requestCode) {
                CmeTabModel cmeTabModel = new Gson().fromJson(((NetworkResponse) result).getResponseString(), CmeTabModel.class);
                if (cmeTabModel != null) {
                    orgUnitList = (ArrayList<OrgUnit>) cmeTabModel.getResponse().getOrgUnits();
                    if(orgUnitList.size()>0) {
                        cmeAdapter = new CmeAdapter(getActivity(), orgUnitList);
                        expandableListView.setAdapter(cmeAdapter);
                    }
                    Log.v("CmeFragment", "CmeFragment:::" + cmeTabModel.getResponse().getOrgUnits().get(0).getName());
                }
            }
        }
    }

    @Override
    public void onBackgroundJobAbort(int requestCode, Object reason) {
        Methods.closeProgressDialog();
    }

    @Override
    public void onBackgroundJobError(int requestCode, Object error) {
        Methods.closeProgressDialog();

    }

    @Override
    public boolean needAsyncResponse() {
        return true;
    }

    @Override
    public boolean needResponse() {
        return true;
    }
}
