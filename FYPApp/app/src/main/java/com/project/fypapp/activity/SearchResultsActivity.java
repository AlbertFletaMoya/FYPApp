package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.adapter.SearchResultsRecyclerAdapter;
import com.project.fypapp.model.Retiree;

import java.util.ArrayList;
import java.util.List;

import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
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

        final TextView logoutView = findViewById(R.id.logout_view);
        logoutView.setOnClickListener(view -> signOut());

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.INVISIBLE);

        if (getIntent().getExtras() != null) {
            String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            editView.setOnClickListener(view -> goToEditSearch(documentId));

        }

        initRecyclerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initRecyclerView();
    }

    private void goToEditSearch(String documentId) {
        final Intent i = new Intent(SearchResultsActivity.this, EditSearchActivity.class);
        i.putExtra(DOCUMENT_ID, documentId);
        startActivity(i);
    }

    private void initRecyclerView() {
        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.showContextMenu();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final TextView noResultsView = findViewById(R.id.no_results_view);
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

                        if (users.size() > 0) {
                            final SearchResultsRecyclerAdapter recyclerAdapter = new SearchResultsRecyclerAdapter(users, (v, position) -> {
                                final Intent i = new Intent(SearchResultsActivity.this, MainActivity.class);
                                i.putExtra(PROFILE_BELONGS_TO_USER, false);
                                i.putExtra(DOCUMENT_ID, userIds.get(position));
                                startActivity(i);
                            }, this);

                            noResultsView.setVisibility(View.GONE);
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        else {
                            recyclerView.setVisibility(View.GONE);
                            noResultsView.setVisibility(View.VISIBLE);
                            noResultsView.setText(R.string.no_results);
                        }
                        progressBar.setVisibility(View.GONE);

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
