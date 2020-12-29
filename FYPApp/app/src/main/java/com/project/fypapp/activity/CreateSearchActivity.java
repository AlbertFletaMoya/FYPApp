package com.project.fypapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.fypapp.R;
import com.project.fypapp.model.UserSearch;

import java.util.Arrays;

public class CreateSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_search);

        final Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(view -> createSearch());
    }

    private void createSearch() {
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
            // save the user search

            // navigate to search results page here
            String message = String.format("Would go to search results page with roles %s, sectors %s, years of experience %s and job description %s",
                    rolesString, sectorsString, yearsString, jobDescriptionString);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
