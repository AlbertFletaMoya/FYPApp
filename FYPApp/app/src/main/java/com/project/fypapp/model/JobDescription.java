package com.project.fypapp.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class JobDescription {
    private final String companyName;
    private final String position;
    private final String startingDate;
    private final String endingDate;
    private final String jobDescription;

    public JobDescription(){
        companyName = "Amazon";
        position = "Software Engineering Intern";
        startingDate = "01/04/2020";
        endingDate = "09/09/2020";
        jobDescription = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
    }
}
