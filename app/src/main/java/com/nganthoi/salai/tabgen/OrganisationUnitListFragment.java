package com.nganthoi.salai.tabgen;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sharePreference.SharedPreference;

/**
 * Created by SALAI on 1/18/2016.
 */
public class OrganisationUnitListFragment extends Fragment {
    View org_unit_layout;
    SharedPreference sp;
    List<String> list;
    ListView orgUnitList;
    ArrayAdapter<String> arrayAdapter;
    public OrganisationUnitListFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState){
        org_unit_layout=layoutInflater.inflate(R.layout.organisation_unit_list,container,false);
        sp = new SharedPreference();
        list = new ArrayList<String>();
        String user_details = sp.getPreference(org_unit_layout.getContext());
        try {
            JSONObject jsonObject = new JSONObject(user_details);
            list = OrganisationDetails.getListOfOrganisationUnits(jsonObject.getString("username"),org_unit_layout.getContext());
        } catch (JSONException e) {
            System.out.println("Exception :" + e.toString());
        }
        orgUnitList = (ListView) org_unit_layout.findViewById(R.id.OrgUnitListView);
        arrayAdapter = new ArrayAdapter<String>(org_unit_layout.getContext(),android.R.layout.simple_list_item_1, list);
        orgUnitList.setAdapter(arrayAdapter);
        return org_unit_layout;
    }
}
