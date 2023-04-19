package com.bisu.pslat.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.BuildConfig;
import com.bisu.pslat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class OverallTotals extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private double totalcbu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_totals);
        Button back = (Button) findViewById(R.id.backButton);
        Button totalBtn = (Button) findViewById(R.id.total);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").orderByChild("type").equalTo("member")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot task) {
                        if (!task.exists()) {
                            totalBtn.setText("No members yet");
                        } else {
                            for (DataSnapshot child : task.getChildren()) {
                                mDatabase.child("balance").orderByChild("user_id").equalTo(child.getKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot child2 : snapshot.getChildren()) {
                                                    if (child2.child("type").getValue().toString().matches("CBU")) {
                                                        totalcbu += Double.parseDouble(child2.child("amount").getValue().toString());
                                                    }
                                                }
                                                totalBtn.setText("Total Capital: P" + totalcbu);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverallTotals.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}