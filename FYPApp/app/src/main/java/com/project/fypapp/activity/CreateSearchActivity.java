package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.project.fypapp.R;
import com.project.fypapp.model.UserSearch;

import java.util.Arrays;

public class CreateSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_search);

        final Button searchButton = findViewById(R.id.search_button);

        if (getIntent().getExtras() != null) {
            UserSearch userSearch = new UserSearch();
            final EditText rolesEditText = findViewById(R.id.roles_write_view); // implement these as drop downs
            final EditText sectorsEditText = findViewById(R.id.sectors_write_view);
            final EditText minYearsEditText = findViewById(R.id.years_experience_write_view);
            final EditText jobDescriptionEditText = findViewById(R.id.job_description_write_view);

            rolesEditText.setText(userSearch.getListOfRoles().toString());
            sectorsEditText.setText(userSearch.getListOfSectors().toString());
            minYearsEditText.setText(String.valueOf(userSearch.getMinYearsOfExperience()));
            jobDescriptionEditText.setText(userSearch.getJobDescription());
            searchButton.setOnClickListener(view -> createSearch(false));
        }

        searchButton.setOnClickListener(view -> createSearch(true));
    }

    private void createSearch(boolean newSearch) {
        final EditText rolesEditText = findViewById(R.id.roles_write_view); // implement these as drop downs
        final EditText sectorsEditText = findViewById(R.id.sectors_write_view);
        final EditText minYearsEditText = findViewById(R.id.years_experience_write_view);
        final EditText jobDescriptionEditText = findViewById(R.id.job_description_write_view);

        final String rolesString = rolesEditText.getText().toString().trim();
        final String sectorsString = sectorsEditText.getText().toString().trim();
        final String yearsString = minYearsEditText.getText().toString().trim();
        final String jobDescriptionString = jobDescriptionEditText.getText().toString().trim();

        if (rolesString.equals("") || sectorsString.equals("") || yearsString.equals("") || jobDescriptionString.equals("")){
            Toast.makeText(getApplicationContext(), "Please fill in all the search parameters", Toast.LENGTH_LONG).show();
        }

        else{
            UserSearch userSearch = new UserSearch(
                    Arrays.asList(rolesString.split(" ")),
                    Arrays.asList(sectorsString.split(" ")),
                    Integer.parseInt(yearsString),
                    jobDescriptionString
            );

            // save the user search if newSearch save if false just overwrite the existing one potentially
            // using the id passed in the intent

            Intent i = new Intent(CreateSearchActivity.this, SearchResultsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
