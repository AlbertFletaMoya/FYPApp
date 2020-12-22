package com.example.fypapp;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {
    private String name;
    private String email;
    private List<JobDescription> jobs;
    private String bio;
    private String region;


}
