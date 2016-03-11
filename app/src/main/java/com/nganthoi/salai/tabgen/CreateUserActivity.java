package com.nganthoi.salai.tabgen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import connectServer.ConnectServer;
import sharePreference.SharedPreference;

public class CreateUserActivity extends AppCompatActivity {
    Intent intent;
    EditText ET_Username,ET_Password,ET_ConfPasswd,ET_Email;
    Spinner Sp_Org_Unit,Sp_User_Role;
    RadioGroup CanAlloOffer;
    Button createUser,back;
    TextView response;
    ProgressDialog progressDialog;
    Context _context= this;
    String OrgUnit="", Role="", type="";
    List<String> org_unit_list, role_list;
    SharedPreference sp;
    ConnectServer connServ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        /*getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.createUserToolbar);
        setSupportActionBar(toolbar);
        TextView user_admin = (TextView) toolbar.findViewById(R.id.user_admin);
        /* Finding ID references */
        findIDs();
        response.setText("");
        /*************************/
        sp = new SharedPreference();
        try{
            JSONObject jObj = new JSONObject(sp.getPreference(_context));
            user_admin.setText(jObj.getString("username"));
            org_unit_list = OrganisationDetails.getListOfOrganisationUnits(/*jObj.getString("username")*/"thoiba",_context);
        }catch(Exception e){
            System.out.println("Exception, unable to read shared preference data: "+e.toString());
        }
        /*setting list of user roles*/
        role_list = new ArrayList<String>();
        role_list.add("Doctor");
        role_list.add("Nurse");
        role_list.add("Administrator");
        role_list.add("Technicians");
        role_list.add("Radiologist");
        role_list.add("admin");
        /***********************************/
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,org_unit_list);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,role_list);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Sp_Org_Unit.setAdapter(adapter1);
        Sp_User_Role.setAdapter(adapter2);

        /*Event for selecting an item (Organisation Unit) from the spinner*/
        Sp_Org_Unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OrgUnit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing is selected.
                OrgUnit = " ";
            }
        });

        /*Event for selecting an item (user role) from the spinner*/
        Sp_User_Role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Role = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing is selected.
                Role = " ";
            }
        });

        /*Action event for create user button */
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()){
                    initiateProgressDialog(v);
                    String data="username="+ET_Username.getText().toString()+
                            "&password="+ET_Password.getText().toString()+
                            "&conf_pwd="+ET_ConfPasswd.getText().toString()+
                            "&email="+ET_Email.getText().toString()+
                            "&org_unit="+OrgUnit+
                            "&Role="+Role+
                            "&type="+type;

                    response.setTextColor(Color.BLUE);
                    response.setText("");
                    new CreateUser().execute(data);
                }
                else {
                    response.setTextColor(Color.RED);
                    response.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        });
        /*Radio Button action event*/
        CanAlloOffer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.CanAllowOfferYes:
                        type ="yes";
                        break;
                    case R.id.CanAllowOfferNo:
                        type = "no";
                        break;
                    default: type=" ";
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void findIDs(){
        ET_Username = (EditText) findViewById(R.id.username);
        ET_Password = (EditText) findViewById(R.id.create_user_password);
        ET_ConfPasswd = (EditText) findViewById(R.id.create_confPasswd);
        ET_Email = (EditText) findViewById(R.id.create_email);
        Sp_Org_Unit = (Spinner) findViewById(R.id.spinnerOU);
        Sp_User_Role = (Spinner) findViewById(R.id.spinnerRole);
        CanAlloOffer = (RadioGroup) findViewById(R.id.radioGroupCanAllowOffer);
        createUser = (Button) findViewById(R.id.create_user_button);
        response = (TextView) findViewById(R.id.response);
        back = (Button) findViewById(R.id.back_to_adminactivity);
    }

    public void initiateProgressDialog(View v){
        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setMessage("Wait Please.....");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }
    /* Input validation method*/
    public Boolean validate(){
        if(ET_Username.getText().toString().equals("")){
            response.setText("User name is blank, please fill it");
            return false;
        }
        else if(ET_Password.getText().toString().equals("")){
            response.setText("Password is blank, please fill it");
            return false;
        }
        else if(ET_ConfPasswd.getText().toString().equals("")){
            response.setText("Confirm Password is blank, please fill it");
            return false;
        }
        else if(ET_Email.getText().toString().equals("")){
            response.setText("Email is blank, please fill it");
            return false;
        }
        else if(OrgUnit==""){
            response.setText("Please select an organisation unit.");
            return false;
        }
        else if(Role==""){
            response.setText("Please select a user role.");
            return false;
        }
        else if(type==""){
            response.setText("Please choose whether the user will be allowed to offer.");
            return false;
        }
        else
            return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.logout){
            intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }*/
        if(id == R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public class CreateUser extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... data){
            SharedPreference sp = new SharedPreference();
            connServ = new ConnectServer("http://"+sp.getServerIP_Preference(_context)+"/TabGenAdmin/createUsers.php");
            onProgressUpdate();
            return connServ.convertInputStreamToString(connServ.putData(data[0]));
        }
        protected void onProgressUpdate(){
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(String respData){
            if(connServ.responseCode==200) {
                if(respData.trim().equals("true")) {
                    response.setText("User has been created");
                    response.setTextColor(Color.BLUE);
                }
                else{
                    response.setText(respData);
                    response.setTextColor(Color.RED);
                }
            }
            else {
                response.setText("Server is temporarily down, try again later");
                response.setTextColor(Color.RED);
            }
            progressDialog.dismiss();
        }
    }

}
