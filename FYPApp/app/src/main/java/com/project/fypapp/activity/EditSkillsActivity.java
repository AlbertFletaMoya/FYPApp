package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class EditSkillsActivity extends AppCompatActivity {
    private static final String TAG = "EditSkillsActivity";

    private TextInputEditText searchTextView;
    private RecyclerView recyclerView;

    private List<String> skills = new ArrayList<>();
    private boolean isRegistration = false;
    private Retiree retiree;
    private List<String> originalUserSkills;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skills_and_interests);

        final TextView title = findViewById(R.id.page_title_view);
        title.setText(R.string.select_skills);

        searchTextView = findViewById(R.id.search_text);
        recyclerView = findViewById(R.id.recycler_view);

        final TextInputLayout searchLayout = findViewById(R.id.search_layout);
        searchLayout.setHint(getString(R.string.search_and_select_skills));

        final TextView saveView = findViewById(R.id.save_view);
        final TextView cancelView = findViewById(R.id.cancel_view);

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
                            skills = (List<String>) task.getResult().get("list");
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
                                            if (retiree.getSkills() == null) {
                                                retiree.setSkills(new ArrayList<>());
                                            }
                                            originalUserSkills = new ArrayList<>(retiree.getSkills());
                                            cancelView.setOnClickListener(view -> cancel());
                                            saveView.setOnClickListener(view ->
                                                    save(documentId, isRegistration, retiree));
                                            searchTextView.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                    assert skills != null;
                                                    final List<String> searchSkills = skills.stream()
                                                            .filter(value -> value.toUpperCase()
                                                                    .contains(charSequence.toString().toUpperCase()))
                                                            .collect(Collectors.toList());
                                                    if (!charSequence.toString().equals("")) {
                                                        String newSkill = charSequence.toString()
                                                                .substring(0, 1).toUpperCase() +
                                                                charSequence.toString().substring(1).toLowerCase();
                                                        if (!searchSkills.contains(newSkill)) {
                                                            searchSkills.add(newSkill);
                                                        }
                                                    }
                                                    initRecyclerView(searchSkills);
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {
                                                }
                                            });
                                            initRecyclerView(skills);
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

    private void initRecyclerView(List<String> customSkills) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        SkillsAndInterestsRecyclerAdapter skillsAndInterestsRecyclerAdapter =
                new SkillsAndInterestsRecyclerAdapter(customSkills, retiree.getSkills(),
                        (position, checked) -> {
                    List<String> currentUserSkills = retiree.getSkills();
                    if (checked) {
                        currentUserSkills.add(customSkills.get(position));
                        retiree.setSkills(currentUserSkills);
                        if ((position == customSkills.size() - 1)
                        && !(skills.contains(customSkills.get(position)))) {
                            skills.add(customSkills.get(position));
                        }
                    } else {
                        currentUserSkills.removeIf(n -> n.equals(customSkills.get(position)));
                        retiree.setSkills(currentUserSkills);
                    }
                        }, true, this);
        recyclerView.setAdapter(skillsAndInterestsRecyclerAdapter);

        for (int i = 0; i < customSkills.size(); i++) {
            if (retiree.getSkills().contains(customSkills.get(i))) {
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
        originalUserSkills = Lists.newArrayList(Sets.newHashSet(originalUserSkills));
        if (!hasChanged()) {
            finish();
        } else {
            final List<String> cleanList = Lists.newArrayList(Sets.newHashSet(retiree.getSkills()));
            retiree.setSkills(cleanList);

            // TODO Store any new skills, for security measures we could limit the amount of new words
            // That a user can store, and validate with a dictionary that the user gave valid words and
            // Not just bogus strings to try and fill the storage up
            for (String checkedElement : cleanList) {
                if (!skills.contains(checkedElement)) {
                    skills.add(checkedElement);
                }
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> map = new HashMap<>();
            map.put("list", skills);

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
        Intent i = new Intent(EditSkillsActivity.this, EditProfilePhotoActivity.class);
        i.putExtra(DOCUMENT_ID, documentId);
        i.putExtra(IS_REGISTRATION, true);
        startActivity(i);
        finish();
    }

    private boolean hasChanged() {
        return (!Sets.newHashSet(originalUserSkills).equals(Sets.newHashSet(retiree.getSkills())));
    }

    private void cancel() {
        originalUserSkills = Lists.newArrayList(Sets.newHashSet(originalUserSkills));
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

    @Override
    public void onBackPressed() {
        if (isRegistration) {
            super.onBackPressed();
        } else {
            cancel();
        }
    }
}
