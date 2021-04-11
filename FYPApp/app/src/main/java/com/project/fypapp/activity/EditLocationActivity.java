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
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.successfullySaved;

public class EditLocationActivity extends AppCompatActivity {
    private static final String TAG = "EditLocationActivity";

    private TextInputEditText countryView;
    private TextInputEditText cityView;

    private boolean isRegistration = false;
    private String originalCity;
    private String originalCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        countryView = findViewById(R.id.country_write_view);
        cityView = findViewById(R.id.city_write_view);

        TextView saveButton = findViewById(R.id.save_view);

        TextView cancelButton = findViewById(R.id.cancel_view);

        ConstraintLayout layout = findViewById(R.id.top_bar);
        Button nextButton = findViewById(R.id.next_button);
        Button skipButton = findViewById(R.id.skip_button);
        TextView textView = findViewById(R.id.text_view);

        if (getIntent().getExtras() != null) {
            isRegistration = getIntent().getBooleanExtra(IS_REGISTRATION, false);
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
                                originalCity = retiree.getCity();
                                originalCountry = retiree.getCountry();
                                cityView.setText(originalCity);
                                countryView.setText(originalCountry);

                                cancelButton.setOnClickListener(view ->
                                        cancel());
                                saveButton.setOnClickListener(view -> saveLocation(documentId, retiree));
                                nextButton.setOnClickListener(view -> saveLocation(documentId, retiree));
                                skipButton.setOnClickListener(view -> skipToNext(documentId));
                            }
                        }

                        else {
                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                        }
                    });
        }
    }

    private void cancel() {
        if (!hasChanged()) {
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

    private boolean hasChanged() {
        return (!Objects.requireNonNull(cityView.getText()).toString().trim().equals(originalCity)
                || !Objects.requireNonNull(countryView.getText()).toString().trim().equals(originalCountry));
    }

    private void saveLocation(String documentId, Retiree retiree) {
        if (!hasChanged()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(DOCUMENT_ID, documentId);
            intent.putExtra(PROFILE_BELONGS_TO_USER, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            retiree.setCity(Objects.requireNonNull(cityView.getText()).toString().trim());
            retiree.setCountry(Objects.requireNonNull(countryView.getText()).toString().trim());
            Map<String, Object> retireeMap = retiree.toMap();
            db.collection(RETIREE_USERS)
                    .document(documentId)
                    .update(retireeMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        if (isRegistration) {
                            goToNext(documentId);
                        } else {
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra(DOCUMENT_ID, documentId);
                            intent.putExtra(PROFILE_BELONGS_TO_USER, true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            successfullySaved(this);
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        }
    }

    private void goToNext(String documentId) {
        Intent i = new Intent(EditLocationActivity.this, EditProfileHeadlineActivity.class);
        i.putExtra(IS_REGISTRATION, true);
        i.putExtra(DOCUMENT_ID, documentId);
        startActivity(i);
        finish();
    }

    private void skipToNext(String documentId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.skip_location)
                .setMessage(R.string.want_to_skip)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToNext(documentId))
                .setNegativeButton(android.R.string.no, null).show();
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
