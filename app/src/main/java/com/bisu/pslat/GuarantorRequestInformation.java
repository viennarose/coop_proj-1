package com.bisu.pslat;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bisu.pslat.admin.MembershipRequestInformation;
import com.bisu.pslat.admin.MembershipRequests;
import com.bisu.pslat.user.ui.notifications.NotificationsFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class GuarantorRequestInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarantor_request_information);
        EditText fullName = (EditText) findViewById(R.id.fullName);
        EditText loanVal = (EditText) findViewById(R.id.loanAmount);
        EditText monthsVal = (EditText) findViewById(R.id.numberMonths);
        EditText datereq = (EditText) findViewById(R.id.reqDate);
        Button back = (Button) findViewById(R.id.backButton);
        Button approveBtn = (Button) findViewById(R.id.approveButton);
        Button rejectBtn = (Button) findViewById(R.id.rejectButton);
        ProgressBar pbar = (ProgressBar) findViewById(R.id.progressBar2);
        TextView barT = (TextView) findViewById(R.id.progressText);

        Intent intent = getIntent();
        String passed_username = intent.getExtras().getString("username");
        String passed_dc = intent.getExtras().getString("date_created");

        final String[] m_id = {""};
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").orderByChild("username").equalTo(AccountSettings.encode(passed_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot child : snapshot.getChildren()) {

                                String user_id = child.getKey();
                                mDatabase.child("guarantor_requests").orderByChild("user_id").equalTo(user_id)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                for (DataSnapshot child2 : snapshot2.getChildren()) {
                                                    if(child2.child("date_created").getValue().toString().matches(passed_dc)){
                                                        m_id[0] = child2.getKey();
                                                        fullName.setText("Name: " + AccountSettings.decode(child.child("fullname").getValue().toString()));
                                                        loanVal.setText("Loan Amount: " + child2.child("amount").getValue().toString());
                                                        monthsVal.setText("Months to pay: " +child2.child("months").getValue().toString() + " month/s");
                                                        datereq.setText("Date Req: " + child2.child("date_created").getValue().toString());

                                                        approveBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                pbar.setVisibility(View.VISIBLE);
                                                                pbar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                                                                ObjectAnimator.ofInt(pbar, "progress", 10000)
                                                                        .setDuration(1000)
                                                                        .start();
                                                                barT.setText("Approving guarantor request...");

                                                                String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                                mDatabase.child("guarantor_requests").child(m_id[0]).child("date_updated").setValue(dateToday);
                                                                mDatabase.child("guarantor_requests").child(m_id[0]).child("status").setValue("approved");

                                                                barT.setText("Submitting loan request...");
                                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                HashMap<String, Object> map = new HashMap<>();
                                                                map.put("user_id", user_id);
                                                                map.put("guarantor_id", user.getUid());
                                                                map.put("guarantor_username", UserDashboard.fullname[0]);
                                                                map.put("user_type", "not_member");
                                                                map.put("amount", loanVal.getText().toString());
                                                                map.put("months", child2.child("months").getValue().toString());
                                                                map.put("interest", child2.child("interest").getValue().toString());
                                                                map.put("status", "pending");
                                                                map.put("date_created", dateToday);
                                                                map.put("date_updated", dateToday);
                                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                                mDatabase.child("loan_requests").push().setValue(map)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                NotificationsFragment.loadList();
                                                                                Toast.makeText(GuarantorRequestInformation.this, "Loan Request Submitted", Toast.LENGTH_SHORT).show();
                                                                                finish();
                                                                            }
                                                                        });
                                                            }
                                                        });

                                                        rejectBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                                mDatabase.child("guarantor_requests").child(m_id[0]).child("date_updated").setValue(dateToday);
                                                                mDatabase.child("guarantor_requests").child(m_id[0]).child("status").setValue("rejected")
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                NotificationsFragment.loadList();
                                                                                Toast.makeText(GuarantorRequestInformation.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                                                                                finish();
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                        else {
                            Toast.makeText(GuarantorRequestInformation.this, "User doesnt exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}