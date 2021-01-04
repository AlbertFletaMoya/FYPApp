package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.project.fypapp.R;
import com.project.fypapp.model.UserProfile;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {
    private EditText profileHeadlineView;
    private EditText locationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        final TextView saveButton = findViewById(R.id.save_view);
        final TextView cancelButton = findViewById(R.id.cancel_view);

        profileHeadlineView = findViewById(R.id.headline_write_view);
        locationView = findViewById(R.id.location_write_view);

        // User is editing the information instead of creating a new profile
        if (getIntent().getExtras() != null) {
            final UserProfile userProfile = new UserProfile();

            profileHeadlineView.setText(userProfile.getBio());
            locationView.setText(userProfile.getRegion());

            cancelButton.setOnClickListener(view -> cancel(profileHeadlineView.getText().toString().trim(),
                    locationView.getText().toString().trim()));
            saveButton.setOnClickListener(view -> saveProfile(false));
        }

        // User is creating a new profile
        else {
            final TextView activityTitle = findViewById(R.id.page_title_view);
            activityTitle.setText(R.string.create_profile);
            ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
            saveButton.setOnClickListener(view -> saveProfile(true));
        }

    }

    private void saveProfile(boolean newProfile) {
        final EditText bioView = findViewById(R.id.headline_write_view);
        final EditText locationView = findViewById(R.id.location_write_view);

        final String bio = bioView.getText().toString().trim();
        final String location = locationView.getText().toString().trim();

        if (validateFields()) {

            UserProfile userProfile = new UserProfile("Albert Fleta", "leinadfc01@gmail.com", new ArrayList<>(),
                    location, bio);

            // save the profile or overwrite it depending on newProfile

            goToMain();
        }
    }

    private void goToMain() {
        Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("firstLogIn", false);
        i.putExtra("profileBelongsToUser", true);
        startActivity(i);
        finish();
    }

    private void cancel(String headline, String location) {
        UserProfile userProfile = new UserProfile();
        if (!userProfile.getBio().trim().equals(headline)
                || !userProfile.getRegion().trim().equals(location)) {

            new AlertDialog.Builder(this)
                    .setTitle("Discard changes")
                    .setMessage("Do you really want to discard your changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToMain())
                    .setNegativeButton(android.R.string.no, null).show();
        }

        else {
            goToMain();
        }
    }

    private boolean validateFields() {
        boolean valid = true;

        final TextInputLayout headlineLayout = findViewById(R.id.headline_layout);
        final TextInputLayout locationLayout = findViewById(R.id.location_layout);

        if (profileHeadlineView.getText().toString().trim().equals("")) {
            headlineLayout.setError("Please enter a profile headline");
            valid = false;
        }

        if (locationView.getText().toString().trim().equals("")) {
            locationLayout.setError("Please enter your current location");
            valid = false;
        }

        return valid;
    }
}
