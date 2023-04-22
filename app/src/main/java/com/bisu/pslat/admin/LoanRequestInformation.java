package com.bisu.pslat.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.GuarantorRequest;
import com.bisu.pslat.GuarantorRequestInformation;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class LoanRequestInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_request_information);
        EditText fullName = (EditText) findViewById(R.id.fullName);
        EditText gname = (EditText) findViewById(R.id.guarantorName);
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
                                mDatabase.child("loan_requests").orderByChild("user_id").equalTo(user_id)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                String userloan_type= "";
                                                for (DataSnapshot child2 : snapshot2.getChildren()) {
                                                    if(child2.child("date_created").getValue().toString().matches(passed_dc)){
                                                        m_id[0] = child2.getKey();
                                                        userloan_type = child2.child("user_type").getValue().toString();
                                                        fullName.setText(AccountSettings.decode(child.child("fullname").getValue().toString()));
                                                        if(child2.child("guarantor_username").getValue() == null){
                                                            gname.setText("Membership Plan");
                                                        }
                                                        else {
                                                            gname.setText(AccountSettings.decode(AccountSettings.decode(child2.child("guarantor_username").getValue().toString())));
                                                        }
                                                        loanVal.setText(child2.child("amount").getValue().toString());
                                                        monthsVal.setText(child2.child("months").getValue().toString() + " months");
                                                        datereq.setText(child2.child("date_created").getValue().toString());

                                                        String finalUserloan_type = userloan_type;
                                                        approveBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                pbar.setVisibility(View.VISIBLE);
                                                                pbar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                                                                ObjectAnimator.ofInt(pbar, "progress", 10000)
                                                                        .setDuration(1000)
                                                                        .start();
                                                                barT.setText("Approving loan request...");

                                                                String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                                mDatabase.child("loan_requests").child(m_id[0]).child("date_updated").setValue(dateToday);
                                                                mDatabase.child("loan_requests").child(m_id[0]).child("status").setValue("approved");

                                                                barT.setText("Submitting loan request...");

                                                                double patronage_refund = Double.parseDouble(child2.child("interest").getValue().toString())*0.1;
                                                                double service_charge = Double.parseDouble(loanVal.getText().toString())*0.02;
                                                                double sur_charge = ((Integer.parseInt(child2.child("amount").getValue().toString())
                                                                        +Double.parseDouble(child2.child("interest").getValue().toString()))
                                                                        /(Integer.parseInt(child2.child("months").getValue().toString())))*0.05;

                                                                HashMap<String, Object> bal_map = new HashMap<>();
                                                                if(child2.child("guarantor_username").getValue() != null){
                                                                    bal_map.put("user_id", child2.child("guarantor_id").getValue().toString());
                                                                    bal_map.put("amount", String.format("%.2f", patronage_refund));
                                                                    bal_map.put("type", "patronage_refund");
                                                                    bal_map.put("date_created", dateToday);

                                                                    mDatabase.child("balance").push().setValue(bal_map);
                                                                }

                                                                HashMap<String, Object> map = new HashMap<>();
                                                                if(finalUserloan_type.matches("not_member")){
                                                                    map.put("guarantor_id", child2.child("guarantor_id").getValue().toString());
                                                                    map.put("guarantor_username", child2.child("guarantor_username").getValue().toString());
                                                                }
                                                                map.put("user_id", user_id);
                                                                map.put("user_type", finalUserloan_type);
                                                                map.put("amount", loanVal.getText().toString());
                                                                map.put("months", child2.child("months").getValue().toString());
                                                                map.put("interest", child2.child("interest").getValue().toString());
                                                                map.put("service_charge", service_charge);
                                                                map.put("sur_charge", String.format("%.2f", sur_charge));
                                                                map.put("date_created", dateToday);
                                                                map.put("date_updated", dateToday);

                                                                mDatabase.child("loans").push().setValue(map)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(LoanRequestInformation.this, "Loan Request Approved", Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(LoanRequestInformation.this, LoanRequests.class);
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
                                                                mDatabase.child("loan_requests").child(m_id[0]).child("date_updated").setValue(dateToday);
                                                                mDatabase.child("loan_requests").child(m_id[0]).child("status").setValue("rejected")
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(LoanRequestInformation.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(LoanRequestInformation.this, LoanRequests.class);
                                                                                startActivity(intent);
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
                            Toast.makeText(LoanRequestInformation.this, "User doesnt exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoanRequestInformation.this, LoanRequests.class);
                startActivity(intent);
                finish();
            }
        });
    }
}