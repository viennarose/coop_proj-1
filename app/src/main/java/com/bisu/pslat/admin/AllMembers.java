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

public class AllMembers extends AppCompatActivity {
    ListView simpleList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members);

        simpleList = (ListView) findViewById(R.id.usersListView);
        Button back = (Button) findViewById(R.id.backButton);
        Button totalBtn = (Button) findViewById(R.id.total);
        Button reportBtn = (Button) findViewById(R.id.report);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager())
//            {
//                try{
//                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
//                    m.invoke(null);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(intent);
//            }
//        }
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.print);
                linearLayout.setDrawingCacheEnabled(true);
                linearLayout.buildDrawingCache(true);
                Bitmap bitmap = Bitmap.createBitmap(linearLayout.getDrawingCache());
                linearLayout.setDrawingCacheEnabled(false); // clear drawing cache

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/coop");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri photoURI = FileProvider.getUriForFile(AllMembers.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            outFile);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    AllMembers.this.startActivity(Intent.createChooser(intent, "View using"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(AllMembers.this, "Report Generated", Toast.LENGTH_SHORT).show();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").orderByChild("type").equalTo("member")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot task) {
                        if (!task.exists()) {
                            totalBtn.setText("No members yet");
                        } else {
                            final double[] totalcbu = {0};
                            ArrayList<String> userList = new ArrayList<String>();
                            for (DataSnapshot child : task.getChildren()) {
                                String full_name = AccountSettings.decode(child.child("fullname").getValue().toString());
                                mDatabase.child("balance").orderByChild("user_id").equalTo(child.getKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String cbu = "";
                                                double patronage = 0.00;
                                                for (DataSnapshot child2 : snapshot.getChildren()) {
                                                    if (child2.child("type").getValue().toString().matches("CBU")) {
//                                                            Log.d("jjjjjj",child.getValue().toString());
//                                                            Log.d("jjjjjj222",child2.getValue().toString());
                                                        cbu = child2.child("amount").getValue().toString();
                                                        totalcbu[0] += Double.parseDouble(child2.child("amount").getValue().toString());
                                                    } else if (child2.child("type").getValue().toString().matches("patronage_refund")) {
                                                        patronage += Double.parseDouble(child2.child("amount").getValue().toString());
                                                    }
                                                }
                                                userList.add(full_name + " @" + AccountSettings.decode(child.child("username").getValue().toString())
                                                        + System.getProperty("line.separator") + "Capital Build Up: P" + cbu
                                                        + System.getProperty("line.separator") + "Patronage Refund: P" + patronage);
                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AllMembers.this, R.layout.activity_listview, R.id.textView, userList);
                                                simpleList.setAdapter(arrayAdapter);
                                                totalBtn.setText("Total Capital: P" + totalcbu[0]);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                reportBtn.setEnabled(true);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("User", (String) adapterView.getItemAtPosition(i));
                Intent go = new Intent(AllMembers.this, MemberInfoIndividual.class);
                go.putExtra("username", adapterView.getItemAtPosition(i).toString().split("@")[1]);
                startActivity(go);
                finish();
            }
        });

//        if (flag == 0) {
//            Toast.makeText(AllMembers.this, "No pending requests", Toast.LENGTH_SHORT).show();
//        } else {
        {
            // ...
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllMembers.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

