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
    private double totalWithdrawal;
    private double totalLoanPayment;
    private double totalDeposit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_totals);
        Button back = (Button) findViewById(R.id.backButton);
        Button totalBtn = (Button) findViewById(R.id.total);
//        Button totalDepositBtn = (Button) findViewById(R.id.totalDeposit);
//        Button totalLoanBtn = (Button) findViewById(R.id.totalLoan);
        Button reportBtn = (Button) findViewById(R.id.report);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager())
            {
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }
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
                    Uri photoURI = FileProvider.getUriForFile(OverallTotals.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            outFile);
                    intent.setDataAndType(photoURI,"image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    OverallTotals.this.startActivity(Intent.createChooser(intent, "View using"));
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
                Toast.makeText(OverallTotals.this, "Report Generated", Toast.LENGTH_SHORT).show();
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
                            for (DataSnapshot child : task.getChildren()) {
                                mDatabase.child("balance").orderByChild("user_id").equalTo(child.getKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot child2 : snapshot.getChildren()) {
                                                    if (child2.child("type").getValue().toString().matches("Withdrawal")) {
                                                        totalWithdrawal += Double.parseDouble(child2.child("amount").getValue().toString());
                                                    }
//                                                    if (child2.child("type").getValue().toString().matches("Loan Payment")) {
//                                                        totalLoanPayment += Double.parseDouble(child2.child("payment").getValue().toString());
//                                                    }
//                                                    if (child2.child("type").getValue().toString().matches("Deposit")) {
//                                                        totalDeposit += Double.parseDouble(child2.child("payment").getValue().toString());
//                                                    }
                                                }
                                                totalBtn.setText("Total Capital: P" + totalWithdrawal);
//                                                totalDepositBtn.setText("Total Capital: P" + totalLoanPayment);
//                                                totalLoanBtn.setText("Total Capital: P" + totalDeposit);


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