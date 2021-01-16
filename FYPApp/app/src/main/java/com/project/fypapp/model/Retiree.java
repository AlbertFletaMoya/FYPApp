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
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String HEADLINE = "headline";
    public static final String LOCATION = "location";
    public static final String PROFILE_PICTURE_URI = "profilePictureUri";

    private final String email;
    private final String firstName;
    private final String lastName;
    private final String headline;
    private final String city;
    private final String country;
    private final String profilePictureUri;

    public Retiree() {
        email = "";
        firstName = "";
        lastName = "";
        headline = "";
        city = "";
        country = "";
        profilePictureUri = "";
    }

    public static String getName(Retiree retiree) {
        return retiree.getFirstName() + " " + retiree.getLastName();
    }

    public static String getLocation(Retiree retiree) {
        return retiree.getCity() + ", " + retiree.getCountry();
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> retireeMap = new HashMap<>();
        retireeMap.put(EMAIL, this.email);
        retireeMap.put(FIRST_NAME, this.firstName);
        retireeMap.put(HEADLINE, this.headline);
        retireeMap.put(CITY, this.city);
        retireeMap.put(COUNTRY, this.country);
        retireeMap.put(PROFILE_PICTURE_URI, this.profilePictureUri);
        return retireeMap;
    }
}