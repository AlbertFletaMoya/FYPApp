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
import com.project.fypapp.model.Search;

import java.util.Objects;

import static com.project.fypapp.model.Search.SEARCHES;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.NEW_SEARCH;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;

public class EditSearchActivity extends AppCompatActivity {
    private static final String TAG = "EditSearchActivity";

    private EditText jobDescriptionEditText;
    private TextView skillsView;
    private TextView interestsView;

    private boolean newSearch = false;
    private Search userSearch;
    private String documentId;
    private String jobDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_search);

        final TextView searchButton = findViewById(R.id.search_view);
        final TextView cancelButton = findViewById(R.id.cancel_view);
        final TextView pageTitle = findViewById(R.id.page_title_view);

        jobDescriptionEditText = findViewById(R.id.description_write_view);
        skillsView = findViewById(R.id.skills_view);
        interestsView = findViewById(R.id.interests_view);

        if (getIntent().getExtras() != null) {
            documentId = getIntent().getStringExtra(DOCUMENT_ID);
            newSearch = getIntent().getBooleanExtra(NEW_SEARCH, false);

            if (documentId != null) {
                final TextView editSkillsView = findViewById(R.id.edit_skills_view);
                final TextView editInterestsView = findViewById(R.id.edit_interests_view);

                editSkillsView.setOnClickListener(view -> {
                    Intent i = new Intent(EditSearchActivity.this, EditSearchSkillsActivity.class);
                    i.putExtra(DOCUMENT_ID, documentId);
                    startActivity(i);
                });

                editInterestsView.setOnClickListener(view -> {
                    Intent i = new Intent(EditSearchActivity.this, EditSearchInterestsActivity.class);
                    i.putExtra(DOCUMENT_ID, documentId);
                    startActivity(i);
                });

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(SEARCHES)
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                userSearch = Objects.requireNonNull(task.getResult()).toObject(Search.class);
                                assert userSearch != null;
                                jobDescriptionEditText.setText(userSearch.getJobDescription());
                                skillsView.setText(Retiree.customSetToString(userSearch.getSkills()));
                                interestsView.setText(Retiree.customSetToString(userSearch.getInterests()));

                                jobDescription = userSearch.getJobDescription();
                                cancelButton.setOnClickListener(view -> cancel());
                                searchButton.setOnClickListener(view -> createSearch());

                                if (Retiree.customSetToString(userSearch.getSkills()).equals("")) {
                                    editSkillsView.setText(R.string.add);
                                }

                                if (Retiree.customSetToString(userSearch.getInterests()).equals("")) {
                                    editInterestsView.setText(R.string.add);
                                }
                            } else {
                                Log.d(TAG, COULD_NOT_RETRIEVE_DATA, task.getException());
                            }
                        });
            }

            if (newSearch) {
                ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
                pageTitle.setText(R.string.create_search);
                searchButton.setText(R.string.search);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void createSearch() {
        if (!hasChanged()) {
            goToSearchResults();
        }

        final String jobDescriptionString = jobDescriptionEditText.getText().toString().trim();
        userSearch.setJobDescription(jobDescriptionString);

        if (validateInputs()) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(SEARCHES)
                    .document(documentId)
                    .update(userSearch.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        goToSearchResults();
                    })

                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        }
    }

    private void goToSearchResults() {
        if (newSearch) {
            final Intent i = new Intent(EditSearchActivity.this, SearchResultsActivity.class);
            i.putExtra(DOCUMENT_ID, documentId);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        finish();
    }

    private void cancel() {
        Log.d(TAG, "OBJECT VALUE: " + jobDescription);
        Log.d(TAG, "EditText VALUE: " + jobDescriptionEditText.getText().toString().trim());
        if (hasChanged()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.discard_changes)
                    .setMessage(R.string.want_to_discard_changes)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            finish();
        }
    }

    private boolean hasChanged() {
        return (!jobDescription.equals(jobDescriptionEditText.getText().toString().trim()));
    }

    private boolean validateInputs() {
        if (jobDescriptionEditText.getText().toString().trim().equals("")) {
            final TextInputLayout jobDescriptionLayout = findViewById(R.id.description_layout);
            jobDescriptionLayout.setError("Please enter a description");
            return false;
        }

        return true;
    }

    private void refresh() {
        String jobDescription = jobDescriptionEditText.getText().toString().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(SEARCHES)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                       userSearch = task.getResult().toObject(Search.class);
                       userSearch.setJobDescription(jobDescription);
                       skillsView.setText(Retiree.customSetToString(userSearch.getSkills()));
                       interestsView.setText(Retiree.customSetToString(userSearch.getInterests()));
                   } else {
                       Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                   }
                });
    }

    @Override
    public void onBackPressed() {
        if (newSearch) {
            super.onBackPressed();
        } else {
            cancel();
        }
    }
}
