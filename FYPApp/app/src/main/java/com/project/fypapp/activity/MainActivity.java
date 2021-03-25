package com.project.fypapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.fypapp.R;
import com.project.fypapp.adapter.UserProfileRecyclerAdapter;
import com.project.fypapp.helper.CameraHelper;
import com.project.fypapp.model.JobExperience;
import com.project.fypapp.model.Retiree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.SneakyThrows;

import static com.project.fypapp.model.JobExperience.JOB_EXPERIENCES;
import static com.project.fypapp.model.Retiree.PROFILE_PICTURE_URI;
import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.CHOOSE_FROM_GALLERY;
import static com.project.fypapp.util.Constants.CHOOSE_FROM_GALLERY_REQUEST_CODE;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.LOGOUT_MESSAGE;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.TAKE_A_PHOTO;
import static com.project.fypapp.util.Constants.TAKE_A_PHOTO_REQUEST_CODE;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.USER;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Uri cameraUri = null;
    private String documentId;
    private boolean recyclerReady = false;
    private boolean imageReady = false;
    private boolean profileBelongsToUser;

    private TextView logoutButton;
    private TextView cancelButton;
    private CircleImageView circleImageView;
    private MaterialCardView materialCardView;
    private ProgressBar progressBar;

    private TextView nameView;
    private TextView locationView;
    private TextView bioView;
    private TextView emailView;
    private TextView editExperienceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerReady = false;
        imageReady = true;

        logoutButton = findViewById(R.id.logout_view);
        logoutButton.setOnClickListener(view -> signOut());

        cancelButton = findViewById(R.id.cancel_view);
        cancelButton.setOnClickListener(view -> finish());

        circleImageView = findViewById(R.id.profile_picture_view);

        materialCardView = findViewById(R.id.card_view);
        progressBar = findViewById(R.id.progress_bar);

        nameView = findViewById(R.id.name_view);
        locationView = findViewById(R.id.location_view);
        bioView = findViewById(R.id.bio_view);
        emailView = findViewById(R.id.email_view);
        TextView editProfileView = findViewById(R.id.edit_profile_view);
        editExperienceView = findViewById(R.id.edit_experience_view);

        editProfileView.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, EditProfileIndexActivity.class);
            i.putExtra(DOCUMENT_ID, documentId);
            startActivity(i);
        });

        editExperienceView.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ExperienceIndexActivity.class);
            i.putExtra(DOCUMENT_ID, documentId);
            startActivity(i);
        });

        hideViews();

        if (getIntent().getExtras() != null) {
            documentId = getIntent().getStringExtra(DOCUMENT_ID);
            profileBelongsToUser = getIntent().getBooleanExtra(PROFILE_BELONGS_TO_USER, false);
            if (!profileBelongsToUser) {
                logoutButton.setVisibility(View.INVISIBLE);
                editExperienceView.setVisibility(View.INVISIBLE);
                editProfileView.setVisibility(View.INVISIBLE);
            } else {
                circleImageView.setOnClickListener(view -> profilePictureDialogue());
                cancelButton.setVisibility(View.INVISIBLE);
            }

            getProfile();
            initRecyclerView(documentId);
            setProfilePicture();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getIntent().getExtras() != null) {
            getProfile();
            setProfilePicture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfilePicture();
    }

    private void getProfile() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(RETIREE_USERS).document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                final DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    Log.d(TAG, "DOCUMENT ID: " + documentId);
                    final Retiree retiree = document.toObject(Retiree.class);
                    assert retiree != null;
                    nameView.setText(getString(R.string.full_name, retiree.getFirstName(), retiree.getLastName()));
                    bioView.setText(retiree.getHeadline());
                    emailView.setText(retiree.getEmail());
                    locationView.setText(getString(R.string.full_location, retiree.getCity(),
                            retiree.getCountry()));
                }
            } else {
                    Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
            }
        });
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

    private void initRecyclerView(String retireeId) {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(JOB_EXPERIENCES)
                .whereEqualTo(USER, retireeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final List<JobExperience> experiences = new ArrayList<>();
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            experiences.add(document.toObject(JobExperience.class));
                        }
                        if (experiences.isEmpty()) {
                            editExperienceView.setText(getString(R.string.add));
                        }
                        final UserProfileRecyclerAdapter userProfileRecyclerAdapter =
                                new UserProfileRecyclerAdapter(experiences);

                        recyclerView.setAdapter(userProfileRecyclerAdapter);
                        recyclerReady = true;
                        showViews();
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }

    private void setProfilePicture() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RETIREE_USERS)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        Retiree retiree = Objects.requireNonNull(task.getResult()).toObject(Retiree.class);
                        assert retiree != null;
                        Glide.with(this)
                                .load(Uri.parse(retiree.getProfilePictureUri()))
                                .centerCrop()
                                .placeholder(R.drawable.ic_baseline_person_120)
                                .error(R.drawable.ic_baseline_person_120)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                                        showViews();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                        imageReady = true;
                                        showViews();
                                        return false;
                                    }
                                })
                                .into(circleImageView);
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }

    private void profilePictureDialogue() {
        String[] options = {TAKE_A_PHOTO, CHOOSE_FROM_GALLERY};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_profile_photo);
        builder.setItems(options, (dialog, which) -> {
            if (options[which].equals(TAKE_A_PHOTO)) {
                Intent takePhoto = CameraHelper.getTakePictureIntent(this,
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        getPackageManager());
                assert takePhoto != null;
                cameraUri = (Uri) takePhoto.getExtras().get(MediaStore.EXTRA_OUTPUT);
                Log.d(TAG, "URI is: " + cameraUri);
                startActivityForResult(takePhoto, TAKE_A_PHOTO_REQUEST_CODE);
                imageReady = false;
            } else {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY_REQUEST_CODE);
                imageReady = false;
            }
        });
        builder.show();
    }

    @SneakyThrows
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        progressBar.setVisibility(View.VISIBLE);
        imageReady = false;
        Uri selectedImage = null;
        switch (requestCode) {
            case TAKE_A_PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    selectedImage = cameraUri;
                    Log.d(TAG, "The chosen photo URI is: " + selectedImage);
                }

                break;
            case CHOOSE_FROM_GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "The chosen photo URI is: " + selectedImage);
                }
                break;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profilePictureStorageRef = storage.getReference().child("profilePictures/" + documentId);
        if (selectedImage != null) {
            UploadTask uploadTask = profilePictureStorageRef.putFile(selectedImage);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return profilePictureStorageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "Download URI is: " + downloadUri);
                    Map<String, Object> retiree = new HashMap<>();
                    assert downloadUri != null;
                    retiree.put(PROFILE_PICTURE_URI, downloadUri.toString());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(RETIREE_USERS)
                            .document(documentId)
                            .update(retiree)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, SUCCESSFULLY_UPDATED);
                                setProfilePicture();
                            })
                            .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
                } else {
                    Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                }
            });
        }
    }

    private void hideViews() {
        progressBar.setVisibility(View.GONE);
        materialCardView.setVisibility(View.GONE);
        logoutButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        circleImageView.setVisibility(View.INVISIBLE);
    }

    private void showViews() {
        Log.d(TAG, "Recycler is " + recyclerReady + " and image is " + imageReady);
        if (recyclerReady && imageReady) {
            materialCardView.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            circleImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (!profileBelongsToUser) {
                cancelButton.setVisibility(View.VISIBLE);
            }
        }
    }
}