package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.fypapp.R;

public class UserTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_type);

        final Button entrepreneurButton = findViewById(R.id.entrepreneur_button);
        final Button retiredUserButton = findViewById(R.id.retired_user_button);

        entrepreneurButton.setOnClickListener(view -> saveEntrepreneur());
        retiredUserButton.setOnClickListener(view -> saveRetiredUser());
    }

    private void saveEntrepreneur() {
        // save that the user is an entrepreneur

        Intent i = new Intent(UserTypeActivity.this, EditSearchActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void saveRetiredUser() {
        // save that the user is a retired user

        Intent i = new Intent(UserTypeActivity.this, EditProfileActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
