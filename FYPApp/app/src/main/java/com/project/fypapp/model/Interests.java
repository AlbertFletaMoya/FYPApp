package com.project.fypapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class Interests {
    public static final String INTEREST_LIST = "interestList";
    private static final String USER = "user";

    private final List<String> interestList;
    private final String user;

    public Interests() {
        this.interestList = new ArrayList<>();
        user = "";
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put(INTEREST_LIST, this.interestList);
        map.put(USER, this.user);
        return map;
    }
}
