package com.bisu.pslat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class JoinMembership extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_membership);
        EditText cbu = (EditText) findViewById(R.id.cbuAmount);
        Button submitBtn = (Button) findViewById(R.id.submitButton);
        Button cancelBtn = (Button) findViewById(R.id.cancelButton);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtn.setEnabled(false);
                if(cbu.getText().toString().matches("")){
                    Toast.makeText(JoinMembership.this, "Enter amount", Toast.LENGTH_SHORT).show();
                    submitBtn.setEnabled(true);
                }
                else if(Integer.parseInt(cbu.getText().toString()) < 500){
                    Toast.makeText(JoinMembership.this, "Amount should be atleast 500 or higher", Toast.LENGTH_SHORT).show();
                    submitBtn.setEnabled(true);
                }
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("user_id", user.getUid());
                        map.put("cbu", cbu.getText().toString());
                        map.put("status", "pending");
                        map.put("date_created", dateToday);
                        map.put("date_updated", dateToday);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("membership_requests").push().setValue(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        finish();
                                        Toast.makeText(JoinMembership.this, "Membership Request Submitted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}