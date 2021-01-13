package com.project.fypapp.model;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class UserProfile {
    private final String name;
    private final String email;
    private final List<JobDescription> jobs;
    private final String bio;
    private final String region;

    public UserProfile() {
        name = "Albert Fleta";
        email = "leinadfc01@gmail.com";
        region = "London, England, UK";
        bio = "Former SDE Intern at Amazon and final-year Electronic Engineering student at Imperial College London";
        jobs = new ArrayList<>();
        for (int i = 0; i<3; i++){
            jobs.add(new JobDescription());
        }
    }

    public UserProfile(DocumentSnapshot document) {
        name = document.getString("name");
        email = document.getString("email");
        region = document.getString("location");
        bio = document.getString("headline");
        jobs = new ArrayList<>(); // replace this once the constructor for jobs is done
        for (int i = 0; i<3; i++){
            jobs.add(new JobDescription());
        }
    }
}
