package com.project.fypapp.model;

import java.util.List;

import lombok.Getter;

@Getter
public class SearchDocument {
    private List<String> roles;
    private List<String> sectors;
    private int min_years_of_experience;
    private String job_description;


    public String getListOfRolesAsString() {
        if (roles.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
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

        StringBuilder sb = new StringBuilder();
        for (String sector : sectors) {
            sb.append(sector).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}

