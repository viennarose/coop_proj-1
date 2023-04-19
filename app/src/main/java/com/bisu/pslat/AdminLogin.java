package com.bisu.pslat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bisu.pslat.admin.AdminMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        mAuth = FirebaseAuth.getInstance();

        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        TextView userBtn = (TextView) findViewById(R.id.userLogin);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Switch simpleSwitch = (Switch) findViewById(R.id.passwordSwitch);

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

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminLogin.this, Login.class);

                // on below line we are
                // starting a new activity.
                startActivity(i);

                // on the below line we are finishing
                // our current activity.
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().matches("")){
                    Toast.makeText(AdminLogin.this, "Input username", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().matches("")){
                    Toast.makeText(AdminLogin.this, "Input password", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("admin").orderByChild("username").equalTo(AccountSettings.encode(username.getText().toString()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for (DataSnapshot child: dataSnapshot.getChildren()){
                                            if(AccountSettings.decode(child.child("password").getValue().toString()).matches(password.getText().toString())){
                                                Intent i = new Intent(AdminLogin.this, AdminMainActivity.class);

                                                // on below line we are
                                                // starting a new activity.
                                                startActivity(i);

                                                // on the below line we are finishing
                                                // our current activity.
                                                finish();
                                                Toast.makeText(AdminLogin.this, "Logged-in Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(AdminLogin.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(AdminLogin.this, "Account doesn't exists", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });

    }
}