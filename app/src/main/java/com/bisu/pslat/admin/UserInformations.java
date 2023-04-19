package com.bisu.pslat.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.Login;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInformations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_informations);

        EditText fullName = (EditText) findViewById(R.id.fullName);
        EditText address = (EditText) findViewById(R.id.address);
        EditText age = (EditText) findViewById(R.id.age);
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        EditText email = (EditText) findViewById(R.id.email);
        EditText username = (EditText) findViewById(R.id.username);
        TextView ms = (TextView) findViewById(R.id.accStatus);
        Button back = (Button) findViewById(R.id.backButton);

        Intent intent = getIntent();
        String passed_username = intent.getExtras().getString("username");
        final String[] usernameVal = {""},emailVal = {""},member_type = {""},fullnameval = {""},addressVal = {""},ageVal = {""},phoneVal = {""};

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").orderByChild("username").equalTo(AccountSettings.encode(passed_username))
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String user_info = dataSnapshot.getValue().toString();
                                String removebracket = user_info.substring(1,user_info.length()-1);
                                String[] extract_id = removebracket.split(",");
                                fullnameval[0] = AccountSettings.originalValue(extract_id[5].split("="));
                                member_type[0] = extract_id[6].split("=")[1];
                                addressVal[0] = AccountSettings.originalValue(extract_id[1].split("="));
                                ageVal[0] = AccountSettings.originalValue(extract_id[7].split("="));
                                phoneVal[0] = AccountSettings.originalValue(extract_id[3].split("="));
                                usernameVal[0] = AccountSettings.originalValue(extract_id[9].split("="));

                                fullName.setText(AccountSettings.decode(fullnameval[0]));
                                address.setText(AccountSettings.decode(addressVal[0]));
                                age.setText(AccountSettings.decode(ageVal[0]));
                                phoneNumber.setText(AccountSettings.decode(phoneVal[0]));
                                email.setText(extract_id[8].split("=")[1]);
                                username.setText(AccountSettings.decode(usernameVal[0].substring(0,usernameVal[0].length()-1)));
                                if(member_type[0].matches("not_member")){
                                    ms.setText("NOT A MEMBER");
                                    ms.setBackgroundColor(Color.parseColor("#e54949"));
                                }
                                else {
                                    ms.setText("MEMBER");
                                    ms.setBackgroundColor(Color.parseColor("#0b804f"));
                                }
                            } else {
                                Toast.makeText(UserInformations.this, "Account doesn't exists", Toast.LENGTH_SHORT).show();
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