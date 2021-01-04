package com.project.fypapp.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.project.fypapp.R;
import com.project.fypapp.dialog.MonthYearPickerDialog;
import com.project.fypapp.model.JobDescription;

import static com.project.fypapp.util.Constants.ADD_NEW_EXPERIENCE;
import static com.project.fypapp.util.Constants.MONTH_MAP;

public class EditJobExperienceActivity extends AppCompatActivity {
    private EditText startingDateView;
    private EditText endingDateView;
    private EditText companyView;
    private EditText sectorView;
    private EditText roleView;
    private EditText jobDescriptionView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job_experience);

        startingDateView = findViewById(R.id.starting_date_write_view);
        endingDateView = findViewById(R.id.ending_date_write_view);
        companyView = findViewById(R.id.company_name_write_view);
        sectorView = findViewById(R.id.sector_write_view);
        roleView = findViewById(R.id.roles_write_view);
        jobDescriptionView = findViewById(R.id.job_description_write_view);

        final TextView saveButton = findViewById(R.id.save_view);
        final TextView deleteButton = findViewById(R.id.delete_experience_view);

        final TextView cancelButton = findViewById(R.id.cancel_view);

        startingDateView.setInputType(0);
        endingDateView.setInputType(0);

        startingDateView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                showDialog(true);
            }
            return true;
        });

        endingDateView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                showDialog(false);
            }
            return true;
        });

        if (getIntent().getExtras() != null) {
            if (getIntent().getIntExtra("experienceId", ADD_NEW_EXPERIENCE) == ADD_NEW_EXPERIENCE) {
                final TextView activityTitle = findViewById(R.id.page_title_view);
                activityTitle.setText(R.string.add_experience);

                saveButton.setOnClickListener(view -> saveExperience(true));
                ((ViewManager)deleteButton.getParent()).removeView(deleteButton);

                cancelButton.setOnClickListener(view -> cancelNewExperience(startingDateView.getText().toString().trim(),
                        endingDateView.getText().toString().trim(),
                        companyView.getText().toString().trim(),
                        sectorView.getText().toString().trim(),
                        roleView.getText().toString().trim(),
                        jobDescriptionView.getText().toString().trim()));
            }

            else {
                JobDescription jobDescription = new JobDescription();

                companyView.setText(jobDescription.getCompanyName());
                sectorView.setText(jobDescription.getSector());
                roleView.setText(jobDescription.getPosition());
                startingDateView.setText(jobDescription.getStartingDate());
                endingDateView.setText(jobDescription.getEndingDate());
                jobDescriptionView.setText(jobDescription.getJobDescription());

                saveButton.setOnClickListener(view -> saveExperience(false));
                deleteButton.setOnClickListener(view -> deleteExperience());

                cancelButton.setOnClickListener(view -> cancel(startingDateView.getText().toString().trim(),
                        endingDateView.getText().toString().trim(),
                        companyView.getText().toString().trim(),
                        sectorView.getText().toString().trim(),
                        roleView.getText().toString().trim(),
                        jobDescriptionView.getText().toString().trim()));
            }
        }
    }

    private void saveExperience(boolean newExperience) {
        if (validateFields()) {
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
                .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                    Toast.makeText(EditJobExperienceActivity.this, "Would delete experience", Toast.LENGTH_SHORT).show();
                    //delete
                    goToIndex();
                })
                .setNegativeButton(R.string.no, null).show();

    }

    private void goToIndex() {
        Intent i = new Intent(EditJobExperienceActivity.this, ExperienceIndexActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private boolean validateFields() {
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

    private void showDialog(boolean isStartingDate) {
        final DatePickerDialog.OnDateSetListener startingDateSetListener = (view, year, month, dayOfMonth) -> {
            String monthString = MONTH_MAP.get(month);
            String yearString = Integer.toString(year);
            startingDateView.setText(String.format("%s %s", monthString, yearString));
        };

        final DatePickerDialog.OnDateSetListener endingDateSetListener = (view, year, month, dayOfMonth) -> {
            String monthString = MONTH_MAP.get(month);
            String yearString = Integer.toString(year);
            endingDateView.setText(String.format("%s %s", monthString, yearString));
        };

        final DatePickerDialog.OnDateSetListener dateSetListener =
                isStartingDate ? startingDateSetListener : endingDateSetListener;

        new MonthYearPickerDialog(dateSetListener).show(getSupportFragmentManager(), "Month Year Picker");
    }

    private void cancel(String startingDate, String endingDate, String companyName,
                        String sector, String role, String jobDescriptionString) {
        JobDescription jobDescription = new JobDescription();
        if (!jobDescription.getCompanyName().trim().equals(companyName)
        || !jobDescription.getSector().trim().equals(sector)
        || !jobDescription.getPosition().trim().equals(role)
        || !jobDescription.getStartingDate().trim().equals(startingDate)
        || !jobDescription.getEndingDate().trim().equals(endingDate)
        || !jobDescription.getJobDescription().trim().equals(jobDescriptionString)) {

            new AlertDialog.Builder(this)
                    .setTitle("Discard changes")
                    .setMessage("Do you really want to discard your changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToIndex())
                    .setNegativeButton(android.R.string.no, null).show();
        }

        else {
            goToIndex();
        }
    }

    private void cancelNewExperience(String startingDate, String endingDate, String companyName,
                                     String sector, String role, String jobDescriptionString) {
        if (!companyName.equals("") || !startingDate.equals("") || !endingDate.equals("") ||
        !sector.equals("") || !role.equals("") || !jobDescriptionString.equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard changes")
                    .setMessage("Do you really want to discard your changes?")
                    .setPositiveButton(R.string.yes, (dialog, whichButton) -> goToIndex())
                    .setNegativeButton(R.string.no, null).show();
        }

        else {
            goToIndex();
        }
    }
}
