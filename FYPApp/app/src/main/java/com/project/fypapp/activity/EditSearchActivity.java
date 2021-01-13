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
import com.project.fypapp.model.SearchDocument;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            String documentId = getIntent().getStringExtra("documentId");
            boolean newSearch = getIntent().getBooleanExtra("newSearch", false);

            if (!newSearch && documentId != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("searches")
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                SearchDocument userSearch = Objects.requireNonNull(task.getResult()).toObject(SearchDocument.class);
                                assert userSearch != null;
                                rolesEditText.setText(userSearch.getListOfRolesAsString());
                                sectorsEditText.setText(userSearch.getListOfSectorsAsString());
                                minYearsEditText.setText(String.valueOf(userSearch.getMin_years_of_experience()));
                                jobDescriptionEditText.setText(userSearch.getJob_description());

                                cancelButton.setOnClickListener(view -> cancel(documentId, rolesEditText.getText().toString().trim(),
                                        sectorsEditText.getText().toString().trim(),
                                        minYearsEditText.getText().toString().trim(),
                                        jobDescriptionEditText.getText().toString().trim()));
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        });
            }

            else {
                ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
                pageTitle.setText(R.string.create_search);
                searchButton.setText(R.string.search);
            }

            searchButton.setOnClickListener(view -> createSearch(documentId));
        }
    }

    private void createSearch(String documentId) {
        final String rolesString = rolesEditText.getText().toString().trim();
        final String sectorsString = sectorsEditText.getText().toString().trim();
        final String yearsString = minYearsEditText.getText().toString().trim();
        final String jobDescriptionString = jobDescriptionEditText.getText().toString().trim();

        if (validateInputs()) {
            final Map<String, Object> search = new HashMap<>();
            search.put("roles", Arrays.asList(Arrays.stream(rolesString.split(",")).map(String::trim).toArray(String[]::new)));
            search.put("sectors", Arrays.asList(Arrays.stream(sectorsString.split(",")).map(String::trim).toArray(String[]::new)));
            search.put("min_years_of_experience", Integer.parseInt(yearsString));
            search.put("job_description", jobDescriptionString);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("searches")
                    .document(documentId)
                    .update(search)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Search was successfully updated");
                        goToSearchResults();
                    })

                    .addOnFailureListener(e -> Log.d(TAG, "Search couldn't be updated"));
        }
    }

    private void goToSearchResults() {
        Intent i = new Intent(EditSearchActivity.this, SearchResultsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void cancel(String documentId, String roles, String sectors, String yearsOfExperience, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("searches")
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SearchDocument searchDocument = Objects.requireNonNull(task.getResult()).toObject(SearchDocument.class);
                            assert searchDocument != null;
                            if (!searchDocument.getRoles().equals(Arrays.asList(Arrays.stream(roles.split(",")).map(String::trim).toArray(String[]::new)))
                                    || !searchDocument.getSectors().equals(Arrays.asList(Arrays.stream(sectors.split(",")).map(String::trim).toArray(String[]::new)))
                                    || searchDocument.getMin_years_of_experience() != Integer.parseInt(yearsOfExperience)
                                    || !searchDocument.getJob_description().equals(description)){

                                new AlertDialog.Builder(this)
                                        .setTitle("Discard changes")
                                        .setMessage("Do you really want to discard your changes?")
                                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToSearchResults())
                                        .setNegativeButton(android.R.string.no, null).show();
                            }

                            else {
                                goToSearchResults();
                            }
                        } else {
                            Log.d(TAG, "Couldn't retrieve data");
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
