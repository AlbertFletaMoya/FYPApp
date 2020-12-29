package com.project.fypapp.model;

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
}
