package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.adapter.SearchResultsRecyclerAdapter;
import com.project.fypapp.model.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.project.fypapp.util.Constants.LOGOUT_MESSAGE;

public class SearchResultsActivity extends AppCompatActivity {
    private static final String TAG = "SearchResultsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        final TextView editView = findViewById(R.id.edit_search_view);
        editView.setOnClickListener(view -> goToEditSearch());

        final TextView logoutView = findViewById(R.id.logout_view);
        logoutView.setOnClickListener(view -> signOut());

        initRecyclerView();
    }

    private void goToEditSearch() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        assert firebaseUser != null;
        db.collection("entrepreneur_users")
                .whereEqualTo("email", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(SearchResultsActivity.this, EditSearchActivity.class);
                        i.putExtra("documentId", (String) Objects.requireNonNull(task.getResult()).getDocuments().get(0).get("search"));
                        startActivity(i);
                    }

                    else {
                        Log.d(TAG, "Task was not successful");
                    }
                });
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        final List<UserProfile> users = createUsers();
        SearchResultsRecyclerAdapter recyclerAdapter = new SearchResultsRecyclerAdapter(users, (v, position) -> {
            Intent i = new Intent(SearchResultsActivity.this, MainActivity.class);
            i.putExtra("profileBelongsToUser", false);
            startActivity(i);
        });

        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<UserProfile> createUsers() {
        final List<UserProfile> users = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            users.add(new UserProfile());
        }

        return users;
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
        Intent intent = new Intent(SearchResultsActivity.this, FirebaseUIActivity.class);
        startActivity(intent);
        finish();
    }
}
