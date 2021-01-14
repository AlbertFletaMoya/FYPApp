package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.adapter.UserExperienceRecyclerAdapter;
import com.project.fypapp.model.JobExperience;

import java.util.ArrayList;
import java.util.List;

import static com.project.fypapp.model.JobExperience.JOB_EXPERIENCES;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.ERROR_ADDING_DOCUMENT;
import static com.project.fypapp.util.Constants.NEW_EXPERIENCE;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.USER;
import static com.project.fypapp.util.Constants.USER_ID;
import static com.project.fypapp.util.Constants.addedSuccessfully;

public class ExperienceIndexActivity extends AppCompatActivity {
    private static final String TAG = "ExperienceIndexActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_index);

        if (getIntent().getExtras() != null) {
            final String documentId = getIntent().getStringExtra(DOCUMENT_ID);

            final ConstraintLayout addExperienceView = findViewById(R.id.add_job_layout);
            addExperienceView.setOnClickListener(view -> addExperience(documentId));

            final TextView cancelView = findViewById(R.id.cancel_view);
            cancelView.setOnClickListener(view -> cancel(documentId));

            initRecyclerView(documentId);
        }
    }

    private void initRecyclerView(String documentId) {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(JOB_EXPERIENCES)
                .whereEqualTo(USER, documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final List<JobExperience> experiences = new ArrayList<>();
                        final List<String> experienceIds = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            experiences.add(document.toObject(JobExperience.class));
                            experienceIds.add(document.getId());
                        }
                        UserExperienceRecyclerAdapter userExperienceRecyclerAdapter =
                                new UserExperienceRecyclerAdapter(experiences, (v, position) -> {
                                    final Intent i = new Intent(ExperienceIndexActivity.this, EditJobExperienceActivity.class);
                                    i.putExtra(USER_ID, documentId);
                                    i.putExtra(DOCUMENT_ID, experienceIds.get(position));
                                    startActivity(i);
                                });
                        recyclerView.setAdapter(userExperienceRecyclerAdapter);
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }

    private void addExperience(String documentId) {
        Intent i = new Intent(ExperienceIndexActivity.this, EditJobExperienceActivity.class);
        i.putExtra(USER_ID, documentId);
        i.putExtra(NEW_EXPERIENCE, true);
        startActivity(i);
    }

    private void cancel(String documentId) {
        Intent i = new Intent(ExperienceIndexActivity.this, MainActivity.class);
        i.putExtra(PROFILE_BELONGS_TO_USER, true);
        i.putExtra(DOCUMENT_ID, documentId);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
