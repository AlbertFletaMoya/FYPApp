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
public class Skills {
    public static final String SKILL_LIST = "skillLIST";
    private static final String USER = "user";

    private final List<String> skillList;
    private final String user;

    public Skills() {
        this.skillList = new ArrayList<>();
        user = "";
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put(SKILL_LIST, this.skillList);
        map.put(USER, this.user);
        return map;
    }
}
