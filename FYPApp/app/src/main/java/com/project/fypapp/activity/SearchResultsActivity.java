package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.adapter.SearchResultsRecyclerAdapter;
import com.project.fypapp.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        final TextView editView = findViewById(R.id.edit_search_view);
        editView.setOnClickListener(view -> goToEditSearch());

        initRecyclerView();
    }

    private void goToEditSearch() {
        Intent i = new Intent(SearchResultsActivity.this, EditSearchActivity.class);
        i.putExtra("searchId", 123);
        startActivity(i);
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final List<UserProfile> users = createUsers();
        SearchResultsRecyclerAdapter recyclerAdapter = new SearchResultsRecyclerAdapter(users, new SearchResultsRecyclerAdapter.SearchResultsOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent i = new Intent(SearchResultsActivity.this, MainActivity.class);
                i.putExtra("profileBelongsToUser", false);
                startActivity(i);
            }
        });

        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<UserProfile> createUsers() {
        final List<UserProfile> users = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            users.add(new UserProfile());
        }

        return users;
    }
}
