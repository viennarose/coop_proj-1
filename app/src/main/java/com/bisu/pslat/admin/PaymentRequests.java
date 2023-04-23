package com.bisu.pslat.admin;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentRequests extends AppCompatActivity {

    ListView simpleList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_requests);
        simpleList = (ListView) findViewById(R.id.paymentListView);
        Button back = (Button) findViewById(R.id.backButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("payment_requests").orderByChild("status").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            ArrayList<String> userList = new ArrayList<String>();
                            final String[] full_name = {""};
                            final String[] username = { "" };
                            for (DataSnapshot child : snapshot.getChildren()) {
                                String user_id = child.child("user_id").getValue().toString();
                                mDatabase.child("users").child(user_id).get()
                                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task2) {
                                                if (!task2.isSuccessful()) {
                                                    Log.e("firebase", "Error getting data", task2.getException());
                                                }
                                                else {
                                                    full_name[0] = task2.getResult().child("fullname").getValue().toString();
                                                    username[0] = task2.getResult().child("username").getValue().toString();

                                                    userList.add("Name: " + AccountSettings.decode(full_name[0]) +System.getProperty("line.separator") +"Username: "+ AccountSettings.decode(username[0]));
                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PaymentRequests.this, R.layout.activity_listview, R.id.textView, userList);
                                                    simpleList.setAdapter(arrayAdapter);
                                                }
                                            }
                                        });
                            }


                            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Log.d("User", (String) adapterView.getItemAtPosition(i));
                                    Intent go = new Intent(PaymentRequests.this,PaymentRequestInformation.class);
                                    go.putExtra("username",adapterView.getItemAtPosition(i).toString().split("Username: ")[1]);
                                    startActivity(go);
                                    finish();
                                }
                            });
                        }
                        else {
                            Toast.makeText(PaymentRequests.this, "No pending requests", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentRequests.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

