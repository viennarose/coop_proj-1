package com.bisu.pslat.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.Login;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class MembershipRequestInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_request_information);
        EditText fullName = (EditText) findViewById(R.id.fullName);
        EditText cbuVal = (EditText) findViewById(R.id.cbu);
        EditText datereq = (EditText) findViewById(R.id.reqDate);
        Button back = (Button) findViewById(R.id.backButton);
        Button approveBtn = (Button) findViewById(R.id.approveButton);
        Button rejectBtn = (Button) findViewById(R.id.rejectButton);
        ProgressBar pbar = (ProgressBar) findViewById(R.id.progressBar2);
        TextView barT = (TextView) findViewById(R.id.progressText);

        Intent intent = getIntent();
        String passed_username = intent.getExtras().getString("username");
        final String[] m_id = {""};
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").orderByChild("username").equalTo(AccountSettings.encode(passed_username))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        Log.d("TEEST",child.getKey());

                                        String user_id = child.getKey();
                                        mDatabase.child("membership_requests").orderByChild("user_id").equalTo(user_id)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                        for (DataSnapshot child2 : snapshot2.getChildren()) {
                                                            m_id[0] = child2.getKey();
                                                            fullName.setText("Name: " + AccountSettings.decode(child.child("fullname").getValue().toString()));
                                                            cbuVal.setText("CBU: " + child2.child("cbu").getValue().toString());
                                                            datereq.setText("Date Req: " + child2.child("date_created").getValue().toString());

                                                            approveBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    pbar.setVisibility(View.VISIBLE);
                                                                    pbar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                                                                    ObjectAnimator.ofInt(pbar, "progress", 10000)
                                                                            .setDuration(1000)
                                                                            .start();
                                                                    barT.setText("Approving membership request...");

                                                                    String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                                    mDatabase.child("membership_requests").child(m_id[0]).child("date_updated").setValue(dateToday);
                                                                    mDatabase.child("membership_requests").child(m_id[0]).child("status").setValue("approved");

                                                                    barT.setText("Updating user table...");

                                                                    mDatabase.child("users").child(user_id).child("type").setValue("member");
                                                                    mDatabase.child("users").child(user_id).child("date_updated").setValue(dateToday);

                                                                    HashMap<String, Object> map = new HashMap<>();
                                                                    map.put("user_id", user_id);
                                                                    map.put("amount", child2.child("cbu").getValue().toString());
                                                                    map.put("type", "CBU");
                                                                    map.put("date_created", dateToday);

                                                                    HashMap<String, Object> map2 = new HashMap<>();
                                                                    map2.put("amount", child2.child("cbu").getValue().toString());
                                                                    map2.put("type", "CBU");
                                                                    map2.put("date_created", dateToday);

                                                                    barT.setText("Setting up user balance...");

                                                                    mDatabase.child("payments").child(user_id).push().setValue(map2);
                                                                    mDatabase.child("balance").push().setValue(map)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Intent intent = new Intent(MembershipRequestInformation.this, MembershipRequests.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                }
                                                                            });
                                                                }
                                                            });

                                                            rejectBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                                    mDatabase.child("membership_requests").child(m_id[0]).child("date_updated").setValue(dateToday);
                                                                    mDatabase.child("membership_requests").child(m_id[0]).child("status").setValue("rejected")
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(MembershipRequestInformation.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(MembershipRequestInformation.this, MembershipRequests.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                }
                                else {
                                    Toast.makeText(MembershipRequestInformation.this, "User doesnt exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MembershipRequestInformation.this, MembershipRequests.class);
                startActivity(intent);
                finish();
            }
        });

    }
}