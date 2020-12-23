package com.project.fypapp.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobDescription {
    private String companyName;
    private String position;
    private String startingDate;
    private String endingDate;
    private String jobDescription;
}
