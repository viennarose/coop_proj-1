package com.bisu.pslat.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bisu.pslat.AdminLogin;
import com.bisu.pslat.Login;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Button userBtn = (Button) findViewById(R.id.usersButton);
        Button memberBtn = (Button) findViewById(R.id.memberButton);
        Button loanBtn = (Button) findViewById(R.id.loanButton);
        Button loansBtn = (Button) findViewById(R.id.loansButton);
        Button membersBtn = (Button) findViewById(R.id.membersButton);
        Button accBtn = (Button) findViewById(R.id.accButton);
        TextView logout = (TextView) findViewById(R.id.loutButton);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, AdminLogin.class);
                startActivity(intent);
                finish();
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, ViewUsers.class);
                startActivity(intent);
                finish();
            }
        });

        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, MembershipRequests.class);
                startActivity(intent);
                finish();
            }
        });

        loanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, LoanRequests.class);
                startActivity(intent);
                finish();
            }
        });

        membersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AllMembers.class);
                startActivity(intent);
                finish();
            }
        });

        loansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AllLoans.class);
                startActivity(intent);
                finish();
            }
        });


        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });
    }
}