package com.nganthoi.salai.tabgen;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import connectServer.ConnectServer;
import sharePreference.SharedPreference;

public class CreateOrgUnit extends AppCompatActivity {
    TextView respMsg;
    EditText orgUnitName, orgUnitDisplayName;
    Spinner orgListSpinner;
    List<String> orgList;
    SharedPreference pref;
    Context context=this;
    String username,email,Org,Org_Unit,Org_Unit_display;
    Button createOrgUnit;
    ProgressDialog progressDialog;
    ConnectServer cs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_org_unit);

        /* Getting IDs*/
        respMsg = (TextView) findViewById(R.id.RespMsgCreateOrgUnit);
        orgUnitName = (EditText) findViewById(R.id.OrgUnitName);
        orgUnitDisplayName = (EditText) findViewById(R.id.OrgUnitDisplayName);
        orgListSpinner = (Spinner) findViewById(R.id.spinnerOrgList);
        createOrgUnit = (Button) findViewById(R.id.createOrgUnit);
        /************************************************/
        respMsg.setText("");
        //Getting user details from shared Preference
        pref = new SharedPreference();
        String user_details = pref.getPreference(context);
        try{
            JSONObject jObj = new JSONObject(user_details);
            username = jObj.getString("username");
            email = jObj.getString("email");
        }catch(Exception e){
            System.out.println("Exception here: "+e.toString());
        }
        /* Getting list of organisation */
        orgList = new ArrayList<String>();
        orgList = OrganisationDetails.getListOfOrganisations(username,context);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,orgList);
        //Setting drop down layout styles
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //attaching dataAdapter to spinner 'ListOfOU'
        orgListSpinner.setAdapter(dataAdapter);
        //Setting onSelected Event for spinner
        orgListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Org = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing is selected.
                Org = " ";
            }
        });
        /********************************/

        createOrgUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Org_Unit = ""+orgUnitName.getText().toString();
                Org_Unit_display = ""+orgUnitDisplayName.getText().toString();
                if(validate()){
                    //showing progress dialog
                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setMessage("Wait Please.....");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    JSONObject org_unitObject = new JSONObject();// json data for creating Organisation Unit
                    JSONObject teamObj = new JSONObject();//json data for creating Team
                    try{
                        /*----Assigning Organisation Unit data -----*/
                        org_unitObject.put("name",Org_Unit_display);
                        org_unitObject.put("email",email);
                        org_unitObject.put("organisation",Org);
                        org_unitObject.put("organisation_unit",Org_Unit);
                        org_unitObject.put("createdBy",username);
                         /*----Assigning Team data -----*/
                        teamObj.put("display_name",Org_Unit_display);
                        teamObj.put("email", email);
                        teamObj.put("name", Org_Unit);
                        teamObj.put("type", "O");

                        //Starts creating team and Organisation Unit
                        createTeamAndOrgUnit(teamObj, org_unitObject);
                    }
                    catch(Exception e){
                        System.out.println("JSON exception goes here: " + e.toString());
                    }
                }
            }
        });
    }
    public Boolean validate(){
        Boolean state=true;
        if(Org_Unit == ""){
            state = false;
            respMsg.setText("Please enter your organisation Unit name");
            respMsg.setTextColor(Color.RED);
            respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        }else if(Org_Unit_display == ""){
            state = false;
            respMsg.setText("Please enter your organisation Unit display name");
            respMsg.setTextColor(Color.RED);
            respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        }else if(Org == ""){
            state = false;
            respMsg.setText("Please select an organisation ");
            respMsg.setTextColor(Color.RED);
            respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        else {
            respMsg.setTextColor(Color.BLUE);
            respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            respMsg.setText(" ");
            state=true;
        }
        return state;
    }
    /* function for creating team and organisation unit */
    public void createTeamAndOrgUnit(JSONObject teamObj,JSONObject org_unitObject){
        SharedPreference sp = new SharedPreference();
        ConnectServer conn = new ConnectServer("http://"+sp.getServerIP_Preference(context)+":8065/api/v1/teams/create");
        String res = conn.convertInputStreamToString(conn.putData(teamObj));
        if(res!=null){
            try{
                JSONObject jObj = new JSONObject(res);
                if(conn.responseCode==200) {
                    System.out.println("Team created...");
                    Toast.makeText(context,"Team Created",Toast.LENGTH_SHORT).show();
                    //starts creating Organisation Units
                    new CreateOrganisationUnit().execute(org_unitObject);
                }
                else{
                    respMsg.setText(jObj.getString("message"));
                }
            }catch(Exception e){
                System.out.println("Exception in creating team: "+e.toString());
                respMsg.setText("Oops! There is an unknown problem in reading server response, try later");
            }
        }
    }
    //Class for creating Organisation Unit
    public class CreateOrganisationUnit extends AsyncTask<JSONObject,Void,String> {
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... jobj){
            cs = new ConnectServer("http://188.166.210.24:8065/api/v1/organisationUnit/create");
            return cs.convertInputStreamToString(cs.putData(jobj[0]));
        }

        protected void onProgressUpdate(){
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String jsonStr){
            if(jsonStr!=null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    if (cs.responseCode == 200) {
                        progressDialog.dismiss();
                        respMsg.setText("Organisation Unit has been created");
                        respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                        respMsg.setTextColor(Color.BLUE);
                    } else {
                        respMsg.setText("Failed to create Organisation Unit: \n" + jsonObject.getString("message"));
                        respMsg.setTextColor(Color.RED);
                        respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }catch(Exception e){
                    System.out.print("An Exception occurs here: "+e.toString());
                }
            }
            else{
                respMsg.setText("Oops! There may be some problems with the server. Try again Later.");
                respMsg.setTextColor(Color.RED);
                respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            progressDialog.dismiss();
        }


    }

}
