package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.fypapp.R;
import com.project.fypapp.model.JobDescription;

import static com.project.fypapp.util.Constants.ADD_NEW_EXPERIENCE;

public class EditJobExperienceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_job_experience);

        final Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> goToIndex());

        final Button saveButton = findViewById(R.id.save_button);

        if (getIntent().getExtras() != null) {
            if (getIntent().getIntExtra("experienceId", ADD_NEW_EXPERIENCE) == ADD_NEW_EXPERIENCE) {
                saveButton.setOnClickListener(view -> saveExperience(true));
            }

            else {
                JobDescription jobDescription = new JobDescription();

                final EditText companyView = findViewById(R.id.company_name_write_view);
                final EditText sectorView = findViewById(R.id.sector_write_view);
                final EditText roleView = findViewById(R.id.position_write_view);
                final EditText startingDateView = findViewById(R.id.starting_date_write_view);
                final EditText endingDateView = findViewById(R.id.ending_date_write_view);
                final EditText jobDescriptionView = findViewById(R.id.job_description_write_view);

                companyView.setText(jobDescription.getCompanyName());
                sectorView.setText(jobDescription.getSector());
                roleView.setText(jobDescription.getPosition());
                startingDateView.setText(jobDescription.getStartingDate());
                endingDateView.setText(jobDescription.getEndingDate());
                jobDescriptionView.setText(jobDescription.getJobDescription());

                saveButton.setOnClickListener(view -> saveExperience(false));
            }
        }
    }

    private void saveExperience(boolean newExperience) {
        final EditText companyView = findViewById(R.id.company_name_write_view);
        final EditText sectorView = findViewById(R.id.sector_write_view);
        final EditText roleView = findViewById(R.id.position_write_view);
        final EditText startingDateView = findViewById(R.id.starting_date_write_view);
        final EditText endingDateView = findViewById(R.id.ending_date_write_view);
        final EditText jobDescriptionView = findViewById(R.id.job_description_write_view);

        final String company = companyView.getText().toString().trim();
        final String sector = sectorView.getText().toString().trim();
        final String role = roleView.getText().toString().trim();
        final String startingDate = startingDateView.getText().toString().trim();
        final String endingDate = endingDateView.getText().toString().trim();
        final String jobDescriptionString = jobDescriptionView.getText().toString().trim();

        if (company.equals("") || sector.equals("") || role.equals("") || startingDate.equals("") ||
            endingDate.equals("") || jobDescriptionString.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all the job description fields", Toast.LENGTH_LONG).show();
        }

        else {
            JobDescription jobDescription = new JobDescription(
                    companyView.getText().toString().trim(),
                    roleView.getText().toString().trim(),
                    startingDateView.getText().toString().trim(),
                    endingDateView.getText().toString().trim(),
                    jobDescriptionView.getText().toString().trim(),
                    sectorView.getText().toString().trim()
            );

            // save the job description or overwrite depending on new experience

            goToIndex();
        }
    }

    private void goToIndex() {
        Intent i = new Intent(EditJobExperienceActivity.this, ExperienceIndexActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
