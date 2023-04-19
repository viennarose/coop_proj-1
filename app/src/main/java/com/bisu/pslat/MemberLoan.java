package com.bisu.pslat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class MemberLoan extends AppCompatActivity {
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_loan);
        EditText loan = (EditText) findViewById(R.id.loanAmount);
        EditText numMos = (EditText) findViewById(R.id.numberMonths);
        Button submitBtn = (Button) findViewById(R.id.submitB);
        Button cancelBtn = (Button) findViewById(R.id.cancelB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loan.getText().toString().matches("")){
                    Toast.makeText(MemberLoan.this, "Enter loan amount", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(loan.getText().toString()) < 1000){
                    Toast.makeText(MemberLoan.this, "Amount should be atleast 1000 or higher", Toast.LENGTH_SHORT).show();
                }
                else if(numMos.getText().toString().matches("")){
                    Toast.makeText(MemberLoan.this, "Enter number of months", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(numMos.getText().toString()) > 12 || Integer.parseInt(numMos.getText().toString()) < 1){
                    Toast.makeText(MemberLoan.this, "Months should be 1 to 12 only", Toast.LENGTH_SHORT).show();
                }
                else {
                    double interest = 0.00;
                    if(Integer.parseInt(numMos.getText().toString()) < 12){
                        interest = ((Integer.parseInt(loan.getText().toString())*0.13)/12)*(Integer.parseInt(numMos.getText().toString())+1);
                    }
                    else {
                        interest = Double.parseDouble(loan.getText().toString())*0.13;
                    }
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("user_id", user.getUid());
                        map.put("user_type", "member");
                        map.put("amount", loan.getText().toString());
                        map.put("months", numMos.getText().toString());
                        map.put("interest", String.format("%.2f", interest));
                        map.put("status", "pending");
                        map.put("date_created", dateToday);
                        map.put("date_updated", dateToday);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("loan_requests").push().setValue(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        finish();
                                        Toast.makeText(MemberLoan.this, "Loan Request Submitted", Toast.LENGTH_SHORT).show();
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