package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
        new AlertDialog.Builder(this)
                .setTitle("Register")
                .setMessage("Do you really want to register as an Entrepreneur?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToCreateSearch())
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void goToCreateSearch() {
        Intent i = new Intent(UserTypeActivity.this, EditSearchActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void saveRetiredUser() {
        // save that the user is a retired user
        new AlertDialog.Builder(this)
                .setTitle("Register")
                .setMessage("Do you really want to register as a Retiree?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToCreateProfile())
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void goToCreateProfile() {
        Intent i = new Intent(UserTypeActivity.this, EditProfileActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
