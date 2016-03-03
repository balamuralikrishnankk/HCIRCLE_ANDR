package com.nganthoi.salai.tabgen;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import connectServer.ConnectServer;
import customDialogManager.CustomDialogManager;
import sharePreference.SharedPreference;


public class CreateOrg extends AppCompatActivity {
    EditText org_name/*organisation name*/,org_display_name/*organisation display name*/;
    TextView respText;/*textview for displaying Response message*/
    String organisation_name=null,display_name=null;
    Button createOrg;/* Button for creating organisation */
    Context _context=this;
    /* Creating an object for connecting the API at the url http://188.166.210.24:8065/api/v1/organisation/create */

    ConnectServer connectServer;
    ProgressDialog progDialog;
    /* Creating SharedPreference object for getting the user details saved at the time of login */
    SharedPreference sharedPreference;
    String user_details; // This string will contain the details of the user
    JSONObject userData; // This JSON object will contain the content of the above string "user_details" in the form of JSON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_org);

        SharedPreference sp = new SharedPreference();
        connectServer=new ConnectServer("http://"+sp.getServerIP_Preference(_context)+":8065/api/v1/organisation/create");

        /* Getting Id references */
        org_name = (EditText) findViewById(R.id.org_name);
        org_display_name = (EditText) findViewById(R.id.org_display_name);
        respText = (TextView) findViewById(R.id.resMsg);
        createOrg = (Button) findViewById(R.id.createOrg);
        /****************************************************/

        respText.setText("");

        /* Creating Organisation */
        createOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                organisation_name = org_name.getText().toString()+"";
                display_name=org_display_name.getText().toString()+"";

                /* Getting user details from the SharedPreferene */
                sharedPreference = new SharedPreference();
                user_details = sharedPreference.getPreference(_context);
                try {
                    userData = new JSONObject(user_details);
                    System.out.println("Organisation details: ");
                    System.out.println("Organisation name: "+organisation_name);
                    System.out.println("Email ID: "+userData.getString("email"));
                    System.out.println("Created By: "+userData.getString("username"));
                    System.out.println("Display name: "+display_name);
                }catch(JSONException e){
                    System.out.println("JSON Exception for creating object: "+e.toString());
                }
                /**************************************************/

                if(isValidate()){
                    JSONObject jObj = new JSONObject();
                    progDialog = new ProgressDialog(v.getContext());
                    progDialog.setMessage("Wait please...");
                    progDialog.setIndeterminate(true);
                    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    try{
                        jObj.put("name",organisation_name);
                        jObj.put("email",userData.getString("email"));
                        jObj.put("createdBy",userData.getString("username"));
                        jObj.put("display_name",display_name);
                        new CreateOrganisation().execute(jObj);
                    }
                    catch(JSONException e){
                        System.out.println("JSON Exception for setting values: "+e.toString());
                    }
                }
            }
        });


    }

    private Boolean isValidate(){
        CustomDialogManager validationDialog;
        if(org_name.getText().toString().trim().length()==0){
            validationDialog = new CustomDialogManager(_context,"Empty fields","Please give an organisation name",false);
            validationDialog.showCustomDialog();
            return false;
        }
        else if(org_display_name.getText().toString().trim().length()==0){
            validationDialog = new CustomDialogManager(_context,"Empty fields","Please enter a display name",false);
            validationDialog.showCustomDialog();
            return false;
        }
        else return true;
    }

    public class CreateOrganisation extends AsyncTask<JSONObject,Void,String>{
        @Override
        protected void onPreExecute(){
            progDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... jObj){
            InputStream is = connectServer.putData(jObj[0]);
            return connectServer.convertInputStreamToString(is);
        }

        protected void onProgressUpdate(){
            progDialog.show();
        }

        @Override
        protected void onPostExecute(String jsonString){
            if(jsonString!=null)
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if(connectServer.responseCode==200){
                        String createdOrgDetails = "ID: "+jsonObject.getString("id")+"\n"+
                                "Organisation Name: "+jsonObject.getString("name")+"\n"+
                                "Email: "+jsonObject.getString("email")+"\n"+
                                "Created By: "+jsonObject.getString("createdBy");
                        respText.setTextColor(Color.BLUE);
                        respText.setText("Organisation created with the following details: \n"+
                                createdOrgDetails);
                    }
                    else if(connectServer.responseCode==-1){
                        CustomDialogManager cdmError = new CustomDialogManager(_context,"Failed to create organisation"
                                ,"Unable to contact server",false);
                        cdmError.showCustomDialog();
                    }
                    else {
                        respText.setTextColor(Color.RED);
                        respText.setText("Failed to create organisation: "+jsonObject.getString("message"));
                    }
                }catch(JSONException jsonExp){
                    System.out.println(jsonExp.toString());
                }
            progDialog.dismiss();
        }
    }
}


