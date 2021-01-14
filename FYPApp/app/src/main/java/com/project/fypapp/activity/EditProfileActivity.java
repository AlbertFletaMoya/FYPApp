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
import com.project.fypapp.model.Retiree;

import java.util.HashMap;
import java.util.Map;

import static com.project.fypapp.model.Retiree.HEADLINE;
import static com.project.fypapp.model.Retiree.LOCATION;
import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.NEW_USER;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;

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

        if (getIntent().getExtras() != null) {
            final String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            final boolean newUser = getIntent().getBooleanExtra(NEW_USER, false);

            if (!newUser) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                assert documentId != null;
                db.collection(RETIREE_USERS)
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                Retiree retiree = task.getResult().toObject(Retiree.class);
                                profileHeadlineView.setText(retiree.getHeadline());
                                locationView.setText(retiree.getLocation());
                            } else {
                                Log.d(TAG, COULD_NOT_RETRIEVE_DATA, task.getException());
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
        final String headline = profileHeadlineView.getText().toString().trim();
        final String location = locationView.getText().toString().trim();

        if (validateFields()) {
           final Map<String, Object> retireeMap = new HashMap<>();
           retireeMap.put(HEADLINE, headline);
           retireeMap.put(LOCATION, location);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(RETIREE_USERS)
                    .document(documentId)
                    .update(retireeMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        goToMain(documentId);
                    })

                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        }
    }

    private void goToMain(String documentId) {
        Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DOCUMENT_ID, documentId);
        i.putExtra(PROFILE_BELONGS_TO_USER, true);
        startActivity(i);
        finish();
    }

    private void cancel(String headline, String location, String documentId) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RETIREE_USERS)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final Retiree retiree = task.getResult().toObject(Retiree.class);
                        if (!retiree.getHeadline().trim().equals(headline)
                                || !retiree.getLocation().trim().equals(location)) {

                            new AlertDialog.Builder(this)
                                    .setTitle(R.string.discard_changes)
                                    .setMessage(R.string.want_to_discard_changes)
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToMain(documentId))
                                    .setNegativeButton(android.R.string.no, null).show();
                        }

                        else {
                            goToMain(documentId);
                        }
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
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
