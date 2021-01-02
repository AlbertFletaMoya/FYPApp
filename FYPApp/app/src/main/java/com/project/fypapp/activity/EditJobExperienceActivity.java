package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.project.fypapp.R;
import com.project.fypapp.model.JobDescription;


import static com.project.fypapp.util.Constants.ADD_NEW_EXPERIENCE;

public class EditJobExperienceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_job_experience);

        final TextView cancelButton = findViewById(R.id.cancel_view);
        cancelButton.setOnClickListener(view -> goToIndex());

        final TextView saveButton = findViewById(R.id.save_view);
        final TextView deleteButton = findViewById(R.id.delete_experience_view);

        if (getIntent().getExtras() != null) {
            if (getIntent().getIntExtra("experienceId", ADD_NEW_EXPERIENCE) == ADD_NEW_EXPERIENCE) {
                saveButton.setOnClickListener(view -> saveExperience(true));
                ((ViewManager)deleteButton.getParent()).removeView(deleteButton);

            }

            else {
                JobDescription jobDescription = new JobDescription();

                final EditText companyView = findViewById(R.id.company_name_write_view);
                final EditText sectorView = findViewById(R.id.sector_write_view);
                final EditText roleView = findViewById(R.id.roles_write_view);
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
                deleteButton.setOnClickListener(view -> deleteExperience());
            }
        }
    }

    private void saveExperience(boolean newExperience) {
        final EditText companyView = findViewById(R.id.company_name_write_view);
        final EditText sectorView = findViewById(R.id.sector_write_view);
        final EditText roleView = findViewById(R.id.roles_write_view);
        final EditText startingDateView = findViewById(R.id.starting_date_write_view);
        final EditText endingDateView = findViewById(R.id.ending_date_write_view);
        final EditText jobDescriptionView = findViewById(R.id.job_description_write_view);

        if (validateFields(companyView, sectorView, roleView, startingDateView, endingDateView)) {
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

    private void deleteExperience() {
        // delete the experience
        new AlertDialog.Builder(this)
                .setTitle("Delete experience")
                .setMessage("Do you really want to delete this job experience?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    Toast.makeText(EditJobExperienceActivity.this, "Would delete experience", Toast.LENGTH_SHORT).show();
                    //delete
                    goToIndex();
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    private void goToIndex() {
        Intent i = new Intent(EditJobExperienceActivity.this, ExperienceIndexActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private boolean validateFields(EditText companyView, EditText sectorView, EditText roleView, EditText startingDateView,
                                   EditText endingDateView) {
        boolean valid = true;

        final TextInputLayout companyNameLayout = findViewById(R.id.company_name_layout);
        final TextInputLayout sectorLayout = findViewById(R.id.sector_layout);
        final TextInputLayout roleLayout = findViewById(R.id.role_layout);
        final TextInputLayout startingDateLayout = findViewById(R.id.starting_date_layout);
        final TextInputLayout endingDateLayout = findViewById(R.id.ending_date_layout);

        if (companyView.getText().toString().trim().equals("")) {
            companyNameLayout.setError("Please enter the name of the company");
            valid = false;
        }

        if (sectorView.getText().toString().trim().equals("")) {
            sectorLayout.setError("Please enter the company's sector");
            valid = false;
        }

        if (roleView.getText().toString().trim().equals("")) {
            roleLayout.setError("Please enter your role in the company");
            valid = false;
        }

        if (startingDateView.getText().toString().trim().equals("")) {
            startingDateLayout.setError("Please enter the start date");
            valid = false;
        }

        if (endingDateView.getText().toString().trim().equals("")) {
            endingDateLayout.setError("Please enter the end date");
            valid = false;
        }

        return valid;
    }
}
