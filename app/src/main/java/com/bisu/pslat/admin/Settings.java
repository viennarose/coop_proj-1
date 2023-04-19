package com.bisu.pslat.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.Login;
import com.bisu.pslat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        TextView backBtn = (TextView) findViewById(R.id.backButton);
        Button updateBtn = (Button) findViewById(R.id.updateButton);
        Switch simpleSwitch = (Switch) findViewById(R.id.passwordSwitch);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updateBtn.getText().toString().matches("SAVE")){
                    if(username.getText().toString().matches("")) {
                        Toast.makeText(Settings.this, "Input username", Toast.LENGTH_SHORT).show();
                    }
                    else if(username.getText().toString().length() <= 3) {
                        Toast.makeText(Settings.this, "Username too short", Toast.LENGTH_SHORT).show();
                    }
                    else if(password.getText().toString().matches("")) {
                        Toast.makeText(Settings.this, "Input password", Toast.LENGTH_SHORT).show();
                    }
                    else if(password.getText().toString().length() <= 7) {
                        Toast.makeText(Settings.this, "Password too short", Toast.LENGTH_SHORT).show();
                    }
                    else if(!AccountSettings.isValidPassword(password.getText().toString().trim())) {
                        Toast.makeText(Settings.this, "Password must contain letters and number", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        username.setEnabled(false);
                        password.setEnabled(false);
                        updateBtn.setEnabled(false);

                        String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                        mDatabase.child("admin").child("account_1").child("username").setValue(AccountSettings.encode(username.getText().toString()));
                        mDatabase.child("admin").child("account_1").child("password").setValue(AccountSettings.encode(password.getText().toString()));
                        mDatabase.child("admin").child("account_1").child("date_updated").setValue(dateToday)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(Settings.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                updateBtn.setText("UPDATE ACCOUNT");
                                                updateBtn.setBackgroundColor(Color.parseColor("#0062ff"));
                                            }
                                        });
                    }
                }
                else {
                    username.setEnabled(true);
                    password.setEnabled(true);

                    updateBtn.setText("SAVE");
                    updateBtn.setBackgroundColor(Color.parseColor("#009d5b"));
                }
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(null);
                }
                else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }

                password.setSelection(password.getText().length());
            }
        });

        mDatabase.child("admin").child("account_1").get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        username.setText(AccountSettings.decode(dataSnapshot.child("username").getValue().toString()));
                        password.setText(AccountSettings.decode(dataSnapshot.child("password").getValue().toString()));
                    }
                });
    }
}