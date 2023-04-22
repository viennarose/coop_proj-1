package com.bisu.pslat.user.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.GuarantorRequestInformation;
import com.bisu.pslat.Login;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;
import com.bisu.pslat.UserLoanInformation;
import com.bisu.pslat.databinding.FragmentDashboardBinding;
import com.bisu.pslat.user.ui.notifications.NotificationsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    public static ListView simpleList,loanlistView;
    public static DatabaseReference mDatabase;
    private static Context context;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        DashboardFragment.context = getContext();
        simpleList = binding.reqloanListView;
        loanlistView = binding.loanListView;
        //final TextView textView = binding.textDashboard;
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        TextView logoutBtn = binding.logoutButton;
        TextView accButton = binding.accBtn;
        loadLoanReqList();
        loadLoanList();
        accButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AccountSettings.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    public static void loadLoanReqList(){
        simpleList.setAdapter(null);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("payment_requests").orderByChild("user_id").equalTo(UserDashboard.user_id[0])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            ArrayList<String> userList = new ArrayList<String>();
                            final String[] payment = {""};
                            final String[] month = {""};
                            final String[] date_created = { "" };
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if(child.child("status").getValue().toString().matches("approved")){
                                    String user_id = child.child("user_id").getValue().toString();
                                    payment[0] = child.child("payment").getValue().toString();
                                    month[0] = child.child("month").getValue().toString();
                                    date_created[0] = child.child("date_created").getValue().toString();

                                    userList.add("Payment Amount: P"+payment[0] +System.getProperty("line.separator")+"Month of: "+month[0] +System.getProperty("line.separator")+"Date Requested: "+date_created[0]);
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DashboardFragment.context, R.layout.activity_listview2, R.id.textView, userList);
                                    simpleList.setAdapter(arrayAdapter);
                                }
                            }

                        }
                        else {
                            Toast.makeText(DashboardFragment.context, "No pending requests", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void loadLoanList(){
        loanlistView.setAdapter(null);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("loans").orderByChild("user_id").equalTo(UserDashboard.user_id[0])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            ArrayList<String> userList = new ArrayList<String>();
                            final String[] amount = {""};
                            final String[] months = { "" };
                            final String[] interest = { "" };
                            final String[] service_charge = { "" };
                            final String[] surcharge = { "" };
                            final String[] date_created = { "" };
                            final String[] guarantor_name = {""};

                            for (DataSnapshot child : snapshot.getChildren()) {
                                amount[0] = child.child("amount").getValue().toString();
                                months[0] = child.child("months").getValue().toString();
                                interest[0] = child.child("interest").getValue().toString();
                                service_charge[0] = child.child("service_charge").getValue().toString();                                interest[0] = child.child("interest").getValue().toString();
                                surcharge[0] = child.child("sur_charge").getValue().toString();
                                date_created[0] = child.child("date_created").getValue().toString();

                                // check if guarantor exists in user's node
                                if (child.child("guarantor_username").exists()) {
                                    guarantor_name[0] = child.child("guarantor_username").getValue().toString();
                                } else {
                                    guarantor_name[0] = "Active Member";
                                }

                                userList.add("Loan Amount: P"+amount[0]
                                        +System.getProperty("line.separator")+"Interest: "+interest[0]
                                        +System.getProperty("line.separator")+"Service Charge: "+service_charge[0]
                                        +System.getProperty("line.separator")+"Surcharge: "+surcharge[0]
                                        +System.getProperty("line.separator")+"Months to pay: "+months[0] + " month/s"
                                        + System.getProperty("line.separator") + "Guarantor: " + guarantor_name[0]
                                        +System.getProperty("line.separator")+"Date Requested: "+date_created[0]);
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DashboardFragment.context, R.layout.activity_listview2, R.id.textView, userList);
                                loanlistView.setAdapter(arrayAdapter);
                            }
                            loanlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                    Log.d("HHHH",adapterView.getItemAtPosition(i).toString().split(",")[1]);
//                                    Intent go = new Intent(DashboardFragment.context, UserLoanInformation.class);
//                                    go.putExtra("date_created",adapterView.getItemAtPosition(i).toString().split(",")[1]
//                                            .split(": ")[1]);
//                                    DashboardFragment.context.startActivity(go);
                                }
                            });
                        }
                        else {
                            Toast.makeText(DashboardFragment.context, "No pending loans", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}