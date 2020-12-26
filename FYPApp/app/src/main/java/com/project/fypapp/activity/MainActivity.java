package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.fypapp.R;
import com.project.fypapp.adapter.UserProfileRecyclerAdapter;
import com.project.fypapp.model.UserProfile;

import static com.project.fypapp.util.Constants.LOGOUT_MESSAGE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkCurrentUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> signOut());


        initRecyclerView();
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

    private void initRecyclerView(){
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final UserProfile userProfile = new UserProfile();

        UserProfileRecyclerAdapter userProfileRecyclerAdapter =
                new UserProfileRecyclerAdapter(userProfile, (v, position) -> {
                    Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
                    i.putExtra("position", position);
                    startActivity(i);
                });
        recyclerView.setAdapter(userProfileRecyclerAdapter);
    }


}