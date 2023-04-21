package com.bisu.pslat.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoanRequests extends AppCompatActivity {
    ListView simpleList;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_requests);
        simpleList = (ListView)findViewById(R.id.loanListView);
        Button back = (Button) findViewById(R.id.backButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("loan_requests").orderByChild("status").equalTo("pending")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot task) {
                                if (task.exists()){
                                    ArrayList<String> userList = new ArrayList<String>();

                                    Log.d("count", String.valueOf(task.getChildrenCount()));
                                    for (DataSnapshot child : task.getChildren()) {
                                        String[] full_name = {""};
                                        String[] username = {""};
                                        String user_id = child.child("user_id").getValue().toString();
                                        mDatabase.child("users").child(user_id).get()
                                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task2) {
                                                        full_name[0] = AccountSettings.decode(task2.getResult().child("fullname").getValue().toString());
                                                        username[0] = AccountSettings.decode(task2.getResult().child("username").getValue().toString());
                                                        userList.add("Name: " + full_name[0] +" @"+username[0]
                                                                +System.getProperty("line.separator")
                                                                +"Date Requested: "+child.child("date_created").getValue().toString());
                                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LoanRequests.this, R.layout.activity_listview, R.id.textView, userList);
                                                        simpleList.setAdapter(arrayAdapter);
                                                    }
                                                });
                                    }

                                    simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            Log.d("User", (String) adapterView.getItemAtPosition(i));
                                            Intent go = new Intent(LoanRequests.this,LoanRequestInformation.class);
                                            go.putExtra("username",adapterView.getItemAtPosition(i).toString().split("@")[1]
                                                    .split("Date Requested: ")[0].trim());
                                            go.putExtra("date_created",adapterView.getItemAtPosition(i).toString().split("@")[1]
                                                    .split("Date Requested: ")[1]);
                                            startActivity(go);
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(LoanRequests.this, "No loan requests", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoanRequests.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}