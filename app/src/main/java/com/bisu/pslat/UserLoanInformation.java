package com.bisu.pslat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bisu.pslat.user.ui.dashboard.DashboardFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLoanInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_loan_information);
        EditText fullName = (EditText) findViewById(R.id.fullName);
        EditText gname = (EditText) findViewById(R.id.guarantorName);
        EditText loanVal = (EditText) findViewById(R.id.loanAmount);
        EditText monthsVal = (EditText) findViewById(R.id.numberMonths);
        EditText datereq = (EditText) findViewById(R.id.reqDate);
        EditText interestVal = (EditText) findViewById(R.id.interest);
        EditText serviceVal = (EditText) findViewById(R.id.serviceCharge);
        EditText surVal = (EditText) findViewById(R.id.surcharge);
        EditText mPay = (EditText) findViewById(R.id.monthly);
        Button back = (Button) findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        String passed_dc = intent.getExtras().getString("date_created");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("loans").orderByChild("user_id").equalTo(UserDashboard.user_id[0])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Log.d("DDDC",child.getKey());
                            if(child.child("date_created").getValue().toString().matches(passed_dc)){
                                double monthly_payable = 0;
                                monthly_payable = ((Integer.parseInt(child.child("amount").getValue().toString())
                                        + Double.parseDouble(  child.child("interest").getValue().toString()))
                                        / Integer.parseInt(child.child("months").getValue().toString()))
                                        + Double.parseDouble(child.child("sur_charge").getValue().toString());

                                if(child.child("guarantor_id").getValue() != null){
                                    gname.setText(AccountSettings.decode(child.child("guarantor_name").getValue().toString()));
                                }
                                else{
                                    gname.setText("Membership Plan");
                                }
                                fullName.setText(AccountSettings.decode(UserDashboard.fullname[0]));
                                loanVal.setText(child.child("amount").getValue().toString());
                                monthsVal.setText(child.child("months").getValue().toString()+ " months");
                                datereq.setText(child.child("date_created").getValue().toString());
                                interestVal.setText("Interest: P"+child.child("interest").getValue().toString());
                                serviceVal.setText("Service Charge: P"+child.child("service_charge").getValue().toString());
                                surVal.setText("Surcharge: P"+child.child("sur_charge").getValue().toString());
                                mPay.setText("Monthly Payable: "+monthly_payable);
//                                Intent go = new Intent(UserLoanInformation.this, UserMonthlyPayable.class);
//                                go.putExtra("loan_id",child.getKey());
//                                go.putExtra("monthly_payable",monthly_payable);
//                                go.putExtra("date_created",child.child("date_created").getValue().toString());
//                                go.putExtra("months",child.child("months").getValue().toString());
//                                startActivity(go);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}