package com.project.fypapp.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor (access = AccessLevel.PUBLIC)
@Getter
public class Entrepreneur {
    public static final String ENTREPRENEUR_USERS = "entrepreneur_users";

    public Entrepreneur() {
        email = "";
        search = "";
    }

    private final String email;
    private final String search;
}
