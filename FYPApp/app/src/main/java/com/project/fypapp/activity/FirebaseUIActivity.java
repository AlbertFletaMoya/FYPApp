package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.firebase.ui.auth.ErrorCodes.NO_NETWORK;
import static com.project.fypapp.helper.FirestoreHelper.isFieldEmpty;
import static com.project.fypapp.helper.FirestoreHelper.isQueryResultEmpty;
import static com.project.fypapp.model.Entrepreneur.ENTREPRENEUR_USERS;
import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.model.Search.SEARCH;
import static com.project.fypapp.model.Search.SEARCHES;
import static com.project.fypapp.util.Constants.BACK_BUTTON_ERROR_MESSAGE;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.EMAIL;
import static com.project.fypapp.util.Constants.NEW_SEARCH;
import static com.project.fypapp.util.Constants.NEW_USER;
import static com.project.fypapp.util.Constants.NO_NETWORK_ERROR_MESSAGE;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.UNKNOWN_ERROR_MESSAGE;

public class FirebaseUIActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 123;
    private static final String TAG = "FirebaseUIActivity";
    private static final String USER_IS_LOGGED_IN = "User is logged in";
    private static final String USER_IS_NULL = "User is null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, USER_IS_LOGGED_IN);
            proceedAfterLogin(user);
        }

        else{
            createSignInIntent();
        }

    }

    private void createSignInIntent(){
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        final AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.picker_login)
                .setGoogleButtonId(R.id.google_button)
                .setEmailButtonId(R.id.email_button)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setAuthMethodPickerLayout(customLayout)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            final IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    proceedAfterLogin(FirebaseAuth.getInstance().getCurrentUser());
                } else {
                    Log.d(TAG, USER_IS_NULL);
                }
            }

            else {
                if (response == null || response.getError() == null){
                    Toast.makeText(getApplicationContext(), BACK_BUTTON_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                    createSignInIntent();
                }

                else if (response.getError().getErrorCode() == NO_NETWORK){
                    Toast.makeText(getApplicationContext(), NO_NETWORK_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                }

                else {
                    Toast.makeText(getApplicationContext(), UNKNOWN_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void proceedAfterLogin(FirebaseUser firebaseUser) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if user is a retiree if it is create a new profile or go to main page depending if it's a new retiree
        db.collection(RETIREE_USERS)
                .whereEqualTo(EMAIL, firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        if (!isQueryResultEmpty(task)) {
                            if (!isFieldEmpty(task, "headline")) {
                                goToMainPage(Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId());
                            } else {
                                goToCreateProfile(Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId());
                            }
                        }

                        else {
                            db.collection(ENTREPRENEUR_USERS)
                                    .whereEqualTo(EMAIL, firebaseUser.getEmail())
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                            if (!isQueryResultEmpty(task2)) {
                                                db.collection(SEARCHES)
                                                        .document(Objects.requireNonNull(Objects.requireNonNull(task2.getResult()).getDocuments().get(0).getString(SEARCH)))
                                                        .get()
                                                        .addOnCompleteListener(task3 -> {
                                                            if (task3.isSuccessful()) {
                                                                Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                                                if (!Objects.equals(Objects.requireNonNull(task3.getResult()).getString("job_description"), "")) {
                                                                    goToSearchResults();
                                                                } else {
                                                                    goToCreateSearch(Objects.requireNonNull(task2.getResult()).getDocuments().get(0).getId());
                                                                }
                                                            } else {
                                                                Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                                                            }
                                                        });
                                            }
                                            else {
                                                goToUserType();
                                            }
                                        }

                                        else {
                                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                                        }
                                    });
                        }
                    }

                    else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }

    private void goToMainPage(String documentId){
        final Intent intent = new Intent(FirebaseUIActivity.this, MainActivity.class);
        intent.putExtra(PROFILE_BELONGS_TO_USER, true);
        intent.putExtra(DOCUMENT_ID, documentId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void goToCreateProfile(String documentId){
        final Intent intent = new Intent(FirebaseUIActivity.this, EditProfileActivity.class);
        intent.putExtra(DOCUMENT_ID, documentId);
        intent.putExtra(NEW_USER, true);
        startActivity(intent);
        finish();
    }

    private void goToSearchResults() {
        final Intent intent = new Intent(FirebaseUIActivity.this, SearchResultsActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToCreateSearch(String userId) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(ENTREPRENEUR_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final Intent intent = new Intent(FirebaseUIActivity.this, EditSearchActivity.class);
                        intent.putExtra(DOCUMENT_ID, Objects.requireNonNull(task.getResult()).getString(SEARCH));
                        intent.putExtra(NEW_SEARCH, true);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void goToUserType() {
        final Intent intent = new Intent(FirebaseUIActivity.this, UserTypeActivity.class);
        startActivity(intent);
        finish();
    }
}
