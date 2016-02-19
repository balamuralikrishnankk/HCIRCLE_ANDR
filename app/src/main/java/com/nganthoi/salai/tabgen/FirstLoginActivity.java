package com.nganthoi.salai.tabgen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstLoginActivity extends AppCompatActivity {
    Button signup;
    TextView login;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
        login = (TextView) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstLoginActivity.this, MainActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
