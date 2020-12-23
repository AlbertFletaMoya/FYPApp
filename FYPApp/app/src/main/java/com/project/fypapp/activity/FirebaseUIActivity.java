package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.fypapp.R;

import java.util.Arrays;
import java.util.List;

import static com.firebase.ui.auth.ErrorCodes.NO_NETWORK;
import static com.project.fypapp.util.Constants.BACK_BUTTON_ERROR_MESSAGE;
import static com.project.fypapp.util.Constants.LOGIN_MESSAGE;
import static com.project.fypapp.util.Constants.NO_NETWORK_ERROR_MESSAGE;
import static com.project.fypapp.util.Constants.UNKNOWN_ERROR_MESSAGE;

public class FirebaseUIActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goToMainPage();
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
                .Builder(R.layout.login_picker)
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
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                goToMainPage();
                // ...
            } else {
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

    private void goToMainPage(){
        Intent intent = new Intent(FirebaseUIActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
