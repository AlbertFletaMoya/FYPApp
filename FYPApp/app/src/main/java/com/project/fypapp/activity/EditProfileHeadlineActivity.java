package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.model.Retiree;

import java.util.Map;
import java.util.Objects;

import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.IS_REGISTRATION;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;

public class EditProfileHeadlineActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileHeadlineActivity";

    private TextInputEditText headlineView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_headline);

        headlineView = findViewById(R.id.headline_write_view);

        TextView saveButton = findViewById(R.id.save_view);
        TextView cancelButton = findViewById(R.id.cancel_view);

        ConstraintLayout layout = findViewById(R.id.top_bar);
        Button nextButton = findViewById(R.id.next_button);
        Button skipButton = findViewById(R.id.skip_button);
        TextView textView = findViewById(R.id.text_view);

        if (getIntent().getExtras() != null) {
            boolean isRegistration = getIntent().getBooleanExtra(IS_REGISTRATION, false);
            if (isRegistration) {
                layout.setVisibility(View.GONE);
            }

            else {
                nextButton.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                skipButton.setVisibility(View.GONE);
            }

            String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            assert documentId != null;
            db.collection(RETIREE_USERS)
                    .document(documentId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                            final DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                Log.d(TAG, "DOCUMENT ID: " + documentId);
                                final Retiree retiree = document.toObject(Retiree.class);
                                assert retiree != null;
                                headlineView.setText(retiree.getHeadline());
                                cancelButton.setOnClickListener(view ->
                                        cancel(retiree.getHeadline()));
                                saveButton.setOnClickListener(view -> saveHeadline(documentId, retiree, isRegistration));
                                nextButton.setOnClickListener(view -> saveHeadline(documentId, retiree, isRegistration));
                                skipButton.setOnClickListener(view -> skipToNext(documentId));
                            }
                        }

                        else {
                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                        }
                    });
        }
    }

    private void cancel(String originalHeadline) {
        if (Objects.requireNonNull(headlineView.getText()).toString().trim().equals(originalHeadline)) {
            finish();
        }

        else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.discard_changes)
                    .setMessage(R.string.want_to_discard_changes)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    private boolean validateFields() {
        boolean valid = true;

        final TextInputLayout headlineLayout = findViewById(R.id.headline_layout);

        if (Objects.requireNonNull(headlineView.getText()).toString().trim().equals("")) {
            headlineLayout.setError("Please enter a profile headline");
            valid = false;
        }

        return valid;
    }

    private void saveHeadline(String documentId, Retiree retiree, boolean isRegistration) {
        // if (validateFields()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            retiree.setHeadline(Objects.requireNonNull(headlineView.getText()).toString().trim());
            Map<String, Object> retireeMap = retiree.toMap();
            db.collection(RETIREE_USERS)
                    .document(documentId)
                    .update(retireeMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        if (isRegistration) {
                            skipToNext(documentId);
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        // }
    }

    private void skipToNext(String documentId) {
        Intent i = new Intent(EditProfileHeadlineActivity.this, EditProfilePhotoActivity.class);
        i.putExtra(IS_REGISTRATION, true);
        i.putExtra(DOCUMENT_ID, documentId);
        startActivity(i);
    }
}
