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
import static com.project.fypapp.util.Constants.BACK_BUTTON_ERROR_MESSAGE;
import static com.project.fypapp.util.Constants.NO_NETWORK_ERROR_MESSAGE;
import static com.project.fypapp.util.Constants.UNKNOWN_ERROR_MESSAGE;

public class FirebaseUIActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 123;
    private static final String TAG = "FirebaseUIActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "User is logged in");
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
                    Log.d(TAG, "User is null");
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
        db.collection("retiree_users")
                .whereEqualTo("email", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!isQueryResultEmpty(task)) {
                            if (!isFieldEmpty(task, "headline")) {
                                goToMainPage(Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId());
                            } else {
                                goToCreateProfile(Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId());
                            }
                        }

                        else {
                            db.collection("entrepreneur_users")
                                    .whereEqualTo("email", firebaseUser.getEmail())
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            if (!isQueryResultEmpty(task2)) {
                                                db.collection("searches")
                                                        .document(Objects.requireNonNull(Objects.requireNonNull(task2.getResult()).getDocuments().get(0).getString("search")))
                                                        .get()
                                                        .addOnCompleteListener(task3 -> {
                                                            if (task3.isSuccessful()) {
                                                                if (!Objects.equals(Objects.requireNonNull(task3.getResult()).getString("job_description"), "")) {
                                                                    goToSearchResults();
                                                                } else {
                                                                    goToCreateSearch(Objects.requireNonNull(task2.getResult()).getDocuments().get(0).getId());
                                                                }
                                                            } else {
                                                                Log.d(TAG, "Could not retrieve data from database");
                                                            }
                                                        });
                                            }
                                            else {
                                                goToUserType();
                                            }
                                        }

                                        else {
                                            Log.d(TAG, "Could not retrieve data from database");
                                        }
                                    });
                        }
                    }

                    else {
                        Log.d(TAG, "Could not retrieve data from database");
                    }
                });
    }

    private void goToMainPage(String documentId){
        Intent intent = new Intent(FirebaseUIActivity.this, MainActivity.class);
        intent.putExtra("profileBelongsToUser", true);
        intent.putExtra("documentId", documentId);
        startActivity(intent);
        finish();
    }

    private void goToCreateProfile(String documentId){
        Intent intent = new Intent(FirebaseUIActivity.this, EditProfileActivity.class);
        intent.putExtra("documentId", documentId);
        intent.putExtra("newUser", true);
        startActivity(intent);
        finish();
    }

    private void goToSearchResults() {
        Intent intent = new Intent(FirebaseUIActivity.this, SearchResultsActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToCreateSearch(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("entrepreneur_users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(FirebaseUIActivity.this, EditSearchActivity.class);
                        intent.putExtra("documentId", Objects.requireNonNull(task.getResult()).getString("search"));
                        intent.putExtra("newSearch", true);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void goToUserType() {
        Intent intent = new Intent(FirebaseUIActivity.this, UserTypeActivity.class);
        startActivity(intent);
        finish();
    }
}
