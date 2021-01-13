package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

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
            final String documentId = getIntent().getStringExtra("documentId");
            final boolean newUser = getIntent().getBooleanExtra("newUser", false);

            if (!newUser) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                assert documentId != null;
                db.collection("retiree_users")
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Successfully retrieved the document");
                                profileHeadlineView.setText(Objects.requireNonNull(task.getResult()).getString("headline"));
                                locationView.setText(task.getResult().getString("location"));
                            } else {
                                Log.d(TAG, "Error getting documents.", task.getException());
                            }
                        });

                cancelButton.setOnClickListener(view -> cancel(profileHeadlineView.getText().toString().trim(),
                        locationView.getText().toString().trim(), documentId));
            }

            else {
                final TextView activityTitle = findViewById(R.id.page_title_view);
                activityTitle.setText(R.string.create_profile);
                ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
            }

            saveButton.setOnClickListener(view -> saveProfile(documentId));
        }
    }

    private void saveProfile(String documentId) {
        final EditText bioView = findViewById(R.id.headline_write_view);
        final EditText locationView = findViewById(R.id.location_write_view);

        final String bio = bioView.getText().toString().trim();
        final String location = locationView.getText().toString().trim();

        if (validateFields()) {

            Map<String, Object> newData = new HashMap<>();
            newData.put("headline", bio);
            newData.put("location", location);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("retiree_users")
                    .document(documentId)
                    .update(newData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Profile was successfully saved");
                        goToMain(documentId);
                    })

                    .addOnFailureListener(e -> Log.d(TAG, "Profile couldn't be saved"));
        }
    }

    private void goToMain(String documentId) {
        Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("documentId", documentId);
        i.putExtra("profileBelongsToUser", true);
        startActivity(i);
        finish();
    }

    private void cancel(String headline, String location, String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("retiree_users")
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfile userProfile = new UserProfile("", "", new ArrayList<>(),
                                Objects.requireNonNull(task.getResult()).getString("headline"),
                                task.getResult().getString("location"));
                        if (!userProfile.getBio().trim().equals(headline)
                                || !userProfile.getRegion().trim().equals(location)) {

                            new AlertDialog.Builder(this)
                                    .setTitle("Discard changes")
                                    .setMessage("Do you really want to discard your changes?")
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToMain(documentId))
                                    .setNegativeButton(android.R.string.no, null).show();
                        }

                        else {
                            goToMain(documentId);
                        }
                    } else {
                        Log.d(TAG, "Couldn't retrieve data");
                    }
                });
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
