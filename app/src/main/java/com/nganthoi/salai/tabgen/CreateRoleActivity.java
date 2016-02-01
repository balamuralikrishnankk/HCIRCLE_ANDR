package com.nganthoi.salai.tabgen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import connectServer.ConnectServer;
import sharePreference.SharedPreference;

public class CreateRoleActivity extends AppCompatActivity {
    Intent intent;
    Context _context=this;
    EditText roleName;
    TextView respMsg;
    Spinner ListOfOU;
    RadioGroup can_access_other_ou;
    Button createRole;
    String Role_Name,Org_Unit,caou=" ";
    ProgressDialog progressDialog;
    ConnectServer cs;
    List<String> list;
    SharedPreference sp;
    String username, sharedPrefData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_role);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /******** Getting objects IDs ***********/
        respMsg = (TextView) findViewById(R.id.respMsgForCreateRole);
        roleName = (EditText) findViewById(R.id.role_name);
        ListOfOU = (Spinner) findViewById(R.id.OrgUnitList);
        can_access_other_ou = (RadioGroup) findViewById(R.id.CanAccessOtherOU);
        createRole = (Button) findViewById(R.id.createRole);
        /*********************************************/

        respMsg.setText(" ");

         /* Getting user details from the shared preference */

        sp = new SharedPreference();
        sharedPrefData = sp.getPreference(_context);
        /****************************************************/
        /* Setting Spinner */
        list = new ArrayList<String>();

        //Getting list of Organisation Units
        try {
            JSONObject user_data = new JSONObject(sharedPrefData);
            username = user_data.getString("username");
            list = OrganisationDetails.getListOfOrganisationUnits(username,_context);
        }
        catch(Exception e){
            System.out.println("Exception here: "+e.toString());
        }
        /**********************************************************/

        //Creating adapter for spinner 'ListOfOU'
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        //Setting drop down layout styles
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //attaching dataAdapter to spinner 'ListOfOU'
        ListOfOU.setAdapter(dataAdapter);
        //Setting onSelected Event for spinner
        ListOfOU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Org_Unit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing is selected.
                Org_Unit = " ";
            }
        });
        /********************************/

        /* Action Event for creating organisation role */
        createRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    Role_Name = roleName.getText().toString();
                    JSONObject jsonObj = new JSONObject();
                    try{
                        jsonObj.put("OrganisationUnit", Org_Unit);
                        jsonObj.put("universalRole",caou);
                        jsonObj.put("role_name",Role_Name);
                        progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Wait Please.....");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        new CreateRoles().execute(jsonObj);
                    }catch(JSONException e){
                        System.out.println("JSONException: "+e.toString());
                    }
                    catch(Exception e) {
                        System.out.println("Other Exception: "+e.toString());
                    }
                }
            }
        });
        /* ---End of Create Role Action--- */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private Boolean validate(){
        int radioId = can_access_other_ou.getCheckedRadioButtonId();
        RadioButton caou_radio = (RadioButton) findViewById(radioId);
        switch(caou_radio.getId()){
            case R.id.caou_Yes:
                caou = "Yes";
                break;
            case R.id.caou_No:
                caou = "No";
                break;
        }

        if(roleName.getText().toString()+""==""){
            respMsg.setText("Please enter the Role Name");
            respMsg.setTextColor(Color.RED);
            respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            return false;
        }
        else if(Org_Unit==" "){
            respMsg.setText("Please select an Organisation Unit");
            respMsg.setTextColor(Color.RED);
            respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            return false;
        }
        else if(caou==" "){
            respMsg.setText("Please select the choices (Yes/No) whether it will be able to access other Organisation Unit also");
            respMsg.setTextColor(Color.RED);
            respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            return false;
        }
        else return true;
    }

    public class CreateRoles extends AsyncTask<JSONObject,Void,String>{
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... jobj){
            SharedPreference sp = new SharedPreference();
            cs = new ConnectServer("http://"+sp.getServerIP_Preference(_context)+":8065/api/v1/organisationRole/create");
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
                        respMsg.setText("Organisation Role has been created");
                        respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                        respMsg.setTextColor(Color.BLUE);
                    } else {
                        respMsg.setText("Failed to create role: \n" + jsonObject.getString("message"));
                        respMsg.setTextColor(Color.RED);
                        respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }catch(Exception e){
                    System.out.print("An Exception occurs here: "+e.toString());
                }
            }
            else{
                respMsg.setText("Oops! There may be some problems with the server. Try again.");
                respMsg.setTextColor(Color.RED);
                respMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            progressDialog.dismiss();
        }
    }

}
