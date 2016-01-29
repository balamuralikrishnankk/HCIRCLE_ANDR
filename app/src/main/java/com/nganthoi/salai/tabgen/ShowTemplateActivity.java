package com.nganthoi.salai.tabgen;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sharePreference.SharedPreference;

public class ShowTemplateActivity extends AppCompatActivity {
    SharedPreference sp;
    List<String> list;
    ListView templateList;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_template);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        templateList = (ListView) findViewById(R.id.listViewTemplates);
        sp = new SharedPreference();
        list = new ArrayList<String>();
        String user_details = sp.getPreference(this);
        try {
            JSONObject jsonObject = new JSONObject(user_details);
            list = OrganisationDetails.getListOfTemplates(this,jsonObject.getString("roles"));
        } catch (JSONException e) {
            System.out.println("Exception :" + e.toString());
        }
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
        templateList.setAdapter(arrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
