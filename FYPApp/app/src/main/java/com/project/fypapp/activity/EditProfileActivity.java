package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.fypapp.R;
import com.project.fypapp.model.UserProfile;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        final Button saveButton = findViewById(R.id.save_button);

        final Button cancelButton = findViewById(R.id.cancel_button);

        // User is editing the information instead of creating a new profile
        if (getIntent().getExtras() != null) {
            final UserProfile userProfile = new UserProfile();

            final EditText headlineView = findViewById(R.id.headline_write_view);
            headlineView.setText(userProfile.getBio());

            final EditText locationView = findViewById(R.id.location_write_view);
            locationView.setText(userProfile.getRegion());

            cancelButton.setOnClickListener(view -> goToMain());

            saveButton.setOnClickListener(view -> saveProfile(false));
        }

        // User is creating a new profile
        else {
            ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
            saveButton.setOnClickListener(view -> saveProfile(true));
        }

    }

    private void saveProfile(boolean newProfile) {
        final EditText bioView = findViewById(R.id.headline_write_view);
        final EditText locationView = findViewById(R.id.location_write_view);

        final String bio = bioView.getText().toString().trim();
        final String location = locationView.getText().toString().trim();

        if (bio.equals("") || location.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all the profile fields", Toast.LENGTH_LONG).show();
        }

        else {

            UserProfile userProfile = new UserProfile("Albert Fleta", "leinadfc01@gmail.com", new ArrayList<>(),
                    location, bio);

            // save the profile or overwrite it depending on new profile

            goToMain();
        }
    }

    private void goToMain() {
        Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("firstLogIn", false);
        startActivity(i);
        finish();
    }
}
