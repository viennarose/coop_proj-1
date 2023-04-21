package com.bisu.pslat.user.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bisu.pslat.AccountSettings;
import com.bisu.pslat.JoinMembership;
import com.bisu.pslat.Login;
import com.bisu.pslat.MemberLoan;
import com.bisu.pslat.MemberPayment;
import com.bisu.pslat.NotMemberLoan;
import com.bisu.pslat.R;
import com.bisu.pslat.UserDashboard;
import com.bisu.pslat.UserLoanInformation;
import com.bisu.pslat.databinding.FragmentHomeBinding;
import com.bisu.pslat.user.ui.dashboard.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    String type;
    String status;
    private FragmentHomeBinding binding;

    TextView stat;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final Button joinB = binding.joinButton;
        final Button loanB = binding.loanButton;
        final Button paymentLoan = binding.paymentLoanButton;
        final TextView textView = binding.textHome;
        stat = binding.status;
//        final TextView bal = binding.balanceValue;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        joinB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), JoinMembership.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").orderByChild("email").equalTo(user.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                            if(snapshot.getValue() != null){
                                for (DataSnapshot child1 : snapshot.getChildren()){
                                    type=child1.child("type").getValue().toString();
                                    UserDashboard.fullname[0] = child1.child("fullname").getValue().toString();
                                    UserDashboard.username[0] = child1.child("username").getValue().toString();

                                    if(child1.child("type").getValue().toString().matches("member")){
                                        mDatabase.child("balance").orderByChild("user_id").equalTo(user.getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                        double balanceVal = 0.00;

                                                        for(DataSnapshot ds : snapshot2.getChildren()){
                                                            balanceVal+=Double.parseDouble(ds.child("amount").getValue().toString());
                                                        }
//                                                        bal.setText("P"+balanceVal);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                    else {
//                                        bal.setText("P0.00");
                                    }
                                }
                                textView.setText("Hello "+AccountSettings.decode(UserDashboard.fullname[0])+"!");

                                //textView.startAnimation(shake);
                                if(type.matches("not_member")){
                                    stat.setText("You're not a member");
                                    stat.setCompoundDrawablesWithIntrinsicBounds(R.drawable.round_error_outline_24, 0, 0, 0);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loanB.startAnimation(shake);
                                            loanB.setVisibility(View.VISIBLE);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    joinB.startAnimation(shake);
                                                    joinB.setVisibility(View.VISIBLE);
                                                }
                                            }, 600);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    paymentLoan.startAnimation(shake);
                                                    paymentLoan.setVisibility(View.VISIBLE);
                                                }
                                            }, 600);
                                        }
                                    }, 1000);

                                    loanB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(), NotMemberLoan.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                                else {
                                    stat.setText("Active Member");
                                    stat.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24, 0, 0, 0);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            loanB.startAnimation(shake);
                                            loanB.setVisibility(View.VISIBLE);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    paymentLoan.startAnimation(shake);
                                                    paymentLoan.setVisibility(View.VISIBLE);
                                                }
                                            }, 600);
                                        }

                                    }, 1000);
                                    loanB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(), MemberLoan.class);
                                            startActivity(intent);
                                        }
                                    });

                                    paymentLoan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(), MemberPayment.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                            else {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getContext(), Login.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
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

}