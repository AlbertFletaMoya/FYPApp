package com.project.fypapp.model;

import android.os.Parcel;
import android.os.Parcelable;

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

    public JobDescription() {
        companyName = "Amazon";
        position = "Software Engineering Intern";
        startingDate = "Apr 2020";
        endingDate = "Sep 2020";
        jobDescription = "JULY 2018 – AUGUST 2018\n" +
                "SUMMER ENGINEERING INTERN, ELECTRICITAT INTEGRAL GENERAL, Ltd.\n" +
                "Assisted in:\n" +
                "• Schematics design of electrical distribution panels (EPLAN, AutoCAD)\n" +
                "• A legalization project of sets of electrical distribution panels at a manufacturing site\n" +
                "\n" +
                "Internship outcome:\n" +
                "• Good understanding of electrical distribution panels for industrial purposes\n" +
                "• Good understanding and operation of EPLAN and AutoCAD to design electrical schematics\n" +
                "• Good understanding of the process and the calculations required to submit and get the authorities approval for a set of industrial electrical distribution panels";
    }
}
