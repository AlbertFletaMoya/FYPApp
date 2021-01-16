package com.project.fypapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.fypapp.R;
import com.project.fypapp.helper.CameraHelper;
import com.project.fypapp.model.Retiree;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.SneakyThrows;

import static com.project.fypapp.model.Retiree.CITY;
import static com.project.fypapp.model.Retiree.COUNTRY;
import static com.project.fypapp.model.Retiree.FIRST_NAME;
import static com.project.fypapp.model.Retiree.HEADLINE;
import static com.project.fypapp.model.Retiree.LAST_NAME;
import static com.project.fypapp.model.Retiree.PROFILE_PICTURE_URI;
import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.CHOOSE_FROM_GALLERY;
import static com.project.fypapp.util.Constants.CHOOSE_FROM_GALLERY_REQUEST_CODE;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.NEW_USER;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.TAKE_A_PHOTO;
import static com.project.fypapp.util.Constants.TAKE_A_PHOTO_REQUEST_CODE;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private String documentId;
    private Uri cameraUri;
    private String profilePictureUri = "";
    Uri selectedImage = null;

    private EditText firstNameView;
    private EditText lastNameView;
    private EditText headlineView;
    private EditText cityView;
    private EditText countryView;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        circleImageView = findViewById(R.id.profile_picture_view);
        circleImageView.setOnClickListener(view -> profilePictureDialogue());

        final TextView editProfilePhotoView = findViewById(R.id.edit_profile_photo_view);
        editProfilePhotoView.setOnClickListener(view -> profilePictureDialogue());

        final TextView saveButton = findViewById(R.id.save_view);
        final TextView cancelButton = findViewById(R.id.cancel_view);

        firstNameView = findViewById(R.id.first_name_write_view);
        lastNameView = findViewById(R.id.last_name_write_view);
        headlineView = findViewById(R.id.headline_write_view);
        cityView = findViewById(R.id.city_write_view);
        countryView = findViewById(R.id.country_write_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {
            documentId = getIntent().getStringExtra(DOCUMENT_ID);
            final boolean newUser = getIntent().getBooleanExtra(NEW_USER, false);

            if (!newUser) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                assert documentId != null;
                db.collection(RETIREE_USERS)
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                Retiree retiree = Objects.requireNonNull(task.getResult()).toObject(Retiree.class);
                                assert retiree != null;
                                firstNameView.setText(retiree.getFirstName());
                                lastNameView.setText(retiree.getLastName());
                                headlineView.setText(retiree.getHeadline());
                                cityView.setText(retiree.getCity());
                                countryView.setText(retiree.getCountry());
                            } else {
                                Log.d(TAG, COULD_NOT_RETRIEVE_DATA, task.getException());
                            }
                        });

                cancelButton.setOnClickListener(view -> cancel(firstNameView.getText().toString().trim(),
                        lastNameView.getText().toString().trim(),
                        headlineView.getText().toString().trim(),
                        cityView.getText().toString().trim(),
                        countryView.getText().toString().trim(),
                        documentId, newUser));

                setProfilePicture();
            }

            else {
                final TextView activityTitle = findViewById(R.id.page_title_view);
                activityTitle.setText(R.string.create_profile);
                ((ViewManager)cancelButton.getParent()).removeView(cancelButton);
            }

            saveButton.setOnClickListener(view -> saveProfile(documentId, newUser));
        }
    }

    private void saveProfile(String documentId, boolean newUser) {
        progressBar.setVisibility(View.VISIBLE);
        final String firstName = firstNameView.getText().toString().trim();
        final String lastName = lastNameView.getText().toString().trim();
        final String headline = headlineView.getText().toString().trim();
        final String city = cityView.getText().toString().trim();
        final String country = countryView.getText().toString().trim();

        if (validateFields()) {
            if (profilePictureUri.equals("")) {
                final Map<String, Object> retireeMap = new HashMap<>();
                retireeMap.put(FIRST_NAME, firstName);
                retireeMap.put(LAST_NAME, lastName);
                retireeMap.put(HEADLINE, headline);
                retireeMap.put(CITY, city);
                retireeMap.put(COUNTRY, country);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(RETIREE_USERS)
                        .document(documentId)
                        .update(retireeMap)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, SUCCESSFULLY_UPDATED);
                            goToMain(true, documentId);
                        })
                        .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
            } else {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference profilePictureStorageRef = storage.getReference().child("profilePictures/" + documentId);
                UploadTask uploadTask = profilePictureStorageRef.putFile(selectedImage);
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return profilePictureStorageRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d(TAG, "Download URI is: " + downloadUri);
                        final Map<String, Object> retireeMap = new HashMap<>();
                        retireeMap.put(FIRST_NAME, firstName);
                        retireeMap.put(LAST_NAME, lastName);
                        retireeMap.put(HEADLINE, headline);
                        retireeMap.put(CITY, city);
                        retireeMap.put(COUNTRY, country);
                        retireeMap.put(PROFILE_PICTURE_URI, downloadUri.toString());

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection(RETIREE_USERS)
                                .document(documentId)
                                .update(retireeMap)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, SUCCESSFULLY_UPDATED);
                                    goToMain(true, documentId);
                                })
                                .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
            }
        }
    }

    private void goToMain(boolean newUser, String documentId) {
        if (newUser) {
            Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
            i.putExtra(PROFILE_BELONGS_TO_USER, true);
            i.putExtra(DOCUMENT_ID, documentId);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        finish();
    }

    private void cancel(String firstName, String lastName, String headline, String city,
                        String country, String documentId, boolean newUser) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RETIREE_USERS)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final Retiree retiree = Objects.requireNonNull(task.getResult()).toObject(Retiree.class);
                        assert retiree != null;
                        if (!retiree.getFirstName().trim().equals(firstName)
                                || !retiree.getLastName().trim().equals(lastName)
                                || !retiree.getHeadline().trim().equals(headline)
                                || !retiree.getCity().trim().equals(city)
                                || !retiree.getCountry().trim().equals(country)
                                || !profilePictureUri.equals("")) {

                            new AlertDialog.Builder(this)
                                    .setTitle(R.string.discard_changes)
                                    .setMessage(R.string.want_to_discard_changes)
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> goToMain(false, ""))
                                    .setNegativeButton(android.R.string.no, null).show();
                        }

                        else {
                            goToMain(false, "");
                        }
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }


    private boolean validateFields() {
        boolean valid = true;

        final TextInputLayout firstNameLayout = findViewById(R.id.first_name_layout);
        final TextInputLayout lastNameLayout = findViewById(R.id.last_name_layout);
        final TextInputLayout headlineLayout = findViewById(R.id.headline_layout);
        final TextInputLayout cityLayout = findViewById(R.id.city_layout);
        final TextInputLayout countryLayout = findViewById(R.id.country_layout);

        if (firstNameView.getText().toString().trim().equals("")) {
            firstNameLayout.setError("Please enter your first name");
            valid = false;
        }

        if (lastNameView.getText().toString().trim().equals("")) {
            lastNameLayout.setError("Please enter your last name");
            valid = false;
        }

        if (headlineView.getText().toString().trim().equals("")) {
            headlineLayout.setError("Please enter a profile headline");
            valid = false;
        }

        if (cityView.getText().toString().trim().equals("")) {
            cityLayout.setError("Please enter your current city");
            valid = false;
        }

        if (countryView.getText().toString().trim().equals("")) {
            countryLayout.setError("Please enter your current country");
            valid = false;
        }

        return valid;
    }

    private void setProfilePicture() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RETIREE_USERS)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        Retiree retiree = task.getResult().toObject(Retiree.class);
                        if (!retiree.getProfilePictureUri().equals("")) {
                            Glide.with(this)
                                    .load(Uri.parse(retiree.getProfilePictureUri()))
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_baseline_person_120)
                                    .error(R.drawable.ic_baseline_person_120)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                            return false;
                                        }
                                    })
                                    .into(circleImageView);
                        }
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }

    private void profilePictureDialogue() {
        String[] options = {TAKE_A_PHOTO, CHOOSE_FROM_GALLERY};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.profile_photo);
        builder.setItems(options, (dialog, which) -> {
            if (options[which].equals(TAKE_A_PHOTO)) {
                Intent takePhoto = CameraHelper.getTakePictureIntent(this,
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        getPackageManager());
                cameraUri = (Uri) takePhoto.getExtras().get(MediaStore.EXTRA_OUTPUT);
                Log.d(TAG, "URI is: " + cameraUri);
                startActivityForResult(takePhoto, TAKE_A_PHOTO_REQUEST_CODE);
            } else {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY_REQUEST_CODE);
            }
        });
        builder.show();
    }

    @SneakyThrows
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case TAKE_A_PHOTO_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    selectedImage = cameraUri;
                    Log.d(TAG, "The chosen photo URI is: " + selectedImage);
                }

                break;
            case CHOOSE_FROM_GALLERY_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "The chosen photo URI is: " + selectedImage);
                }
                break;
        }

        profilePictureUri = selectedImage.toString();
        circleImageView.setImageURI(selectedImage);
    }
}
