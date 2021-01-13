package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserTypeActivity extends AppCompatActivity {
    private static final String TAG = "UserTypeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_type);

        final Button entrepreneurButton = findViewById(R.id.entrepreneur_button);
        final Button retiredUserButton = findViewById(R.id.retired_user_button);

        entrepreneurButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("Register")
                .setMessage("Do you really want to register as an Entrepreneur?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> saveEntrepreneur())
                .setNegativeButton(android.R.string.no, null).show());

        retiredUserButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("Register")
                .setMessage("Do you really want to register as a Retiree?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> saveRetiredUser())
                .setNegativeButton(android.R.string.no, null).show());
    }

    private void saveEntrepreneur() {
        // save that the user is an entrepreneur
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> search = new HashMap<>();
        search.put("min_years_of_experience", 0);
        search.put("roles", new ArrayList<String>());
        search.put("sectors", new ArrayList<String>());
        search.put("job_description", "");

        db.collection("searches")
                .add(search)
                .addOnSuccessListener(documentReference -> {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Map<String, Object> user = new HashMap<>();
                    assert firebaseUser != null;
                    user.put("email", firebaseUser.getEmail());
                    user.put("name", firebaseUser.getDisplayName());
                    user.put("search", documentReference.getId());
                    db.collection("entrepreneur_users")
                            .add(user)
                            .addOnSuccessListener(documentReference2 -> {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference2.getId());
                                goToCreateSearch(documentReference.getId());
                            })
                            .addOnFailureListener(e -> Log.d(TAG, "Error adding document", e));
                })

                .addOnFailureListener(e -> Log.d(TAG, "Error adding document", e));



    }

    private void goToCreateSearch(String documentId) {
        Intent i = new Intent(UserTypeActivity.this, EditSearchActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("documentId", documentId);
        i.putExtra("newSearch", true);
        startActivity(i);
        finish();
    }

    private void saveRetiredUser() {
        // save that the user is a retired user
        // TODO later on just save the email and specify name in create profile
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> user = new HashMap<>();
        assert firebaseUser != null;
        user.put("email", firebaseUser.getEmail());
        user.put("name", firebaseUser.getDisplayName());
        user.put("headline", "");
        user.put("location", "");
        user.put("job_experiences", new ArrayList <String>());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("retiree_users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    goToCreateProfile(documentReference.getId());
                })
                .addOnFailureListener(e -> Log.d(TAG, "Error adding document", e));
    }

    private void goToCreateProfile(String documentId) {
        Intent i = new Intent(UserTypeActivity.this, EditProfileActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("documentId", documentId);
        i.putExtra("newUser", true);
        startActivity(i);
        finish();
    }
}
