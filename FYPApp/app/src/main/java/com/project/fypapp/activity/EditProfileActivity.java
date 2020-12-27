package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.project.fypapp.R;
import com.project.fypapp.model.UserProfile;

import org.w3c.dom.Text;

public class EditProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            saveProfile();
            goToMain();
        });

        final Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> goToMain());

        final EditText edit = findViewById(R.id.edit_text_view);

        if (getIntent().getExtras() != null){
            UserProfile userProfile = new UserProfile();
            int position = getIntent().getIntExtra("position", -1);

            if (position == -2){
                edit.setText("");
            }

            else if (position == -1){
                edit.setText(userProfile.getBio());
            }

            else {
                edit.setText(userProfile.getJobs().get(position).getJobDescription());
            }
        }
    }

    private void goToMain() {
        Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void saveProfile() {
        // save in database
    }
}
