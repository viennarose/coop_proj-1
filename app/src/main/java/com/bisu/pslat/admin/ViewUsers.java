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
import com.bisu.pslat.Login;
import com.bisu.pslat.NotMemberLoan;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewUsers extends AppCompatActivity {
    ListView simpleList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        simpleList = (ListView) findViewById(R.id.usersListView);
        Button back = (Button) findViewById(R.id.backButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    if (task.getResult().exists()) {
                        String[] userList = new String[Integer.parseInt(String.valueOf(task.getResult().getChildrenCount()))];
                        Integer ind = 0;

                        for (DataSnapshot child : task.getResult().getChildren()) {

                            String full_name = AccountSettings.decode(child.child("fullname").getValue().toString());
                            String username = AccountSettings.decode(child.child("username").getValue().toString());
                            String address = AccountSettings.decode(child.child("address").getValue().toString());
                            String age = AccountSettings.decode(child.child("age").getValue().toString());
                            String phoneNumber = AccountSettings.decode(child.child("phoneNumber").getValue().toString());

                            String userString = "Name: " + full_name + "\nUsername: " + username + "\nAddress: " + address + "\nAge: " + age + "\nPhone Number: " + phoneNumber;

                            userList[ind] = userString;
                            ind++;

                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewUsers.this, R.layout.activity_listview, R.id.textView, userList);
                        simpleList.setAdapter(arrayAdapter);
                        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                Log.d("User", (String) adapterView.getItemAtPosition(i));
//                                Intent go = new Intent(ViewUsers.this, UserInformations.class);
//                                go.putExtra("username", adapterView.getItemAtPosition(i).toString().split("@")[1]);
//                                startActivity(go);
                            }
                        });
                    } else {
                        Toast.makeText(ViewUsers.this, "No users yet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewUsers.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}