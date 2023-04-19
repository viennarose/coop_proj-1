package com.bisu.pslat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        EditText fullName = (EditText) findViewById(R.id.fullName);
        EditText address = (EditText) findViewById(R.id.address);
        EditText age = (EditText) findViewById(R.id.age);
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        EditText email = (EditText) findViewById(R.id.email);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        TextView loginButton = (TextView) findViewById(R.id.loginButton);
        Button createAcc = (Button) findViewById(R.id.createButton);
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

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fullName.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input full name", Toast.LENGTH_SHORT).show();
                }
                else if(address.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input address", Toast.LENGTH_SHORT).show();
                }
                else if(age.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input age", Toast.LENGTH_SHORT).show();
                }
                else if(age.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input full name", Toast.LENGTH_SHORT).show();
                }
                else if(phoneNumber.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input phone number", Toast.LENGTH_SHORT).show();
                }
                else if(!phoneNumber.getText().toString().substring(0, 2).matches("09")) {
                    Toast.makeText(Registration.this, "Phone number is invalid", Toast.LENGTH_SHORT).show();
                }
                else if(phoneNumber.getText().toString().length() != 11){
                    Toast.makeText(Registration.this, "Phone number is too short", Toast.LENGTH_SHORT).show();
                }
                else if(email.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input email address", Toast.LENGTH_SHORT).show();
                }
                else if(!isValidEmail(email.getText().toString())){
                    Toast.makeText(Registration.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
                else if(username.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input username", Toast.LENGTH_SHORT).show();
                }
                else if(username.getText().toString().length() <= 3) {
                    Toast.makeText(Registration.this, "Username too short", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().matches("")) {
                    Toast.makeText(Registration.this, "Input password", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().length() <= 7) {
                    Toast.makeText(Registration.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                else if(!isValidPassword(password.getText().toString().trim())) {
                    Toast.makeText(Registration.this, "Password must contain letters and number", Toast.LENGTH_SHORT).show();
                }
                else {
                    String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("fullname", AccountSettings.encode(fullName.getText().toString()));
                    map.put("address", AccountSettings.encode(address.getText().toString()));
                    map.put("age", AccountSettings.encode(age.getText().toString()));
                    map.put("phoneNumber", AccountSettings.encode(phoneNumber.getText().toString()));
                    map.put("email", email.getText().toString());
                    map.put("username", AccountSettings.encode(username.getText().toString()));
                    map.put("password", AccountSettings.encode(password.getText().toString()));
                    map.put("type", "not_member");
                    map.put("date_created", dateToday);
                    map.put("date_updated", dateToday);

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").orderByChild("username").equalTo(AccountSettings.encode(username.getText().toString()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        //exists in Database
                                        Toast.makeText(Registration.this, "Username already used", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        mDatabase.child("users").child(authResult.getUser().getUid()).setValue(map)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        fullName.setText("");
                                                                        address.setText("");
                                                                        age.setText("");
                                                                        phoneNumber.setText("");
                                                                        email.setText("");
                                                                        username.setText("");
                                                                        password.setText("");

                                                                        Toast.makeText(Registration.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Registration.this, "Email already exist", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Registration.this, Login.class);

                // on below line we are
                // starting a new activity.
                startActivity(i);

                // on the below line we are finishing
                // our current activity.
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent intent = new Intent(Registration.this, UserDashboard.class);
            startActivity(intent);
            finish();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=\\S+$)";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.find();

    }
}