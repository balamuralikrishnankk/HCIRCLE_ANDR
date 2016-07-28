package com.nganthoi.salai.tabgen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Utils.PreferenceHelper;
import activity.CmeLandingActivity;
import activity.NewsFlipperActivity;
import activity.NewsTabActivity;
import activity.ViewFlipperActivity;
import sharePreference.SharedPreference;


public class UserLandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreference sp;
    PreferenceHelper preferenceHelper;
    String role;//user role
    Context _context=this;
    List<String> list;
    //ListView templateList;
    //ArrayAdapter<String> arrayAdapter;
    //TemplateAdapter templateAdapter;
    public final static String templateListExtra="TEMPLATE_LIST";
    public final static String tabPosition="TAB_POSITION";
    ArrayList<String> stringArray;
    ProgressDialog progressDialog;
    Button chat,cme,ref,news;
    String team,user_id;//team name
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceHelper=new PreferenceHelper(this);
        setContentView(R.layout.activity_user_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Getting textView IDs from the drawer */
        TextView username = (TextView) findViewById(R.id.username);
        TextView usermail = (TextView) findViewById(R.id.user_email);
        TextView userrole = (TextView) findViewById(R.id.user_role);

        //Getting Button Ids
        chat = (Button) findViewById(R.id.landing_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(_context,UserActivity.class);
                    intent.putStringArrayListExtra(templateListExtra,stringArray);
                    intent.putExtra(tabPosition, 0);
                    startActivity(intent);
            }
        });

        ref = (Button) findViewById(R.id.landing_reference);
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(_context,UserActivity.class);
                    intent.putStringArrayListExtra(templateListExtra,stringArray);
                    intent.putExtra(tabPosition, 1);
                    startActivity(intent);
            }
        });

        cme = (Button) findViewById(R.id.landing_cme);
        cme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(_context, CmeLandingActivity.class);
                startActivity(intent);
//                    Intent intent = new Intent(_context,UserActivity.class);
//                    intent.putStringArrayListExtra(templateListExtra,stringArray);
//                    intent.putExtra(tabPosition, 2);
//                    startActivity(intent);
            }
        });

        news = (Button) findViewById(R.id.landing_news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(_context, NewsTabActivity.class);
                startActivity(intent);
//                    Intent intent = new Intent(_context,UserActivity.class);
//                    intent.putStringArrayListExtra(templateListExtra,stringArray);
//                    intent.putExtra(tabPosition, 3);
//                    startActivity(intent);
            }
        });
        sp = new SharedPreference();
        String user_details = sp.getPreference(_context);

        try {
            JSONObject jsonObject = new JSONObject(user_details);
            username.setText(jsonObject.getString("username"));
            usermail.setText(jsonObject.getString("email"));
            role = jsonObject.getString("roles");
            user_id=jsonObject.getString("id");
            preferenceHelper.addString("LOGIN_USER_ID",user_id);
            userrole.setText(role);
            team = sp.getTeamNamePreference(_context);
            System.out.println("Team Name: " + team + "\n");
            new GetTemplates(team).execute(user_id);

        } catch (JSONException e) {
            System.out.println("Exception :" + e.toString());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            logout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* function for logout */
    public void logout(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setTitle("Logout ?");
        alertDialogBuilder.setMessage("Are you sure to logout?");
        alertDialogBuilder.setIcon(R.drawable.failure_icon);
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(_context, MainActivity.class));
                finish();
                sp.clearPreference(_context);
            }
        });
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public class GetTemplates extends AsyncTask<String,String,List<String>>{
        String team_name;
        public GetTemplates(String team){
            team_name = team;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(_context);
            progressDialog.setMessage("Loading your Templates");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... user_id){
            publishProgress("");
            list = OrganisationDetails.getListOfTemplates(_context,user_id[0]);
            return list;
        }
        protected void onProgressUpdate(String str){
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<String> list){
            //templateAdapter = new TemplateAdapter(UserLandingActivity.this,list);
            //templateList.setAdapter(templateAdapter);
            stringArray = new ArrayList<String>();
            for(int i=0;i<list.size();i++){
                stringArray.add(list.get(i));
            }
            progressDialog.dismiss();
        }
    }
}
