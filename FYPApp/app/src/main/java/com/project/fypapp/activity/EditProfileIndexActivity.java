package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.adapter.BasicRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.IS_REGISTRATION;

public class EditProfileIndexActivity extends AppCompatActivity {
    private static final List<String> ACTIVITIES = new ArrayList<String>(){{
        add("Change name");
        add("Change location");
        add("Change profile headline");
        add("Change profile photo");
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile_index);

        final TextView cancelButton = findViewById(R.id.cancel_view);
        cancelButton.setOnClickListener(view -> finish());

        if (getIntent().getExtras() != null) {
            final String documentId = getIntent().getStringExtra(DOCUMENT_ID);
            initRecyclerView(documentId);
        }
    }

    private void initRecyclerView(String documentId) {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        final BasicRecyclerAdapter basicRecyclerAdapter =
                new BasicRecyclerAdapter(ACTIVITIES, (v, position) -> {
                    Intent i = null;
                    switch (position) {
                        case 0:
                            i = new Intent(EditProfileIndexActivity.this, EditNameActivity.class);
                            break;
                        case 1:
                            i = new Intent(EditProfileIndexActivity.this, EditLocationActivity.class);
                            break;
                        case 2:
                            i = new Intent(EditProfileIndexActivity.this, EditProfileHeadlineActivity.class);
                            break;
                        case 3:
                            i = new Intent(EditProfileIndexActivity.this, EditProfilePhotoActivity.class);
                            break;
                    }
                    assert i != null;
                    i.putExtra(DOCUMENT_ID, documentId);
                    i.putExtra(IS_REGISTRATION, false);
                    startActivity(i);
                });
        recyclerView.setAdapter(basicRecyclerAdapter);
    }
}
