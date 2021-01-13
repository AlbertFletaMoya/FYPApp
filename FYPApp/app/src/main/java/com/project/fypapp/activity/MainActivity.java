package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.project.fypapp.R;
import com.project.fypapp.adapter.UserProfileRecyclerAdapter;
import com.project.fypapp.model.UserProfile;

import static com.project.fypapp.util.Constants.LOGOUT_MESSAGE;

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

        boolean profileBelongsToUser = true;

        if (getIntent().getExtras() != null) {
            profileBelongsToUser = getIntent().getBooleanExtra("profileBelongsToUser", false);
            if (!profileBelongsToUser) {
                ((ViewGroup) logoutButton.getParent()).removeView(logoutButton);
            }
        }

        if (profileBelongsToUser) {
            ((ViewGroup) cancelButton.getParent()).removeView(cancelButton);
        }

        initRecyclerView(profileBelongsToUser);
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
        Intent intent = new Intent(MainActivity.this, FirebaseUIActivity.class);
        startActivity(intent);
        finish();
    }

    private void initRecyclerView(boolean profileBelongsToUser) {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final UserProfile userProfile = new UserProfile();

        UserProfileRecyclerAdapter userProfileRecyclerAdapter =
                new UserProfileRecyclerAdapter(userProfile, new UserProfileRecyclerAdapter.UserProfileRecyclerAdapterListener() {
                    @Override
                    public void editProfileOnClick(View v, int position) {
                        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
                        i.putExtra("userId", 123);
                        startActivity(i);
                    }

                    @Override
                    public void editExperienceOnClick(View v, int position) {
                        Intent i = new Intent(MainActivity.this, ExperienceIndexActivity.class);
                        startActivity(i);
                    }
                }, profileBelongsToUser); // set to false if don't want user to belong to profile
        recyclerView.setAdapter(userProfileRecyclerAdapter);
    }

}