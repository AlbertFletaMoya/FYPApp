package com.project.fypapp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.project.fypapp.model.Retiree.INTERESTS;
import static com.project.fypapp.model.Retiree.SKILLS;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Search {
    public static final String SEARCHES = "searches";
    public static final String SEARCH = "search";
    public static final String JOB_DESCRIPTION = "jobDescription";

    private String jobDescription;
    private List<String> skills;
    private List<String> interests;

    public Search() {
        jobDescription = "";
        skills = new ArrayList<>();
        interests = new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put(JOB_DESCRIPTION, this.jobDescription);
        searchMap.put(SKILLS, this.skills);
        searchMap.put(INTERESTS, this.interests);
        return searchMap;
    }

    public static List<String> stringToList(String input) {
        return Arrays.asList(Arrays.stream(input.split(","))
                .map(String::trim).toArray(String[]::new));
    }
}

