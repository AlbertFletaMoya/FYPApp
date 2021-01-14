package com.project.fypapp.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class JobExperience {
    public static final String JOB_EXPERIENCES = "job_experiences";
    public static final String COMPANY = "company";
    public static final String POSITION = "position";
    public static final String STARTING_DATE = "startingDate";
    public static final String ENDING_DATE = "endingDate";
    public static final String JOB_DESCRIPTION = "jobDescription";
    public static final String SECTOR = "sector";
    private static final String USER = "user";

    private final String company;
    private final String position;
    private final String startingDate;
    private final String endingDate;
    private final String jobDescription;
    private final String sector;
    private final String user;

    public JobExperience() {
        company = "";
        position = "";
        startingDate = "";
        endingDate = "";
        jobDescription = "";
        sector = "";
        user = "";
    }


    public Map<String, Object> toMap() {
        Map<String, Object> jobExperienceMap = new HashMap<>();
        jobExperienceMap.put(COMPANY, this.company);
        jobExperienceMap.put(POSITION, this.position);
        jobExperienceMap.put(STARTING_DATE, this.startingDate);
        jobExperienceMap.put(ENDING_DATE, this.endingDate);
        jobExperienceMap.put(JOB_DESCRIPTION, this.jobDescription);
        jobExperienceMap.put(SECTOR, this.sector);
        jobExperienceMap.put(USER, this.user);
        return jobExperienceMap;
    }
}
