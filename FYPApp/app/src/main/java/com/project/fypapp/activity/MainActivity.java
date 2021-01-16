package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.adapter.UserProfileRecyclerAdapter;
import com.project.fypapp.model.JobExperience;
import com.project.fypapp.model.Retiree;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.project.fypapp.model.JobExperience.JOB_EXPERIENCES;
import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.CHOOSE_FROM_GALLERY;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_DOES_NOT_EXIST;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.LOGOUT_MESSAGE;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.TAKE_A_PHOTO;
import static com.project.fypapp.util.Constants.USER;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView logoutButton = findViewById(R.id.logout_view);
        logoutButton.setOnClickListener(view -> signOut());

        final TextView cancelButton = findViewById(R.id.cancel_view);
        cancelButton.setOnClickListener(view -> finish());

        final CircleImageView circleImageView = findViewById(R.id.profile_picture_view);
        circleImageView.setOnClickListener(view -> profilePictureDialogue());

        if (getIntent().getExtras() != null) {
            final String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            final boolean profileBelongsToUser = getIntent().getBooleanExtra(PROFILE_BELONGS_TO_USER, false);
            if (!profileBelongsToUser) {
                ((ViewGroup) logoutButton.getParent()).removeView(logoutButton);
            } else {
                ((ViewGroup) cancelButton.getParent()).removeView(cancelButton);
            }

            initRecyclerView(profileBelongsToUser, documentId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getExtras() != null) {
            final String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            final boolean profileBelongsToUser = getIntent().getBooleanExtra(PROFILE_BELONGS_TO_USER, false);
            initRecyclerView(profileBelongsToUser, documentId);
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Toast.makeText(getApplicationContext(), LOGOUT_MESSAGE, Toast.LENGTH_LONG).show();
                    goToLogIn();
                });
    }

    private void goToLogIn() {
        final Intent intent = new Intent(MainActivity.this, FirebaseUIActivity.class);
        startActivity(intent);
        finish();
    }

    private void initRecyclerView(boolean profileBelongsToUser, String documentId) {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(RETIREE_USERS).document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                final DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    final Retiree retiree = document.toObject(Retiree.class);
                    Log.d(TAG, "DOCUMENT ID: " + documentId);

                    db.collection(JOB_EXPERIENCES)
                            .whereEqualTo(USER, documentId)
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                    final List<JobExperience> experiences = new ArrayList<>();
                                    for (DocumentSnapshot document1 : task1.getResult().getDocuments()) {
                                        experiences.add(document1.toObject(JobExperience.class));
                                    }
                                    final UserProfileRecyclerAdapter userProfileRecyclerAdapter =
                                            new UserProfileRecyclerAdapter(retiree, experiences, new UserProfileRecyclerAdapter.UserProfileRecyclerAdapterListener() {
                                                @Override
                                                public void editProfileOnClick(View v, int position) {
                                                    final Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
                                                    i.putExtra(DOCUMENT_ID, documentId);
                                                    startActivity(i);
                                                }

                                                @Override
                                                public void editExperienceOnClick(View v, int position) {
                                                    final Intent i = new Intent(MainActivity.this, ExperienceIndexActivity.class);
                                                    i.putExtra(DOCUMENT_ID, documentId);
                                                    startActivity(i);
                                                }
                                            }, profileBelongsToUser);
                                    recyclerView.setAdapter(userProfileRecyclerAdapter);
                                } else {
                                    Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                                }
                            });

                } else {
                    Log.d(TAG, DOCUMENT_DOES_NOT_EXIST);
                }
            } else {
                Log.d(TAG, COULD_NOT_RETRIEVE_DATA, task.getException());
            }
        });
    }

    private void profilePictureDialogue() {
        String[] options = {TAKE_A_PHOTO, CHOOSE_FROM_GALLERY};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.profile_photo);
        builder.setItems(options, (dialog, which) -> {
                // the user clicked on colors[which]
        });
        builder.show();
    }

}