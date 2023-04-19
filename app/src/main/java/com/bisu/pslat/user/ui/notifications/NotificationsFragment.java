package com.bisu.pslat.user.ui.notifications;

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
import com.bisu.pslat.GuarantorRequest;
import com.bisu.pslat.GuarantorRequestInformation;
import com.bisu.pslat.Login;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;
import com.bisu.pslat.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    public static ListView simpleList;
    public static DatabaseReference mDatabase;
    private static Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        NotificationsFragment.context = getContext();
        simpleList = binding.loanListView;

        TextView logoutBtn = binding.logoutButton;
        TextView accButton = binding.accBtn;

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
        //final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        loadList();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void loadList(){
        simpleList.setAdapter(null);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("guarantor_requests").orderByChild("status").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            ArrayList<String> userList = new ArrayList<String>();
                            final String[] full_name = {""};
                            final String[] username = { "" };
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if(child.child("guarantor_username").getValue().toString().matches(UserDashboard.username[0])){
                                    String user_id = child.child("user_id").getValue().toString();
                                    mDatabase.child("users").child(user_id).get()
                                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task2) {
                                                    if (!task2.isSuccessful()) {
                                                        Log.e("firebase", "Error getting data", task2.getException());
                                                    }
                                                    else {
                                                        full_name[0] = task2.getResult().child("fullname").getValue().toString();
                                                        username[0] = task2.getResult().child("username").getValue().toString();

                                                        userList.add(AccountSettings.decode(full_name[0]) +" @"+ AccountSettings.decode(username[0])
                                                        +System.getProperty("line.separator")+"Date Requested: "
                                                                +child.child("date_created").getValue().toString());
                                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NotificationsFragment.context, R.layout.activity_listview, R.id.textView, userList);
                                                        simpleList.setAdapter(arrayAdapter);
                                                    }
                                                }
                                            });
                                }
                            }

                            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Log.d("User", (String) adapterView.getItemAtPosition(i));
                                    Intent go = new Intent(NotificationsFragment.context, GuarantorRequestInformation.class);
                                    go.putExtra("username",adapterView.getItemAtPosition(i).toString().split("@")[1]
                                            .split("Date Requested: ")[0].trim());
                                    go.putExtra("date_created",adapterView.getItemAtPosition(i).toString().split("@")[1]
                                            .split("Date Requested: ")[1]);
                                    NotificationsFragment.context.startActivity(go);
                                }
                            });
                        }
                        else {
                            Toast.makeText(NotificationsFragment.context, "No pending requests", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}