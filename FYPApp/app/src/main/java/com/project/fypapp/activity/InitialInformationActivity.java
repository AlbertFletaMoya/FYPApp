package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.fypapp.R;
import com.project.fypapp.model.UserProfile;

import java.util.ArrayList;

public class InitialInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initial_information);

        final Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(view -> saveProfile());
    }

    private void saveProfile() {
        final EditText bioView = findViewById(R.id.headline_write_view);
        final EditText locationView = findViewById(R.id.location_write_view);

        UserProfile userProfile = new UserProfile("Albert Fleta", "leinadfc01@gmail.com", new ArrayList<>(),
                locationView.getText().toString().trim(), bioView.getText().toString().trim());
        // save the profile

        goToMain();
    }

    private void goToMain() {
        Intent i = new Intent(InitialInformationActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("firstLogIn", false);
        startActivity(i);
        finish();
    }
}
