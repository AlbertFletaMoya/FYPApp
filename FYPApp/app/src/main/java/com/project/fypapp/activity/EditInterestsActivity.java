package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.fypapp.R;
import com.project.fypapp.adapter.SkillsAndInterestsRecyclerAdapter;
import com.project.fypapp.model.Retiree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.fypapp.model.Retiree.RETIREE_USERS;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.IS_REGISTRATION;
import static com.project.fypapp.util.Constants.SKILLS_AND_INTERESTS;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.successfullySaved;

public class EditInterestsActivity extends AppCompatActivity {
    private static final String TAG = "EditInterestsActivity";

    private EditText searchTextView;
    private RecyclerView recyclerView;

    private List<String> interests = new ArrayList<>();
    private boolean isRegistration = false;
    private List<String> originalUserInterests;
    private Retiree retiree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skills_and_interests);

        final TextView title = findViewById(R.id.page_title_view);
        title.setText(R.string.select_interests);

        final TextView saveView = findViewById(R.id.save_view);
        final TextView cancelView = findViewById(R.id.cancel_view);

        searchTextView = findViewById(R.id.search_text);
        recyclerView = findViewById(R.id.recycler_view);

        if (getIntent().getExtras() != null) {
            isRegistration = getIntent().getBooleanExtra(IS_REGISTRATION, false);
            String documentId = getIntent().getStringExtra(DOCUMENT_ID);

            if (isRegistration) {
                cancelView.setVisibility(View.GONE);
                saveView.setText(R.string.next);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(SKILLS_AND_INTERESTS)
                    .document(SKILLS_AND_INTERESTS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                            interests = (List<String>) task.getResult().get("list");
                            assert documentId != null;
                            db.collection(RETIREE_USERS)
                                    .document(documentId)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                            retiree =
                                                    Objects.requireNonNull(task1.getResult())
                                                            .toObject(Retiree.class);
                                            assert retiree != null;
                                            if (retiree.getInterests() == null) {
                                                retiree.setInterests(new ArrayList<>());
                                            }
                                            originalUserInterests = new ArrayList<>(retiree.getInterests());
                                            cancelView.setOnClickListener(view -> cancel());
                                            saveView.setOnClickListener(view ->
                                                    save(documentId, isRegistration, retiree));
                                            searchTextView.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                    assert interests != null;
                                                    final List<String> searchInterests = interests.stream()
                                                            .filter(value -> value.toUpperCase()
                                                                    .contains(charSequence.toString().toUpperCase()))
                                                            .collect(Collectors.toList());
                                                    if (!charSequence.toString().equals("")) {
                                                        String newInterest = charSequence.toString()
                                                                .substring(0, 1).toUpperCase() +
                                                                charSequence.toString().substring(1).toLowerCase();
                                                        if (!searchInterests.contains(newInterest)) {
                                                            searchInterests.add(newInterest);
                                                        }
                                                    }
                                                    initRecyclerView(searchInterests, retiree);
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {}
                                            });
                                            initRecyclerView(interests, retiree);
                                        } else {
                                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                                        }
                                    });
                        } else {
                            Log.d(TAG, COULD_NOT_RETRIEVE_DATA);
                        }
                    });

        }
    }

    private void initRecyclerView(List<String> customInterests, Retiree retiree) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        SkillsAndInterestsRecyclerAdapter skillsAndInterestsRecyclerAdapter =
                new SkillsAndInterestsRecyclerAdapter(customInterests, retiree.getInterests(), (position, checked) -> {
                    List<String> currentUserInterests = retiree.getInterests();
                    if (checked) {
                        currentUserInterests.add(customInterests.get(position));
                        retiree.setInterests(currentUserInterests);
                        if ((position == customInterests.size() - 1) && !(interests.contains(customInterests.get(position)))) {
                            interests.add(customInterests.get(position));
                        }
                    } else {
                        currentUserInterests.removeIf(n -> n.equals(customInterests.get(position)));
                        retiree.setInterests(currentUserInterests);
                    }
                }, true, this);
        recyclerView.setAdapter(skillsAndInterestsRecyclerAdapter);

        for (int i = 0; i < customInterests.size(); i++) {
                if (retiree.getInterests().contains(customInterests.get(i))) {
                    SkillsAndInterestsRecyclerAdapter.SkillsAndInterestsViewHolder viewHolder =
                            (SkillsAndInterestsRecyclerAdapter.SkillsAndInterestsViewHolder)
                                    recyclerView.findViewHolderForAdapterPosition(i);
                    if (null != viewHolder) {
                        viewHolder.setCheckboxView();
                    }
                }
        }
    }

    private void save(String documentId, boolean isRegistration, Retiree retiree) {
        originalUserInterests = Lists.newArrayList(Sets.newHashSet(originalUserInterests));
        if (!hasChanged()) {
            finish();
        } else {

            final List<String> cleanList = Lists.newArrayList(Sets.newHashSet(retiree.getInterests()));
            retiree.setInterests(cleanList);

            // TODO Store any new interests, for security measures we could limit the amount of new words
            // That a user can store, and validate with a dictionary that the user gave valid words and
            // Not just bogus strings to try and fill the storage up
            for (String checkedElement : cleanList) {
                if (!interests.contains(checkedElement)) {
                    interests.add(checkedElement);
                }
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> map = new HashMap<>();
            map.put("list", interests);

            db.collection(RETIREE_USERS)
                    .document(documentId)
                    .update(retiree.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, SUCCESSFULLY_UPDATED);
                        db.collection(SKILLS_AND_INTERESTS)
                                .document(SKILLS_AND_INTERESTS)
                                .update(map)
                                .addOnSuccessListener(aVoid1 -> Log.d(TAG, SUCCESSFULLY_UPDATED))
                                .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
                        if (isRegistration) {
                            goToNext(documentId);
                        } else {
                            finish();
                            successfullySaved(this);
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
        }
    }

    private void goToNext(String documentId) {
        Intent i = new Intent(EditInterestsActivity.this, EditSkillsActivity.class);
        i.putExtra(DOCUMENT_ID, documentId);
        i.putExtra(IS_REGISTRATION, true);
        startActivity(i);
        finish();
    }

    private void cancel() {
        originalUserInterests = Lists.newArrayList(Sets.newHashSet(originalUserInterests));
        if (hasChanged()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.discard_changes)
                    .setMessage(R.string.want_to_discard_changes)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> finish())
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            finish();
        }
    }

    private boolean hasChanged() {
        return !Sets.newHashSet(originalUserInterests).equals(Sets.newHashSet(retiree.getInterests()));
    }

    @Override
    public void onBackPressed() {
        if (isRegistration) {
            super.onBackPressed();
        } else {
            cancel();
        }
    }
}
