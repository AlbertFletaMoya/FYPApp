package com.project.fypapp.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor (access = AccessLevel.PUBLIC)
public class Retiree {
    public static final String RETIREE_USERS = "retiree_users";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String HEADLINE = "headline";
    public static final String LOCATION = "location";

    private final String email;
    private final String name;
    private final String headline;
    private final String location;

    public Retiree() {
        email = "";
        name = "";
        headline = "";
        location = "";
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> retireeMap = new HashMap<>();
        retireeMap.put(EMAIL, this.email);
        retireeMap.put(NAME, this.name);
        retireeMap.put(HEADLINE, this.headline);
        retireeMap.put(LOCATION, this.location);
        return retireeMap;
    }
}
