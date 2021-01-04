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
import com.project.fypapp.model.UserSearch;

import java.util.Arrays;

public class EditSearchActivity extends AppCompatActivity {
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
            UserSearch userSearch = new UserSearch();

            rolesEditText.setText(userSearch.getListOfRolesAsString());
            sectorsEditText.setText(userSearch.getListOfSectorsAsString());
            minYearsEditText.setText(String.valueOf(userSearch.getMinYearsOfExperience()));
            jobDescriptionEditText.setText(userSearch.getJobDescription());

            searchButton.setOnClickListener(view -> createSearch(false));
            cancelButton.setOnClickListener(view -> cancel(rolesEditText.getText().toString().trim(),
                    sectorsEditText.getText().toString().trim(),
                    minYearsEditText.getText().toString().trim(),
                    jobDescriptionEditText.getText().toString().trim()));
        }

        else {
            ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
            searchButton.setOnClickListener(view -> createSearch(true));
            pageTitle.setText(R.string.create_search);
            searchButton.setText(R.string.search);
        }
    }

    private void createSearch(boolean newSearch) {
        final String rolesString = rolesEditText.getText().toString().trim();
        final String sectorsString = sectorsEditText.getText().toString().trim();
        final String yearsString = minYearsEditText.getText().toString().trim();
        final String jobDescriptionString = jobDescriptionEditText.getText().toString().trim();

        if (validateInputs()) {
            UserSearch userSearch = new UserSearch(
                    Arrays.asList(rolesString.split(" ")),
                    Arrays.asList(sectorsString.split(" ")),
                    Integer.parseInt(yearsString),
                    jobDescriptionString
            );

            // save the user search if newSearch save if false just overwrite the existing one potentially
            // using the id passed in the intent

            goToSearchResults();
        }
    }

    private void goToSearchResults() {
        Intent i = new Intent(EditSearchActivity.this, SearchResultsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void cancel(String roles, String sectors, String yearsOfExperience, String description) {
        UserSearch userSearch = new UserSearch();
        if (!userSearch.getListOfRoles().equals(Arrays.asList(Arrays.stream(roles.split(",")).map(String::trim).toArray(String[]::new)))
        || !userSearch.getListOfSectors().equals(Arrays.asList(Arrays.stream(sectors.split(",")).map(String::trim).toArray(String[]::new)))
        || userSearch.getMinYearsOfExperience() != Integer.parseInt(yearsOfExperience)
        || !userSearch.getJobDescription().equals(description)){

            new AlertDialog.Builder(this)
                    .setTitle("Discard changes")
                    .setMessage("Do you really want to discard your changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToSearchResults())
                    .setNegativeButton(android.R.string.no, null).show();
        }

        else {
            goToSearchResults();
        }
    }

    private boolean validateInputs() {
        if (jobDescriptionEditText.getText().toString().trim().equals("")) {
            final TextInputLayout jobDescriptionLayout = findViewById(R.id.description_layout);
            jobDescriptionLayout.setError("Please enter a description");
            return false;
        }

        return true;
    }
}
