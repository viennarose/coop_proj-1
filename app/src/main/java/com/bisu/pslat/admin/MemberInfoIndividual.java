package com.bisu.pslat.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bisu.pslat.R;

public class MemberInfoIndividual extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info_individual);

        Button back = findViewById(R.id.memberbackButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberInfoIndividual.this, AllMembers.class);
                startActivity(intent);
                finish();
            }
        });
    }
}