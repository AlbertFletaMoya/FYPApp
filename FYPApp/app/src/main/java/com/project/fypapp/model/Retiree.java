package com.project.fypapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor (access = AccessLevel.PUBLIC)
public class Retiree {
    public static final String RETIREE_USERS = "retiree_users";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String HEADLINE = "headline";
    public static final String LOCATION = "location";
    public static final String PROFILE_PICTURE_URI = "profilePictureUri";
    public static final String SKILLS = "skills";
    public static final String INTERESTS = "interests";

    private final String email;
    private String firstName;
    private String lastName;
    private String headline;
    private String city;
    private String country;
    private String profilePictureUri;
    private List<String> skills;
    private List<String> interests;

    public Retiree() {
        email = "";
        firstName = "";
        lastName = "";
        headline = "";
        city = "";
        country = "";
        profilePictureUri = "";
        skills = null;
        interests = null;
    }

    public static String getName(Retiree retiree) {
        return retiree.getFirstName() + " " + retiree.getLastName();
    }

    public static String getLocation(Retiree retiree) {
        if (retiree.getCity().equals("")) {
            return retiree.getCountry();
        } else if (retiree.getCountry().equals("")) {
            return retiree.getCity();
        }
        return retiree.getCity() + ", " + retiree.getCountry();
    }

    public static String customSetToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append(", ");
        }

        return sb.substring(0, sb.length()-2);
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> retireeMap = new HashMap<>();
        retireeMap.put(EMAIL, this.email);
        retireeMap.put(FIRST_NAME, this.firstName);
        retireeMap.put(LAST_NAME, this.lastName);
        retireeMap.put(HEADLINE, this.headline);
        retireeMap.put(CITY, this.city);
        retireeMap.put(COUNTRY, this.country);
        retireeMap.put(PROFILE_PICTURE_URI, this.profilePictureUri);
        retireeMap.put(INTERESTS, this.interests);
        retireeMap.put(SKILLS, this.skills);
        return retireeMap;
    }
}
