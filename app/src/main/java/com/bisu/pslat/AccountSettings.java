package com.bisu.pslat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        final String[] dateCreated = {""},member_type = {""};
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        EditText fullName = (EditText) findViewById(R.id.fullName);
        EditText address = (EditText) findViewById(R.id.address);
        EditText age = (EditText) findViewById(R.id.age);
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        EditText email = (EditText) findViewById(R.id.email);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        TextView backBtn = (TextView) findViewById(R.id.backButton);
        Button updateBtn = (Button) findViewById(R.id.updateButton);
        Switch simpleSwitch = (Switch) findViewById(R.id.passwordSwitch);

        final String[] old_password = {""};

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updateBtn.getText().toString().matches("SAVE")){
                    if(fullName.getText().toString().matches("")) {
                        Toast.makeText(AccountSettings.this, "Input full name", Toast.LENGTH_SHORT).show();
                    }
                    else if(address.getText().toString().matches("")) {
                        Toast.makeText(AccountSettings.this, "Input address", Toast.LENGTH_SHORT).show();
                    }
                    else if(age.getText().toString().matches("")) {
                        Toast.makeText(AccountSettings.this, "Input age", Toast.LENGTH_SHORT).show();
                    }
                    else if(age.getText().toString().matches("")) {
                        Toast.makeText(AccountSettings.this, "Input full name", Toast.LENGTH_SHORT).show();
                    }
                    else if(phoneNumber.getText().toString().matches("")) {
                        Toast.makeText(AccountSettings.this, "Input phone number", Toast.LENGTH_SHORT).show();
                    }
                    else if(!phoneNumber.getText().toString().substring(0, 2).matches("09")) {
                        Toast.makeText(AccountSettings.this, "Phone number is invalid", Toast.LENGTH_SHORT).show();
                    }
                    else if(phoneNumber.getText().toString().length() != 11){
                        Toast.makeText(AccountSettings.this, "Phone number is too short", Toast.LENGTH_SHORT).show();
                    }
                    else if(email.getText().toString().matches("")) {
                        Toast.makeText(AccountSettings.this, "Input email address", Toast.LENGTH_SHORT).show();
                    }
                    else if(!isValidEmail(email.getText().toString())){
                        Toast.makeText(AccountSettings.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                    else if(password.getText().toString().matches("")) {
                        Toast.makeText(AccountSettings.this, "Input password", Toast.LENGTH_SHORT).show();
                    }
                    else if(password.getText().toString().length() <= 7) {
                        Toast.makeText(AccountSettings.this, "Password too short", Toast.LENGTH_SHORT).show();
                    }
                    else if(!isValidPassword(password.getText().toString().trim())) {
                        Toast.makeText(AccountSettings.this, "Password must contain letters and number", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        fullName.setEnabled(false);
                        address.setEnabled(false);
                        age.setEnabled(false);
                        phoneNumber.setEnabled(false);
                        username.setEnabled(false);
                        password.setEnabled(false);

                        updateBtn.setEnabled(false);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user != null){
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), old_password[0]);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            user.updatePassword(password.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                String dateToday = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                                                                mDatabase.child("users").child(user.getUid()).child("fullname").setValue(encode(fullName.getText().toString()));
                                                                mDatabase.child("users").child(user.getUid()).child("address").setValue(encode(address.getText().toString()));
                                                                mDatabase.child("users").child(user.getUid()).child("age").setValue(encode(age.getText().toString()));
                                                                mDatabase.child("users").child(user.getUid()).child("phoneNumber").setValue(encode(phoneNumber.getText().toString()));
                                                                mDatabase.child("users").child(user.getUid()).child("password").setValue(encode(password.getText().toString()));
                                                                mDatabase.child("users").child(user.getUid()).child("date_updated").setValue(dateToday)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(AccountSettings.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                updateBtn.setText("UPDATE ACCOUNT");
                                                                                updateBtn.setBackgroundColor(Color.parseColor("#0062ff"));
                                                                            }
                                                                        });
                                                            }
                                                            else {
                                                                Log.d("NGANO: ", task.getException().toString());
                                                                Toast.makeText(AccountSettings.this, "Update failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                        else {
                            Intent intent = new Intent(AccountSettings.this, Login.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(AccountSettings.this, "Session Expired", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    fullName.setEnabled(true);
                    address.setEnabled(true);
                    age.setEnabled(true);
                    phoneNumber.setEnabled(true);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String[] usernameVal = {""},passwordVal = {""},fullnameval = {""},addressVal = {""},ageVal = {""},phoneVal = {""};
            DatabaseReference mDatabaseref = FirebaseDatabase.getInstance().getReference();
            mDatabaseref.child("users").orderByChild("email").equalTo(user.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String user_info = snapshot.getValue().toString();
                            String removebracket = user_info.substring(1,user_info.length()-1);
                            String[] extract_id = removebracket.split(",");
                            dateCreated[0] = extract_id[4].split("=")[1];
                            fullnameval[0] = originalValue(extract_id[5].split("="));
                            member_type[0] = extract_id[6].split("=")[1];
                            addressVal[0] = originalValue(extract_id[1].split("="));
                            ageVal[0] = originalValue(extract_id[7].split("="));
                            phoneVal[0] = originalValue(extract_id[3].split("="));
                            usernameVal[0] = originalValue(extract_id[9].split("="));
                            passwordVal[0] = password_originalValue(extract_id[0].split("="));
                            Log.d("KINI: ", removebracket);

                            old_password[0] = decode(passwordVal[0]);
                            fullName.setText(decode(fullnameval[0]));
                            address.setText(decode(addressVal[0]));
                            age.setText(decode(ageVal[0]));
                            phoneNumber.setText(decode(phoneVal[0]));
                            email.setText(user.getEmail());
                            username.setText(decode(usernameVal[0].substring(0,usernameVal[0].length()-1)));
                            password.setText(decode(passwordVal[0]));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=\\S+$)";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.find();

    }

    public static String encode(String text){

        byte[] data = text.getBytes(StandardCharsets.ISO_8859_1);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String decode(String text){
        byte[] data = Base64.decode(text, Base64.DEFAULT);
        return new String(data, StandardCharsets.ISO_8859_1);
    }

    public static String originalValue (String[] text){
        return TextUtils.join("",Arrays.copyOfRange(text, 1, text.length));
    }

    public static String password_originalValue (String[] text){
        String[] p = TextUtils.join("=",Arrays.copyOfRange(text, 1, text.length)).split("=");
        return TextUtils.join("",Arrays.copyOfRange(p, 1, p.length));
    }
}