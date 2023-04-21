package com.bisu.pslat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MemberPayment extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_payment);
        EditText payment = (EditText) findViewById(R.id.paymentAmount);
        Spinner month = (Spinner) findViewById(R.id.monthSpinner);
        Button submitBtn = (Button) findViewById(R.id.submitButton);
        Button cancelBtn = (Button) findViewById(R.id.cancelButton);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtn.setEnabled(false);
                if(payment.getText().toString().matches("")){
                    Toast.makeText(MemberPayment.this, "Enter amount", Toast.LENGTH_SHORT).show();
                    submitBtn.setEnabled(true);
                }
                else if(Integer.parseInt(payment.getText().toString()) < 100){
                    Toast.makeText(MemberPayment.this, "Amount should be atleast 100 or higher", Toast.LENGTH_SHORT).show();
                    submitBtn.setEnabled(true);
                }
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        String dateToday = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("user_id", user.getUid());
                        map.put("payment", payment.getText().toString());
                        String selectedMonth = month.getSelectedItem().toString();
                        map.put("month", selectedMonth);
//                        map.put("month", month.getText().toString());
                        map.put("status", "pending");
                        map.put("date_created", dateToday);
                        map.put("date_updated", dateToday);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("payment_requests").push().setValue(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        finish();
                                        Toast.makeText(MemberPayment.this, "Payment Request Submitted", Toast.LENGTH_SHORT).show();
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
