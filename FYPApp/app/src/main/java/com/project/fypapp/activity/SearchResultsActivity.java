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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.adapter.SearchResultsRecyclerAdapter;
import com.project.fypapp.model.Retiree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.project.fypapp.model.Entrepreneur.ENTREPRENEUR_USERS;
import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.model.Search.SEARCH;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.EMAIL;
import static com.project.fypapp.util.Constants.LOGOUT_MESSAGE;
import static com.project.fypapp.util.Constants.PROFILE_BELONGS_TO_USER;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;

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
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        assert firebaseUser != null;
        db.collection(ENTREPRENEUR_USERS)
                .whereEqualTo(EMAIL, firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final Intent i = new Intent(SearchResultsActivity.this, EditSearchActivity.class);
                        i.putExtra(DOCUMENT_ID, (String) Objects.requireNonNull(task.getResult()).getDocuments().get(0).get(SEARCH));
                        startActivity(i);
                    }

                    else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RETIREE_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                        final List<Retiree> users = new ArrayList<>();
                        final List<String> userIds = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            users.add(document.toObject(Retiree.class));
                            userIds.add(document.getId());
                        }
                        final SearchResultsRecyclerAdapter recyclerAdapter = new SearchResultsRecyclerAdapter(users, (v, position) -> {
                            final Intent i = new Intent(SearchResultsActivity.this, MainActivity.class);
                            i.putExtra(PROFILE_BELONGS_TO_USER, false);
                            i.putExtra(DOCUMENT_ID, userIds.get(position));
                            startActivity(i);
                        });

                        recyclerView.setAdapter(recyclerAdapter);
                    } else {
                        Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                    }
                });
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
        final Intent intent = new Intent(SearchResultsActivity.this, FirebaseUIActivity.class);
        startActivity(intent);
        finish();
    }
}
