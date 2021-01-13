package com.project.fypapp.helper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class FirestoreHelper {
    public static boolean isQueryResultEmpty(Task<QuerySnapshot> task) {
        return !(Objects.requireNonNull(task.getResult()).getDocuments().size() > 0);
    }

    public static boolean isFieldEmpty(Task<com.google.firebase.firestore.QuerySnapshot> task, String field) {
        return Objects.equals(Objects.requireNonNull(task.getResult()).getDocuments().get(0).get(field), "");
    }
}
