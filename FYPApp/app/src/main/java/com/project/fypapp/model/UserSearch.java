package com.project.fypapp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class UserSearch {
    private final List<String> listOfRoles;
    private final List<String> listOfSectors;
    private final int minYearsOfExperience;
    private final String jobDescription;

    public UserSearch() {
        jobDescription = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        minYearsOfExperience = 10;
        listOfRoles = new ArrayList<>(Arrays.asList("Software Engineer", "Technical Manager"));
        listOfSectors = new ArrayList<>(Arrays.asList("Tech", "Finance", "Health"));
    }
}
