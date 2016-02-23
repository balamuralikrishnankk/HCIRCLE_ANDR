package com.nganthoi.salai.tabgen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sharePreference.SharedPreference;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    Context _context=this;
    ProgressDialog progressDialog;
    String role;//role of the user
    SharedPreference sp;
    List<String> list;
    ArrayList<String> template_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Getting textView IDs from the drawer */
        TextView username = (TextView) findViewById(R.id.username);
        TextView usermail = (TextView) findViewById(R.id.user_email);
        TextView userrole = (TextView) findViewById(R.id.user_role);
        /* Getting user details from the shared preference */
        sp = new SharedPreference();

        String user_details = sp.getPreference(_context);
        try {
            JSONObject jsonObject = new JSONObject(user_details);
            username.setText(jsonObject.getString("username"));
            usermail.setText(jsonObject.getString("email"));
            role = jsonObject.getString("roles");
            userrole.setText(role);
        } catch (JSONException e) {
            System.out.println("Exception :" + e.toString());
        }
        progressDialog = new ProgressDialog(_context);
        progressDialog.setMessage("Loading Your Templates....");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        Intent intent = getIntent();
        template_list = intent.getStringArrayListExtra(UserLandingActivity.templateListExtra);//gettting list of templates from the previous activity

        new GetTabList().execute(template_list);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "chat":
                        Toast.makeText(_context, "Chat is selected", Toast.LENGTH_SHORT).show();
                        break;
                    case "reference":
                        Toast.makeText(_context, "Reference is selected", Toast.LENGTH_SHORT).show();
                        break;
                    case "cme":
                        Toast.makeText(_context, "CME is selected", Toast.LENGTH_SHORT).show();
                        break;
                    case "news":
                        Toast.makeText(_context, "News is selected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            //logout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
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
        if (id == R.id.logout){
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    class GetTabList extends AsyncTask<ArrayList<String>,Void,List<String>> {
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }
        @Override
        protected List<String> doInBackground(ArrayList<String>... template_list){
            /*Getting list of Templates for a particular role */
            //list=OrganisationDetails.getListOfTemplates(_context,userRole[0]);
            list = new ArrayList<String>();
            for(int i=0;i<template_list[0].size();i++){
                list.add(template_list[0].get(i));
            }
            onProgressUpdate();
            return list;
        }

        protected void onProgressUpdate(){
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(List<String> list){

            setupViewPager(mViewPager, list);
            tabLayout.setupWithViewPager(mViewPager);

            try{
                setTabLayoutIcons(tabLayout,list);
            }catch(Exception e){
                System.out.println("Layout Exception: "+e.toString());
            }
            progressDialog.dismiss();
             /*
            tabLayout.getTabAt(0).setIcon(R.drawable.chat);
            tabLayout.getTabAt(1).setIcon(R.drawable.reference);
            tabLayout.getTabAt(2).setIcon(R.drawable.cme);
            tabLayout.getTabAt(3).setIcon(R.drawable.latest_news);*/

        }
    }

    private void setupViewPager(ViewPager viewPager,List<String> list) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for(int i=0;i<list.size();i++){
            /*Adding tabs and fragments according to the Templates from the server*/
            switch(list.get(i)){
                case "Chat Template"://check if Chat template exist
                    adapter.addFragment(new ChatFragment(), "CHAT");
                    break;
                case "Reference Template":
                    adapter.addFragment(new ReferenceFragment(), "REFERENCE");
                    break;
                case "CME Template":
                    adapter.addFragment(new CmeFragment(),"CME");
                    break;
                case "Latest News Template":
                    adapter.addFragment(new LatestNewsFragment(),"LATEST NEWS");
                    break;
            }
        }
        viewPager.setAdapter(adapter);
    }

    private void setTabLayoutIcons(TabLayout tabLayout,List<String> list) throws Exception{
        try {
            for (int i = 0; i < list.size(); i++) {
            /*Adding tabs and fragments according to the Templates from the server*/
                switch (list.get(i)) {
                    case "Chat Template"://check if Chat template exist
                        TextView chatView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                        chatView.setText(" ");//chatView.setText("CHAT");
                        chatView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_chat, 0, 0);
                        tabLayout.getTabAt(i).setCustomView(chatView);
                        tabLayout.getTabAt(i).setText("chat");
                        break;
                    case "Reference Template":
                        TextView refView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                        refView.setText(" ");//refView.setText("REFERENCE");
                        refView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_reference, 0, 0);
                        tabLayout.getTabAt(i).setCustomView(refView);
                        tabLayout.getTabAt(i).setText("reference");
                        break;
                    case "CME Template":
                        TextView cmeView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                        cmeView.setText(" ");//cmeView.setText("CME");
                        cmeView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_cme, 0, 0);
                        tabLayout.getTabAt(i).setCustomView(cmeView);
                        tabLayout.getTabAt(i).setText("cme");
                        break;
                    case "Latest News Template":
                        TextView newsView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                        newsView.setText(" ");//newsView.setText("LATEST NEWS");
                        newsView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_news, 0, 0);
                        tabLayout.getTabAt(i).setCustomView(newsView);
                        tabLayout.getTabAt(i).setText("news");
                        break;
                }
            }
        }catch(Exception e){
            System.out.println("Exception: "+e.toString());
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /* function for logout */
    public void logout(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setTitle("Logout ?");
        alertDialogBuilder.setMessage("Are you sure to logout?");
        alertDialogBuilder.setIcon(R.drawable.failure_icon);
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(_context, FirstLoginActivity.class));
                sp.clearPreference(_context);
                finish();
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

}
