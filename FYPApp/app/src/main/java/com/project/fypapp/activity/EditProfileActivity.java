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
import java.util.Objects;

import static com.project.fypapp.model.Retiree.CITY;
import static com.project.fypapp.model.Retiree.COUNTRY;
import static com.project.fypapp.model.Retiree.FIRST_NAME;
import static com.project.fypapp.model.Retiree.HEADLINE;
import static com.project.fypapp.model.Retiree.LAST_NAME;
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

    private EditText firstNameView;
    private EditText lastNameView;
    private EditText headlineView;
    private EditText cityView;
    private EditText countryView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        final TextView saveButton = findViewById(R.id.save_view);
        final TextView cancelButton = findViewById(R.id.cancel_view);

        firstNameView = findViewById(R.id.first_name_write_view);
        lastNameView = findViewById(R.id.last_name_write_view);
        headlineView = findViewById(R.id.headline_write_view);
        cityView = findViewById(R.id.city_write_view);
        countryView = findViewById(R.id.country_write_view);

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
                                Retiree retiree = Objects.requireNonNull(task.getResult()).toObject(Retiree.class);
                                assert retiree != null;
                                firstNameView.setText(retiree.getFirstName());
                                lastNameView.setText(retiree.getLastName());
                                headlineView.setText(retiree.getHeadline());
                                cityView.setText(retiree.getCity());
                                countryView.setText(retiree.getCountry());
                            } else {
                                Log.d(TAG, COULD_NOT_RETRIEVE_DATA, task.getException());
                            }
                        });

                cancelButton.setOnClickListener(view -> cancel(firstNameView.getText().toString().trim(),
                        lastNameView.getText().toString().trim(),
                        headlineView.getText().toString().trim(),
                        cityView.getText().toString().trim(),
                        countryView.getText().toString().trim(),
                        documentId));
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
        final String firstName = firstNameView.getText().toString().trim();
        final String lastName = lastNameView.getText().toString().trim();
        final String headline = headlineView.getText().toString().trim();
        final String city = cityView.getText().toString().trim();
        final String country = countryView.getText().toString().trim();

        if (validateFields()) {
           final Map<String, Object> retireeMap = new HashMap<>();
           retireeMap.put(FIRST_NAME, firstName);
           retireeMap.put(LAST_NAME, lastName);
           retireeMap.put(HEADLINE, headline);
           retireeMap.put(CITY, city);
           retireeMap.put(COUNTRY, country);

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

    private void cancel(String firstName, String lastName, String headline, String city,
                        String country, String documentId) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RETIREE_USERS)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final Retiree retiree = Objects.requireNonNull(task.getResult()).toObject(Retiree.class);
                        assert retiree != null;
                        if (!retiree.getFirstName().trim().equals(firstName)
                                || !retiree.getLastName().trim().equals(lastName)
                                || !retiree.getHeadline().trim().equals(headline)
                                || !retiree.getCity().trim().equals(city)
                                || !retiree.getCountry().trim().equals(country)) {

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

        final TextInputLayout firstNameLayout = findViewById(R.id.first_name_layout);
        final TextInputLayout lastNameLayout = findViewById(R.id.last_name_layout);
        final TextInputLayout headlineLayout = findViewById(R.id.headline_layout);
        final TextInputLayout cityLayout = findViewById(R.id.city_layout);
        final TextInputLayout countryLayout = findViewById(R.id.country_layout);

        if (firstNameView.getText().toString().trim().equals("")) {
            firstNameLayout.setError("Please enter your first name");
            valid = false;
        }

        if (lastNameView.getText().toString().trim().equals("")) {
            lastNameLayout.setError("Please enter your last name");
            valid = false;
        }

        if (headlineView.getText().toString().trim().equals("")) {
            headlineLayout.setError("Please enter a profile headline");
            valid = false;
        }

        if (cityView.getText().toString().trim().equals("")) {
            cityLayout.setError("Please enter your current city");
            valid = false;
        }

        if (countryView.getText().toString().trim().equals("")) {
            countryLayout.setError("Please enter your current country");
            valid = false;
        }

        return valid;
    }
}
