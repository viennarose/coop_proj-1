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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        TextView createAcc = (TextView) findViewById(R.id.createAcc);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        ImageView adminBtn = (ImageView) findViewById(R.id.adminButton);
        Switch simpleSwitch = (Switch) findViewById(R.id.passwordSwitch);

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, AdminLogin.class);

                // on below line we are
                // starting a new activity.
                startActivity(i);

                // on the below line we are finishing
                // our current activity.
                finish();
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

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Registration.class);

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
                    Toast.makeText(Login.this, "Input username", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().matches("")){
                    Toast.makeText(Login.this, "Input password", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").orderByChild("username").equalTo(AccountSettings.encode(username.getText().toString()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        String user_info = dataSnapshot.getValue().toString();
                                        String removebracket = user_info.substring(1,user_info.length()-1);
                                        String[] extract_id = removebracket.split(",");
                                        String entered_email = extract_id[8].split("=")[1];
                                        String entered_password = password.getText().toString();
                                        Log.d("KINI: ", removebracket);
                                        mAuth.signInWithEmailAndPassword(entered_email, entered_password)
                                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            Toast.makeText(Login.this, "Logged-in Successfully", Toast.LENGTH_SHORT).show();

                                                            Intent i = new Intent(Login.this, UserDashboard.class);

                                                            // on below line we are
                                                            // starting a new activity.
                                                            startActivity(i);

                                                            // on the below line we are finishing
                                                            // our current activity.
                                                            finish();
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Toast.makeText(Login.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(Login.this, "Account doesn't exists", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent intent = new Intent(Login.this, UserDashboard.class);
            startActivity(intent);
            finish();
        }
    }
}