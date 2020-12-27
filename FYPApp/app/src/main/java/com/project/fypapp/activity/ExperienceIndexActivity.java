package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.adapter.UserExperienceRecyclerAdapter;
import com.project.fypapp.model.UserProfile;

public class ExperienceIndexActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_index);
        initRecyclerView();

        final TextView addExperienceView = findViewById(R.id.add_job_view);
        addExperienceView.setOnClickListener(view -> addExperience());
    }

    private void initRecyclerView(){
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final UserProfile userProfile = new UserProfile();

        UserExperienceRecyclerAdapter userExperienceRecyclerAdapter =
                new UserExperienceRecyclerAdapter(userProfile.getJobs(), (v, position) -> {
                    Intent i = new Intent(ExperienceIndexActivity.this, EditProfileActivity.class);
                    i.putExtra("position", position);
                    startActivity(i);
                });
        recyclerView.setAdapter(userExperienceRecyclerAdapter);
    }

    private void addExperience() {
        Intent i = new Intent(ExperienceIndexActivity.this, EditProfileActivity.class);
        i.putExtra("position", -2);
        startActivity(i);
    }
}
