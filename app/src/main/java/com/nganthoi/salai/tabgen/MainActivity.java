package com.nganthoi.salai.tabgen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connectServer.CheckInternetConnection;
import connectServer.ConnectServer;
import customDialogManager.CustomDialogManager;
import sharePreference.SharedPreference;

public class MainActivity extends Activity {
    Button signin;
    Intent intent;
    Context context=this;
    String msg,uname, passwd, team,server_ip;
    EditText username,password,team_name;
    //CheckBox show_password;
    ProgressDialog progressDialog;
    TextView forgotPassword;
    InputStream is;
    ConnectServer cs;
    SharedPreference sp;
    TextView signup;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signin = (Button) findViewById(R.id.signIn);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        //show_password = (CheckBox) findViewById(R.id.show_password);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        server_ip="128.199.111.18";
        //forgotPassword.setPaintFlags(forgotPassword.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        //forgotPassword.setText(Html.fromHtml("<u><i>Forgot Password ?</i></u>"));

        sp = new SharedPreference();
        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidate()==true){
                    try {
                        uname = username.getText().toString().trim();
                        passwd = password.getText().toString().trim();
                        team = "neworgunit";
                        sp.saveServerIP_Preference(context,server_ip);
                        JSONObject jsonObject= new JSONObject();
                        jsonObject.put("name",team);
                        jsonObject.put("username", uname);//username.getText().toString()
                        jsonObject.put("password", passwd);//password.getText().toString()
                        progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Wait Please.....");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        UserLogin ul = new UserLogin();
                        ul.execute(jsonObject);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri forgotPassword = Uri.parse("http://"+sp.getServerIP_Preference(context)+
                        ":8065/"+team_name.getText().toString()+
                        ""+"/reset_password");
                intent = new Intent(Intent.ACTION_VIEW, forgotPassword);
                startActivity(intent);
            }
        });
        /*
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });*/
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        //moveTaskToBack(true);
    }

    public Boolean isValidate(){
        CheckInternetConnection ic = new CheckInternetConnection(this);
        CustomDialogManager cdm;
        Boolean testInternet = ic.isMobileInternetConnected()||ic.isWifiInternetConnected();
        if(username.getText().toString().trim().length()==0){
            msg="Please enter your username";
            cdm =  new CustomDialogManager(MainActivity.this,"Username empty",msg,false);
            cdm.showCustomDialog();
            return false;
        }
        else if(password.getText().toString().trim().length()==0){
            msg="Please enter your password";
            cdm =  new CustomDialogManager(MainActivity.this,"Password empty",msg,false);
            cdm.showCustomDialog();
            return false;
        }
        else if(!testInternet){
            msg="You device is not connected to internet. Please check your connection!";
            cdm =  new CustomDialogManager(MainActivity.this,"No Internet Connection",msg,false);
            cdm.showCustomDialog();
            return false;
        }
        else if(server_ip.trim().length()==0){
            msg="Please setup your server IP";
            cdm =  new CustomDialogManager(MainActivity.this,"Set Up server IP",msg,false);
            cdm.showCustomDialog();
            return false;
        }
        return true;
    }

    public class UserLogin extends AsyncTask<JSONObject,Void,String>{
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... jObj){
            String ip = sp.getServerIP_Preference(context);
            cs = new ConnectServer("http://"+ip+":8065/api/v1/users/login");
            is = cs.putData(jObj[0]);
            String result = cs.convertInputStreamToString(is);
            return result;
        }

        protected void onProgressUpdate(){
            progressDialog.show();
        }

        @Override
        protected  void onPostExecute(String json){
            if(json!=null){
                JSONObject jObj=null;
                try {
                    jObj=new JSONObject(json);
                    if(cs.responseCode==200){
                        progressDialog.dismiss();
                        sp.savePreference(context, json);
                        //Getting Token Id
                        String TokenId = cs.conn.getHeaderField("Token");
                        if(TokenId==null) System.out.println("Token is null");
                        else {
                            System.out.println("Token ID: "+TokenId);
                            //Toast.makeText(MainActivity.this,"Token ID: "+TokenId,Toast.LENGTH_SHORT).show();
                            sp.saveTokenPreference(context,TokenId);
                        }
                        System.out.println(jObj.getString("id"));
                        switch(jObj.getString("roles")){
                            case "system_admin":
                                intent = new Intent(context,SuperAdminActivity.class);
                                Toast.makeText(MainActivity.this, "Sucessfully login as Superadmin...", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                finish();
                                break;
                            case "admin":
                                intent = new Intent(context,Admin.class);
                                Toast.makeText(context,"Sucessfully login as Admin...",Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                finish();
                                break;
                            default:
                                intent = new Intent(context,UserLandingActivity.class);
                                Toast.makeText(context,"You have sucessfully login",Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                finish();
                                break;
                            /*default:
                                Toast.makeText(context,"Status Code: "+jObj.getInt("Status_code"),Toast.LENGTH_LONG).show();*/
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        CustomDialogManager error = new CustomDialogManager(context,"Login Failed",jObj.getString("message"),false);
                        error.showCustomDialog();
                    }
                }catch(JSONException e){
                    System.out.println("JSON Exception occurs here: " + e.toString());
                }
            }
            else
            {
                CustomDialogManager error = new CustomDialogManager(context,"Server Problem","Failed to connect server",false);
                error.showCustomDialog();

            }
            progressDialog.dismiss();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
