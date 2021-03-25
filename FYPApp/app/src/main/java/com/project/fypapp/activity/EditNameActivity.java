package com.project.fypapp.activity;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.model.Retiree;

import java.util.Map;
import java.util.Objects;

import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;

public class EditNameActivity extends AppCompatActivity {
    public static final String IS_REGISTRATION = "isRegistration";
    private static final String TAG = "EditNameActivity";

    private TextInputEditText firstNameView;
    private TextInputEditText lastNameView;

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
            if (getIntent().getBooleanExtra(IS_REGISTRATION, false)) {
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
                                firstNameView.setText(retiree.getFirstName());
                                lastNameView.setText(retiree.getLastName());
                                cancelButton.setOnClickListener(view ->
                                        cancel(retiree.getFirstName(), retiree.getLastName()));
                                saveButton.setOnClickListener(view -> saveName(documentId, retiree));
                            }
                        }

                        else {
                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                        }
                    });


        }


    }

    private void cancel(String originalFirstName, String originalLastName) {
        if (firstNameView.getText().toString().trim().equals(originalFirstName)
        && lastNameView.getText().toString().trim().equals(originalLastName)) {
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

    private void saveName(String documentId, Retiree retiree) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        retiree.setFirstName(Objects.requireNonNull(firstNameView.getText()).toString().trim());
        retiree.setLastName(Objects.requireNonNull(lastNameView.getText()).toString().trim());
        Map<String, Object> retireeMap = retiree.toMap();
        db.collection(RETIREE_USERS)
                .document(documentId)
                .update(retireeMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, SUCCESSFULLY_UPDATED);
                    finish();
                })
                .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
    }
}
