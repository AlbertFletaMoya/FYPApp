package com.project.fypapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.fypapp.R;
import com.project.fypapp.helper.CameraHelper;
import com.project.fypapp.model.Retiree;

import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.SneakyThrows;

import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.CHOOSE_FROM_GALLERY;
import static com.project.fypapp.util.Constants.CHOOSE_FROM_GALLERY_REQUEST_CODE;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.IS_REGISTRATION;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.TAKE_A_PHOTO;
import static com.project.fypapp.util.Constants.TAKE_A_PHOTO_REQUEST_CODE;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.successfullySaved;

public class EditProfilePhotoActivity extends AppCompatActivity {
    private static final String TAG = "EditProfilePhotoActivity";

    private Uri cameraUri;
    private String profilePictureUri = "";
    Uri selectedImage = null;
    private ProgressBar progressBar;
    private boolean isRegistration;

    private CircleImageView circleImageView;
    private Button noButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        circleImageView = findViewById(R.id.profile_picture_view);

        TextView saveButton = findViewById(R.id.save_view);
        TextView cancelButton = findViewById(R.id.cancel_view);

        ConstraintLayout layout = findViewById(R.id.top_bar);
        Button nextButton = findViewById(R.id.yes_button);
        Button skipButton = findViewById(R.id.skip_button);
        noButton = findViewById(R.id.no_button);
        TextView textView = findViewById(R.id.text_view);

        noButton.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {
            isRegistration = getIntent().getBooleanExtra(IS_REGISTRATION, false);
            if (isRegistration) {
                layout.setVisibility(View.GONE);
            }

            else {
                nextButton.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                skipButton.setVisibility(View.GONE);
                noButton.setVisibility(View.GONE);
            }

            String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            assert documentId != null;
            db.collection(RETIREE_USERS)
                    .document(documentId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                            final DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                Log.d(TAG, "DOCUMENT ID: " + documentId);
                                final Retiree retiree = document.toObject(Retiree.class);
                                assert retiree != null;
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
                                circleImageView.setOnClickListener(view -> profilePictureDialogue());

                                final TextView editProfilePhotoView = findViewById(R.id.edit_profile_photo_view);
                                editProfilePhotoView.setOnClickListener(view -> profilePictureDialogue());
                                cancelButton.setOnClickListener(view ->
                                        cancel());
                                saveButton.setOnClickListener(view -> savePhoto(documentId, retiree, isRegistration));
                                nextButton.setOnClickListener(view -> savePhoto(documentId, retiree, isRegistration));
                                skipButton.setOnClickListener(view -> goToNext(documentId));
                                noButton.setOnClickListener(view -> repeatPhoto());
                            }
                        }

                        else {
                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                        }
                    });
        }
    }

    private void cancel() {
        if (!hasChanged()) {
            finish();
        }

        else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.discard_changes)
                    .setMessage(R.string.want_to_discard_changes)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    private boolean hasChanged() {
        return (!profilePictureUri.equals(""));
    }

    private void savePhoto(String documentId, Retiree retiree, boolean isRegistration) {
        if (!hasChanged()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(DOCUMENT_ID, documentId);
            intent.putExtra(PROFILE_BELONGS_TO_USER, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        else {

            if (selectedImage == null) {
                goToNext(documentId);
            }
            progressBar.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference profilePictureStorageRef = storage.getReference().child("profilePictures/" + documentId);
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
                    assert downloadUri != null;
                    retiree.setProfilePictureUri(downloadUri.toString());
                    Map<String, Object> retireeMap = retiree.toMap();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(RETIREE_USERS)
                            .document(documentId)
                            .update(retireeMap)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, SUCCESSFULLY_UPDATED);
                                if (isRegistration) {
                                    goToNext(documentId);
                                }
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra(DOCUMENT_ID, documentId);
                                intent.putExtra(PROFILE_BELONGS_TO_USER, true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                successfullySaved(this);
                            })
                            .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
                } else {
                    Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                }
            });
        }
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
                assert takePhoto != null;
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
        if (isRegistration) {
            noButton.setVisibility(View.VISIBLE);
        }
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
        if (selectedImage != null) {
            profilePictureUri = selectedImage.toString();
            circleImageView.setImageURI(selectedImage);
        }
    }

    private void goToNext(String documentId) {
        Intent i = new Intent(EditProfilePhotoActivity.this, MainActivity.class);
        i.putExtra(PROFILE_BELONGS_TO_USER, true);
        i.putExtra(DOCUMENT_ID, documentId);
        startActivity(i);
    }

    private void repeatPhoto() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.discard_changes)
                .setMessage(R.string.want_to_discard_changes)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    selectedImage = null;
                    circleImageView.setImageResource(R.drawable.ic_baseline_person_120);
                    noButton.setVisibility(View.GONE);
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onBackPressed() {
        if (isRegistration) {
            super.onBackPressed();
        } else {
            cancel();
        }
    }
}
