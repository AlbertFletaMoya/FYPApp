package com.project.fypapp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Search {
    public static final String SEARCH = "search";
    public static final String SEARCHES = "searches";
    public static final String ROLES = "roles";
    public static final String SECTORS = "sectors";
    public static final String MIN_YEARS_OF_EXPERIENCE = "minYearsOfExperience";
    public static final String JOB_DESCRIPTION = "jobDescription";

    private final List<String> roles;
    private final List<String> sectors;
    private final int minYearsOfExperience;
    private final String jobDescription;

    public Search() {
        roles = new ArrayList<>();
        sectors = new ArrayList<>();
        minYearsOfExperience = 0;
        jobDescription = "";
    }


    public String getListOfRolesAsString() {
        if (roles.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (String role : roles) {
            sb.append(role).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public String getListOfSectorsAsString() {
        if (sectors.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (String sector : sectors) {
            sb.append(sector).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put(ROLES, this.roles);
        searchMap.put(SECTORS, this.sectors);
        searchMap.put(MIN_YEARS_OF_EXPERIENCE, this.minYearsOfExperience);
        searchMap.put(JOB_DESCRIPTION, this.jobDescription);
        return searchMap;
    }

    public static List<String> stringToList(String input) {
        return Arrays.asList(Arrays.stream(input.split(","))
                .map(String::trim).toArray(String[]::new));
    }
}

