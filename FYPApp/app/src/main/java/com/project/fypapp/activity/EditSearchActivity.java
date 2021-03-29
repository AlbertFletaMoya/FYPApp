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
import com.project.fypapp.model.Search;

import java.util.Objects;

import static com.project.fypapp.model.Search.SEARCHES;
import static com.project.fypapp.model.Search.stringToList;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.NEW_SEARCH;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;

public class EditSearchActivity extends AppCompatActivity {
    private static final String TAG = "EditSearchActivity";

    private EditText rolesEditText;
    private EditText sectorsEditText;
    private EditText minYearsEditText;
    private EditText jobDescriptionEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_search);

        final TextView searchButton = findViewById(R.id.search_view);
        final TextView cancelButton = findViewById(R.id.cancel_view);
        final TextView pageTitle = findViewById(R.id.page_title_view);

        rolesEditText = findViewById(R.id.roles_write_view);
        sectorsEditText = findViewById(R.id.sectors_write_view);
        minYearsEditText = findViewById(R.id.years_experience_write_view);
        jobDescriptionEditText = findViewById(R.id.description_write_view);

        if (getIntent().getExtras() != null) {
            String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            boolean newSearch = getIntent().getBooleanExtra(NEW_SEARCH, false);

            if (!newSearch && documentId != null) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(SEARCHES)
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                final Search userSearch = Objects.requireNonNull(task.getResult()).toObject(Search.class);
                                assert userSearch != null;
                                rolesEditText.setText(userSearch.getListOfRolesAsString());
                                sectorsEditText.setText(userSearch.getListOfSectorsAsString());
                                minYearsEditText.setText(String.valueOf(userSearch.getMinYearsOfExperience()));
                                jobDescriptionEditText.setText(userSearch.getJobDescription());

                                cancelButton.setOnClickListener(view -> cancel(documentId, rolesEditText.getText().toString().trim(),
                                        sectorsEditText.getText().toString().trim(),
                                        minYearsEditText.getText().toString().trim(),
                                        jobDescriptionEditText.getText().toString().trim()));
                            } else {
                                Log.d(TAG, COULD_NOT_RETRIEVE_DATA, task.getException());
                            }
                        });
            }

            else {
                ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
                pageTitle.setText(R.string.create_search);
                searchButton.setText(R.string.search);
            }

            searchButton.setOnClickListener(view -> createSearch(documentId, newSearch));
        }
    }

    private void createSearch(String documentId, boolean newSearch) {
        final String rolesString = rolesEditText.getText().toString().trim();
        final String sectorsString = sectorsEditText.getText().toString().trim();
        final String yearsString = minYearsEditText.getText().toString().trim();
        final String jobDescriptionString = jobDescriptionEditText.getText().toString().trim();

        if (validateInputs()) {
            final Search search = new Search(stringToList(rolesString), stringToList(sectorsString),
                    Integer.parseInt(yearsString), jobDescriptionString);


            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(SEARCHES)
                    .document(documentId)
                    .update(search.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        goToSearchResults(newSearch);
                    })

                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        }
    }

    private void goToSearchResults(boolean newSearch) {
        if (newSearch) {
            final Intent i = new Intent(EditSearchActivity.this, SearchResultsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        finish();
    }

    private void cancel(String documentId, String roles, String sectors, String yearsOfExperience,
                        String description) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(SEARCHES)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                            final Search search = Objects.requireNonNull(task.getResult()).toObject(Search.class);
                            assert search != null;
                            if (!search.getRoles().equals(Search.stringToList(roles))
                                    || !search.getSectors().equals(Search.stringToList(sectors))
                                    || search.getMinYearsOfExperience() != Integer.parseInt(yearsOfExperience)
                                    || !search.getJobDescription().equals(description)){

                                new AlertDialog.Builder(this)
                                        .setTitle(R.string.discard_changes)
                                        .setMessage(R.string.want_to_discard_changes)
                                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToSearchResults(false))
                                        .setNegativeButton(android.R.string.no, null).show();
                            }

                            else {
                                goToSearchResults(false);
                            }
                        } else {
                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                        }
                });
    }

    private boolean validateInputs() {
        boolean valid = true;
        if (jobDescriptionEditText.getText().toString().trim().equals("")) {
            final TextInputLayout jobDescriptionLayout = findViewById(R.id.description_layout);
            jobDescriptionLayout.setError("Please enter a description");
            valid = false;
        }

        final String minYearsString = minYearsEditText.getText().toString().trim();
        final TextInputLayout minYearsLayout = findViewById(R.id.years_experience_layout);

        try {
            int years = Integer.parseInt(minYearsString);
            if (years < 0) {
                minYearsLayout.setError("Please enter a positive number");
                valid = false;
            }
        }

        catch (NumberFormatException e) {
            minYearsLayout.setError("Please enter a valid number");
            valid = false;
        }

        return valid;
    }
}
