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

}
