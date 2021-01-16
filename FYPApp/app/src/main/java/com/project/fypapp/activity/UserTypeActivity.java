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
import com.project.fypapp.model.Entrepreneur;
import com.project.fypapp.model.Retiree;
import com.project.fypapp.model.Search;

import java.util.ArrayList;

import static com.project.fypapp.model.Entrepreneur.ENTREPRENEUR_USERS;
import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.model.Search.SEARCHES;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.ERROR_ADDING_DOCUMENT;
import static com.project.fypapp.util.Constants.NEW_SEARCH;
import static com.project.fypapp.util.Constants.NEW_USER;
import static com.project.fypapp.util.Constants.addedSuccessfully;

public class UserTypeActivity extends AppCompatActivity {
    private static final String TAG = "UserTypeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_type);

        final Button entrepreneurButton = findViewById(R.id.entrepreneur_button);
        final Button retiredUserButton = findViewById(R.id.retired_user_button);

        entrepreneurButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.register)
                .setMessage(R.string.register_as_entrepreneur)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> saveEntrepreneur())
                .setNegativeButton(android.R.string.no, null).show());

        retiredUserButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.register)
                .setMessage(R.string.register_as_retiree)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> saveRetiredUser())
                .setNegativeButton(android.R.string.no, null).show());
    }

    private void saveEntrepreneur() {
        final Search search = new Search(new ArrayList<>(), new ArrayList<>(),
                0, "");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(SEARCHES)
                .add(search)
                .addOnSuccessListener(documentReference -> {
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert firebaseUser != null;
                    final Entrepreneur entrepreneur = new Entrepreneur(firebaseUser.getEmail(),
                            documentReference.getId());

                    db.collection(ENTREPRENEUR_USERS)
                            .add(entrepreneur)
                            .addOnSuccessListener(documentReference2 -> {
                                Log.d(TAG, addedSuccessfully(documentReference2.getId()));
                                goToCreateSearch(documentReference.getId());
                            })
                            .addOnFailureListener(e -> Log.d(TAG, ERROR_ADDING_DOCUMENT, e));
                })

                .addOnFailureListener(e -> Log.d(TAG, ERROR_ADDING_DOCUMENT, e));
    }


    private void saveRetiredUser() {
        // save that the user is a retired user
        // TODO later on just save the email and specify name in create profile
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        final Retiree retiree = new Retiree(firebaseUser.getEmail(), "", "", "",
                "", "", "");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RETIREE_USERS)
                .add(retiree)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, addedSuccessfully(documentReference.getId()));
                    goToCreateProfile(documentReference.getId());
                })
                .addOnFailureListener(e -> Log.d(TAG, ERROR_ADDING_DOCUMENT, e));
    }

    private void goToCreateSearch(String documentId) {
        final Intent i = new Intent(UserTypeActivity.this, EditSearchActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DOCUMENT_ID, documentId);
        i.putExtra(NEW_SEARCH, true);
        startActivity(i);
        finish();
    }

    private void goToCreateProfile(String documentId) {
        final Intent i = new Intent(UserTypeActivity.this, EditProfileActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DOCUMENT_ID, documentId);
        i.putExtra(NEW_USER, true);
        startActivity(i);
        finish();
    }
}
