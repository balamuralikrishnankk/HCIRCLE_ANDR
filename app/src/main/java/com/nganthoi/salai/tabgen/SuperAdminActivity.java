package com.nganthoi.salai.tabgen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import connectServer.ConnectServer;
import customDialogManager.CustomDialogManager;
import sharePreference.SharedPreference;

public class SuperAdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context _context = this;
    ProgressDialog progDialog;
    ConnectServer connectServer;
    SharedPreference sp;
    List<String> list1, list2;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    LayoutInflater layoutInflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* displaying progress dialog for loading data from server*/
        progDialog = new ProgressDialog(_context);
        progDialog.setMessage("Wait please...");
        progDialog.setIndeterminate(true);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.show();
        /***************************************/

        /* Getting textView IDs from the drawer */
        TextView username = (TextView) findViewById(R.id.username);
        TextView usermail = (TextView) findViewById(R.id.user_email);
        TextView userrole = (TextView) findViewById(R.id.user_role);
        // Getting ListViews IDs

        /**************************************/
        /*
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        orgUnitList.setLayoutParams(new RelativeLayout.LayoutParams(width,height/2));
        orgList.setLayoutParams(new RelativeLayout.LayoutParams(width,height/2));*/

        viewPager = (ViewPager) findViewById(R.id.superadmin_container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.superadminTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        /* Getting user details from the shared preference */
        sp = new SharedPreference();
        String user_details = sp.getPreference(_context);
        try {
            JSONObject jsonObject = new JSONObject(user_details);
            username.setText(jsonObject.getString("username"));
            usermail.setText(jsonObject.getString("email"));
            userrole.setText(jsonObject.getString("roles"));
        } catch (JSONException e) {
            System.out.println("Exception :" + e.toString());
        }
        progDialog.dismiss();
        /***************************************************/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_superadmin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "No action is assigned yet for this button", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.CreateOrganisation://Create an Organisation
                startActivity(new Intent(_context, CreateOrg.class));
                break;
            case R.id.logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
                alertDialogBuilder.setTitle("Logout ?");
                alertDialogBuilder.setMessage("Are you sure to logout?");
                alertDialogBuilder.setIcon(R.drawable.failure_icon);
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(_context, MainActivity.class));
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
                break;
            case R.id.findByOrgName:
                findOrgByName();
                break;
            case R.id.CreateRole:
                startActivity(new Intent(_context, CreateRoleActivity.class));
                break;
            case R.id.CreateOrgUnit:
                startActivity(new Intent(_context, CreateOrgUnit.class));
                break;
            case R.id.CreateUser:
                startActivity(new Intent(_context, CreateUserActivity.class));
                break;
            default:
                CustomDialogManager cdm = new CustomDialogManager(_context, "Under Development", "This action is under devolopment", true);
                cdm.showCustomDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* Method for finding organisation */
    public void findOrgByName() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setTitle("Find an organisation");
        alertDialogBuilder.setMessage("Please enter your organisation name below:");

        progDialog = new ProgressDialog(_context);
        progDialog.setMessage("Wait please...");
        progDialog.setIndeterminate(true);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final EditText orgName = new EditText(_context);
        orgName.setPadding(15, 20, 15, 20);
        //orgName.setBackground(R.drawable.border_focus_background);
        alertDialogBuilder.setView(orgName);
        alertDialogBuilder.setPositiveButton("FIND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String organisationName = orgName.getText().toString();
                if (organisationName.trim().length() == 0) {
                    Toast.makeText(_context, "You have to enter organisation name.", Toast.LENGTH_LONG).show();
                } else {
                    JSONObject jobj = new JSONObject();
                    try {
                        jobj.put("name", organisationName);
                        new FindOrg().execute(jobj);
                    } catch (JSONException e) {
                        System.out.println("JSON Exception: " + e.toString());
                    }
                }
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
    /* -------------End of findOrgByName()----------------- */

    public class FindOrg extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected void onPreExecute() {
            progDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... jObj) {
            connectServer = new ConnectServer("http://188.166.210.24:8065/api/v1/organisation/findByName");
            InputStream is = connectServer.putData(jObj[0]);
            return connectServer.convertInputStreamToString(is);
        }

        protected void onProgressUpdate() {
            progDialog.show();
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString != null)
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (connectServer.responseCode == 200) {
                        String createdOrgDetails = "ID: " + jsonObject.getString("id") + "\n" +
                                "Organisation Name: " + jsonObject.getString("name") + "\n" +
                                "Email: " + jsonObject.getString("email") + "\n" +
                                "Created By: " + jsonObject.getString("createdBy");
                        CustomDialogManager cdm = new CustomDialogManager(_context, "Organisation Details", createdOrgDetails, true);
                        cdm.showCustomDialog();
                    } else if (connectServer.responseCode == -1) {
                        CustomDialogManager cdmError = new CustomDialogManager(_context, "Failed to search organisation"
                                , "Unable to contact server", false);
                        cdmError.showCustomDialog();
                    } else {
                        CustomDialogManager cdmError = new CustomDialogManager(_context, "Organisation Not Found",
                                jsonObject.getString("message"), false);
                        cdmError.showCustomDialog();
                    }
                } catch (JSONException jsonExp) {
                    System.out.println(jsonExp.toString());
                }
            progDialog.dismiss();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OrganisationListFragment(), "ORGANISATIONS");
        adapter.addFragment(new OrganisationUnitListFragment(), "ORGANISATION UNITS");
        viewPager.setAdapter(adapter);
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
}