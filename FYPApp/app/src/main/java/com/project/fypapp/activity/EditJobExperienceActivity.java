package com.project.fypapp.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.dialog.MonthYearPickerDialog;
import com.project.fypapp.model.JobExperience;

import java.util.Objects;

import static com.project.fypapp.model.JobExperience.JOB_EXPERIENCES;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.ERROR_ADDING_DOCUMENT;
import static com.project.fypapp.util.Constants.MONTH_MAP;
import static com.project.fypapp.util.Constants.NEW_EXPERIENCE;
import static com.project.fypapp.util.Constants.NEW_INFO;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_DELETED;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_DELETED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.USER_ID;
import static com.project.fypapp.util.Constants.addedSuccessfully;

public class EditJobExperienceActivity extends AppCompatActivity {
    private static final String TAG = "EditJobExperienceActivity";

    private EditText startingDateView;
    private EditText endingDateView;
    private EditText companyView;
    private EditText roleView;
    private EditText jobDescriptionView;

    private String userId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job_experience);

        startingDateView = findViewById(R.id.starting_date_write_view);
        endingDateView = findViewById(R.id.ending_date_write_view);
        companyView = findViewById(R.id.company_name_write_view);
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
            String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            userId = getIntent().getStringExtra(USER_ID);
            final boolean newExperience = getIntent().getBooleanExtra(NEW_EXPERIENCE, false);

            if (newExperience) {
                final TextView activityTitle = findViewById(R.id.page_title_view);
                activityTitle.setText(R.string.add_experience);
                saveButton.setOnClickListener(view -> saveExperience(userId));
                ((ViewManager)deleteButton.getParent()).removeView(deleteButton);
                cancelButton.setOnClickListener(view -> cancelNewExperience(startingDateView.getText().toString().trim(),
                        endingDateView.getText().toString().trim(),
                        companyView.getText().toString().trim(),
                        roleView.getText().toString().trim(),
                        jobDescriptionView.getText().toString().trim()));
            }

            else {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                assert documentId != null;
                    db.collection(JOB_EXPERIENCES)
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                final JobExperience jobExperience = Objects.requireNonNull(task.getResult()).toObject(JobExperience.class);

                                assert jobExperience != null;
                                companyView.setText(jobExperience.getCompany());
                                roleView.setText(jobExperience.getPosition());
                                startingDateView.setText(jobExperience.getStartingDate());
                                endingDateView.setText(jobExperience.getEndingDate());
                                jobDescriptionView.setText(jobExperience.getJobDescription());

                            } else {
                                Log.d(TAG, COULD_NOT_RETRIEVE_DATA, task.getException());
                            }
                        });


                deleteButton.setOnClickListener(view -> deleteExperience(documentId));
                saveButton.setOnClickListener(view -> updateExperience(documentId, userId));

                cancelButton.setOnClickListener(view -> cancel(documentId, startingDateView.getText().toString().trim(),
                        endingDateView.getText().toString().trim(),
                        companyView.getText().toString().trim(),
                        roleView.getText().toString().trim(),
                        jobDescriptionView.getText().toString().trim(), userId));
            }
        }
    }

    private void saveExperience(String userId) {
        final JobExperience jobExperience = new JobExperience(companyView.getText().toString().trim(),
                roleView.getText().toString().trim(), startingDateView.getText().toString().trim(),
                endingDateView.getText().toString().trim(), jobDescriptionView.getText().toString().trim(), userId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(JOB_EXPERIENCES)
                .add(jobExperience)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, addedSuccessfully(documentReference.getId()));
                    goToIndex();
                })
                .addOnFailureListener(e -> Log.d(TAG, ERROR_ADDING_DOCUMENT));
    }

    private void updateExperience(String documentId, String userId) {
        if (validateFields()) {
            final JobExperience jobExperience = new JobExperience(companyView.getText().toString().trim(),
                    roleView.getText().toString().trim(), startingDateView.getText().toString().trim(),
                    endingDateView.getText().toString().trim(), jobDescriptionView.getText().toString().trim(), userId);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(JOB_EXPERIENCES)
                    .document(documentId)
                    .update(jobExperience.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        goToIndex();
                    })

                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        }
    }

    private void deleteExperience(String documentId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_experience)
                .setMessage(R.string.want_to_delete_experience)
                .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(JOB_EXPERIENCES).document(documentId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, SUCCESSFULLY_DELETED);
                                goToIndex();})
                            .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_DELETED, e));
                })
                .setNegativeButton(R.string.no, null).show();

    }

    private void goToIndex() {
        Intent i = new Intent(EditJobExperienceActivity.this, ExperienceIndexActivity.class);
        i.putExtra(DOCUMENT_ID, userId);
        i.putExtra(NEW_INFO, true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private boolean validateFields() {
        boolean valid = true;

        final TextInputLayout roleLayout = findViewById(R.id.role_layout);
        final TextInputLayout startingDateLayout = findViewById(R.id.starting_date_layout);
        final TextInputLayout endingDateLayout = findViewById(R.id.ending_date_layout);

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
            final String monthString = MONTH_MAP.get(month);
            final String yearString = Integer.toString(year);
            startingDateView.setText(String.format("%s %s", monthString, yearString));
        };

        final DatePickerDialog.OnDateSetListener endingDateSetListener = (view, year, month, dayOfMonth) -> {
            final String monthString = MONTH_MAP.get(month);
            final String yearString = Integer.toString(year);
            endingDateView.setText(String.format("%s %s", monthString, yearString));
        };

        final DatePickerDialog.OnDateSetListener dateSetListener =
                isStartingDate ? startingDateSetListener : endingDateSetListener;

        new MonthYearPickerDialog(dateSetListener).show(getSupportFragmentManager(), "Month Year Picker");
    }

    private void cancel(String documentId ,String startingDate, String endingDate, String companyName,
                        String role, String jobDescriptionString, String userId) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(JOB_EXPERIENCES)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final JobExperience jobExperience = Objects.requireNonNull(task.getResult()).toObject(JobExperience.class);

                        assert jobExperience != null;
                        if (!jobExperience.getCompany().trim().equals(companyName)
                                || !jobExperience.getPosition().trim().equals(role)
                                || !jobExperience.getStartingDate().trim().equals(startingDate)
                                || !jobExperience.getEndingDate().trim().equals(endingDate)
                                || !jobExperience.getJobDescription().trim().equals(jobDescriptionString)) {

                            new AlertDialog.Builder(this)
                                    .setTitle(R.string.discard_changes)
                                    .setMessage(R.string.want_to_discard_changes)
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                                    .setNegativeButton(android.R.string.no, null).show();
                        }

                        else {
                            Log.d(TAG, "User id is: " + userId);
                            finish();
                        }
                    }

                    else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }

    private void cancelNewExperience(String startingDate, String endingDate, String companyName,
                                     String role, String jobDescriptionString) {
        if (!startingDate.equals("") || !endingDate.equals("") || !companyName.equals("")
            || !role.equals("") || !jobDescriptionString.equals("")) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle(R.string.discard_changes)
                    .setMessage(R.string.want_to_discard_changes)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            finish();
        }
    }

}
