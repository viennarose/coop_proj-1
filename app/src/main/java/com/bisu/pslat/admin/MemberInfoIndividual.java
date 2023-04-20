package com.bisu.pslat.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class MemberInfoIndividual extends AppCompatActivity {

    private String fullname;
    private String cbu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info_individual);

        TextView fullName = (TextView) findViewById(R.id.memberFULLNAME);
        TextView cbuVal = (TextView) findViewById(R.id.memberCBU);
        TextView datereq = (TextView) findViewById(R.id.memberDATE);
        TextView total = (TextView) findViewById(R.id.memberTOTAL);
        Button back = findViewById(R.id.memberbackButton);


        Intent intent = getIntent();
        String full_Name = intent.getStringExtra("fullname");
        full_Name = "Name: " + full_Name;
        String cbu = intent.getStringExtra("cbu");
        cbu = "CBU: " + cbu;
        String dateCreated = intent.getStringExtra("date_created");
        dateCreated = "Date Paid: " + dateCreated;

        // Check if username and CBU are not null
        if (dateCreated != null && cbu != null && full_Name != null) {
            // Set username and CBU in views
            datereq.setText(dateCreated);
            cbuVal.setText(cbu);
            fullName.setText(full_Name);
        } else {
            // Handle the null values here
            Toast.makeText(this, "Username or CBU is null", Toast.LENGTH_SHORT).show();
            finish();
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberInfoIndividual.this, AllMembers.class);
                startActivity(intent);
                finish();
            }
        });
    }
}