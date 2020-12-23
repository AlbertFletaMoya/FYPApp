package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.fypapp.R;

import static com.project.fypapp.util.Constants.LOGOUT_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private final FirebaseUIActivity firebaseUIActivity = new FirebaseUIActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkCurrentUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> signOut());
    }

    private void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            goToLogIn();
        }
    }

    private void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Toast.makeText(getApplicationContext(), LOGOUT_MESSAGE, Toast.LENGTH_LONG).show();
                    goToLogIn();
                });
    }

    private void goToLogIn(){
        Intent intent = new Intent(MainActivity.this, FirebaseUIActivity.class);
        startActivity(intent);
        finish();
    }
}