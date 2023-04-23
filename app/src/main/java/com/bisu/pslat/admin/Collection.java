package com.bisu.pslat.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Collection extends AppCompatActivity {

    ListView simpleList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        simpleList = (ListView) findViewById(R.id.usersListView);
        Button back = (Button) findViewById(R.id.backButton);
        Button total = (Button) findViewById(R.id.total);
//        Button PaymentsBtn = (Button) findViewById(R.id.PaymentsBtn);
        Button reportBtn = (Button) findViewById(R.id.report);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
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
                    Uri photoURI = FileProvider.getUriForFile(Collection.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            outFile);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Collection.this.startActivity(Intent.createChooser(intent, "View using"));
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
                Toast.makeText(Collection.this, "Report Generated", Toast.LENGTH_SHORT).show();
            }
        });

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("payment_requests").orderByChild("status").equalTo("approved").get()
//
//                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            ArrayList<String> loanList = new ArrayList<>();
//                            double[] totalDeposits = {0};
////                            double[] totalInterest = {0};
////                            double[] totalService = {0};
////                            double[] totalSur = {0};
//                            for (DataSnapshot child : task.getResult().getChildren()) {
//                                String userId = child.child("user_id").getValue().toString();
//                                mDatabase.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DataSnapshot> snapshot) {
//                                        if (snapshot.isSuccessful()) {
////                                          String fullName = snapshot.getResult().child("fullname").getValue(String.class);
//
////                                            String guarantorName = child.child("guarantor_username").exists() ? new String(Base64.decode(child.child("guarantor_username").getValue().toString(), Base64.DEFAULT)) : "Active Member";
//
//                                            String loanInfo = "Status " + child.child("status").getValue().toString();
////                                                    "Loan Amount: P" + child.child("amount").getValue().toString() + "\n" +
////                                                    "Interest: P" + child.child("interest").getValue().toString() + "\n" +
////                                                    "Service Charge: P" + child.child("service_charge").getValue().toString() + "\n" +
////                                                    "Surcharge: P" + child.child("sur_charge").getValue().toString();
//                                            loanList.add(loanInfo);
//                                            totalDeposits[0] += Double.parseDouble(child.child("payment").getValue().toString());
////                                            totalInterest[0] += Double.parseDouble(child.child("interest").getValue().toString());
////                                            totalService[0] += Double.parseDouble(child.child("service_charge").getValue().toString());
////                                            totalSur[0] += Double.parseDouble(child.child("sur_charge").getValue().toString());
//                                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Collection.this, R.layout.activity_listview, R.id.textView, loanList);
//                                            simpleList.setAdapter(arrayAdapter);
//                                            total.setText("Total Loans: P" + totalDeposits[0]);
////                                                    "\nTotal Interest: P" + totalInterest[0] +
////                                                    "\nTotal Service Charge: P" + totalService[0] +
////                                                    "\nTotal Surcharge: P" + totalSur[0]);
//                                        } else {
//                                            // Handle error
//                                        }
//                                    }
//                                });
//                            }
//                            reportBtn.setEnabled(true);
//                        } else {
//                            // Handle error
//                        }
//                    }
//                });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("payment_requests").orderByChild("status").equalTo("approved")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot task) {
                        if (!task.exists()) {
//                            totalBtn.setText("No members yet");
                        } else {
                            final double[] totalcbu = {0};
                            ArrayList<String> userList = new ArrayList<String>();
                            for (DataSnapshot child : task.getChildren()) {
                                String payment = child.child("payment").getValue().toString();
                                String date_created = child.child("date_created").getValue().toString();
                                String paymentType = child.child("payment_type").getValue().toString();
                                mDatabase.child("balance").orderByChild("user_id").equalTo(child.getKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String loanPayment = "";
                                                double totalPayments = 0.00;
                                                for (DataSnapshot child2 : snapshot.getChildren()) {
                                                    if (child2.child("payment_type").getValue().toString().matches("Loan Payment")) {
//                                                            Log.d("jjjjjj",child.getValue().toString());
//                                                            Log.d("jjjjjj222",child2.getValue().toString());
                                                        loanPayment = child2.child("payment").getValue().toString();
                                                        totalcbu[0] += Double.parseDouble(child2.child("payment").getValue().toString());
                                                    }
//                                                    } else if (child2.child("type").getValue().toString().matches("patronage_refund")) {
//                                                        patronage += Double.parseDouble(child2.child("amount").getValue().toString());
//                                                    }

                                                }
                                                userList.add("Amount: " + payment + System.getProperty("line.separator")
                                                        + System.getProperty("line.separator") + "Payment Type " + paymentType

                                                        + System.getProperty("line.separator") + "Date Paid: " + date_created);

                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Collection.this, R.layout.activity_listview, R.id.textView, userList);
                                                simpleList.setAdapter(arrayAdapter);
                                                total.setText("Total Payments: P" + totalcbu[0]);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                reportBtn.setEnabled(true);
                            }
                        }

                        // add the if statement here
                        if (task.exists()) {
//                            totalBtn.setText("No members yet");
                            // handle pending requests
                        } else {
                            Toast.makeText(Collection.this, "No pending requests", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        PaymentsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Collection.this, AllWithdrawals.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Collection.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
