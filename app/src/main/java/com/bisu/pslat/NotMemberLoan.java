package com.bisu.pslat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NotMemberLoan extends AppCompatActivity {
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_member_loan);
        EditText loan = (EditText) findViewById(R.id.loanAmount);
        EditText numMos = (EditText) findViewById(R.id.numberMonths);
        Button submitBtn = (Button) findViewById(R.id.submitB);
        Button cancelBtn = (Button) findViewById(R.id.cancelB);
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.dynamic_spinner);
        ArrayList<String> items = new ArrayList<String>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").orderByChild("type").equalTo("member")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child : snapshot.getChildren()) {
                                    String full_name = AccountSettings.decode(child.child("fullname").getValue().toString());
                                    items.add(full_name+" @"+AccountSettings.decode(child.child("username").getValue().toString()));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(NotMemberLoan.this,
                                        android.R.layout.simple_spinner_dropdown_item, items);
                                dynamicSpinner.setAdapter(adapter);
                                dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int position, long id) {
                                        Log.d("item", (String) parent.getItemAtPosition(position));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // TODO Auto-generated method stub
                                    }
                                });
                                submitBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(items.isEmpty()){
                                            Toast.makeText(NotMemberLoan.this, "No guarantor available yet", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(loan.getText().toString().matches("")){
                                            Toast.makeText(NotMemberLoan.this, "Enter loan amount", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(Integer.parseInt(loan.getText().toString()) < 1000){
                                            Toast.makeText(NotMemberLoan.this, "Amount should be atleast 1000 or higher", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(numMos.getText().toString().matches("")){
                                            Toast.makeText(NotMemberLoan.this, "Enter number of months", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(Integer.parseInt(numMos.getText().toString()) > 12 || Integer.parseInt(numMos.getText().toString()) < 1){
                                            Toast.makeText(NotMemberLoan.this, "Months should be 1 to 12 only", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            double interest = 0.00;
                                            if(Integer.parseInt(numMos.getText().toString()) < 12){
                                                interest = ((Integer.parseInt(loan.getText().toString())*0.13)/12)*(Integer.parseInt(numMos.getText().toString())+1);
                                            }
                                            else {
                                                interest = Integer.parseInt(loan.getText().toString())*0.13;
                                            }
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if(user != null){
                                                String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("user_id", user.getUid());
                                                map.put("guarantor_username", AccountSettings.encode(dynamicSpinner.getSelectedItem().toString().split("@")[1]));
                                                map.put("amount", loan.getText().toString());
                                                map.put("months", numMos.getText().toString());
                                                map.put("interest", String.format("%.2f", interest));
                                                map.put("status", "pending");
                                                map.put("date_created", dateToday);
                                                map.put("date_updated", dateToday);
                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                mDatabase.child("guarantor_requests").push().setValue(map)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                                finish();
                                                                Toast.makeText(NotMemberLoan.this, "Loan Request Submitted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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