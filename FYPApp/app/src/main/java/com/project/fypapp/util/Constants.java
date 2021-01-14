package com.project.fypapp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {
    public static final String BACK_BUTTON_ERROR_MESSAGE = "Do not press the back button while you're being authenticated";
    public static final String UNKNOWN_ERROR_MESSAGE = "Please try again";
    public static final String NO_NETWORK_ERROR_MESSAGE = "You have no network connection";

    public static final String LOGOUT_MESSAGE = "Successfully signed out";
    public static final String DOCUMENT_ID = "documentId";
    public static final String NEW_USER = "newUser";
    public static final String NEW_SEARCH = "newSearch";
    public static final String NEW_EXPERIENCE = "newExperience";
    public static final String EMAIL = "email";
    public static final String PROFILE_BELONGS_TO_USER = "profileBelongsToUser";
    public static final String USER_ID = "userId";
    public static final String USER = "user";

    public static final int ADD_NEW_EXPERIENCE = -1;

    public static final Map<Integer, String> MONTH_MAP = Stream.of(new Object[][] {
            {1, "Jan"},
            {2, "Feb"},
            {3, "Mar"},
            {4, "Apr"},
            {5, "May"},
            {6, "Jun"},
            {7, "Jul"},
            {8, "Aug"},
            {9, "Sep"},
            {10, "Oct"},
            {11, "Nov"},
            {12, "Dec"},
    }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (String) data[1]));

    public static String addedSuccessfully(String id) {
        return String.format("DocumentSnapshot added with ID: %s", id);
    }

    public static final String SUCCESSFULLY_RETRIEVED_DATA = "Successfully retrieve object";
    public static final String ERROR_ADDING_DOCUMENT = "Error adding document";
    public static final String COULD_NOT_RETRIEVE_DATA = "Could not retrieve data from database";
    public static final String SUCCESSFULLY_UPDATED = "Object successfully updated";
    public static final String UNSUCCESSFULLY_UPDATED = "Object could not be updated";
    public static final String SUCCESSFULLY_DELETED = "DocumentSnapshot successfully deleted";
    public static final String UNSUCCESSFULLY_DELETED = "Could not delete DocumentSnapshot";
    public static final String DOCUMENT_DOES_NOT_EXIST = "No such document";
}
