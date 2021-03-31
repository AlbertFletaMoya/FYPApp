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

public class EditNameActivity extends AppCompatActivity {
    private static final String TAG = "EditNameActivity";

    private TextInputEditText firstNameView;
    private TextInputEditText lastNameView;

    private boolean isRegistration = false;
    private String originalFirstName;
    private String originalLastName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        firstNameView = findViewById(R.id.first_name_write_view);
        lastNameView = findViewById(R.id.last_name_write_view);

        TextView saveButton = findViewById(R.id.save_view);

        TextView cancelButton = findViewById(R.id.cancel_view);

        ConstraintLayout layout = findViewById(R.id.top_bar);
        Button nextButton = findViewById(R.id.next_button);
        TextView textView = findViewById(R.id.text_view);

        if (getIntent().getExtras() != null) {
            isRegistration = getIntent().getBooleanExtra(IS_REGISTRATION, false);
            if (isRegistration) {
                layout.setVisibility(View.GONE);
            }

            else {
                nextButton.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
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
                                originalFirstName = retiree.getFirstName();
                                originalLastName = retiree.getLastName();
                                firstNameView.setText(originalFirstName);
                                lastNameView.setText(originalLastName);
                                cancelButton.setOnClickListener(view ->
                                        cancel());
                                saveButton.setOnClickListener(view -> saveName(documentId, retiree, isRegistration));
                                nextButton.setOnClickListener(view -> saveName(documentId, retiree, isRegistration));
                            }
                        }

                        else {
                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                        }
                    });
        }
    }

    private void cancel() {
        if (Objects.requireNonNull(firstNameView.getText()).toString().trim().equals(originalFirstName)
        && Objects.requireNonNull(lastNameView.getText()).toString().trim().equals(originalLastName)) {
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

        final TextInputLayout firstNameLayout = findViewById(R.id.first_name_layout);
        final TextInputLayout lastNameLayout = findViewById(R.id.last_name_layout);

        if (Objects.requireNonNull(firstNameView.getText()).toString().trim().equals("")) {
            firstNameLayout.setError("Please enter your first name");
            valid = false;
        }

        if (Objects.requireNonNull(lastNameView.getText()).toString().trim().equals("")) {
            lastNameLayout.setError("Please enter your last name");
            valid = false;
        }

        return valid;
    }

    private void saveName(String documentId, Retiree retiree, boolean isRegistration) {
        if (validateFields()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            retiree.setFirstName(Objects.requireNonNull(firstNameView.getText()).toString().trim());
            retiree.setLastName(Objects.requireNonNull(lastNameView.getText()).toString().trim());
            Map<String, Object> retireeMap = retiree.toMap();
            db.collection(RETIREE_USERS)
                    .document(documentId)
                    .update(retireeMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        if (isRegistration) {
                            Intent i = new Intent(EditNameActivity.this, EditLocationActivity.class);
                            i.putExtra(DOCUMENT_ID, documentId);
                            i.putExtra(IS_REGISTRATION, true);
                            startActivity(i);
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        }
    }

    @Override
    public void onBackPressed() {
        if (isRegistration) {
            super.onBackPressed();
        } else {
            cancel();
        }
    }
}
