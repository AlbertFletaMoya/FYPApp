package com.project.fypapp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.project.fypapp.model.Search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.fypapp.model.Search.SEARCHES;
import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.DOCUMENT_ID;
import static com.project.fypapp.util.Constants.SKILLS_AND_INTERESTS;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_UPDATED;
import static com.project.fypapp.util.Constants.UNSUCCESSFULLY_UPDATED;

public class EditSearchSkillsActivity extends AppCompatActivity {
    private static final String TAG = "EditSearchSkillsActivity";

    private TextInputEditText searchTextView;
    private RecyclerView recyclerView;

    private List<String> skills = new ArrayList<>();
    private List<String> originalUserSkills;
    private Search search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skills_and_interests);

        final TextView title = findViewById(R.id.page_title_view);
        title.setText(R.string.select_skills);

        searchTextView = findViewById(R.id.search_text);
        recyclerView = findViewById(R.id.recycler_view);

        final TextInputLayout searchLayout = findViewById(R.id.search_layout);
        searchLayout.setHint(getString(R.string.search_and_select_skills_you_are_looking_for));

        final TextView saveView = findViewById(R.id.save_view);
        final TextView cancelView = findViewById(R.id.cancel_view);

        if (getIntent().getExtras() != null) {
            String documentId = getIntent().getStringExtra(DOCUMENT_ID);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(SKILLS_AND_INTERESTS)
                    .document(SKILLS_AND_INTERESTS)
                    .get()
                    .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                           skills = (List<String>) task.getResult().get("list");
                           assert documentId != null;
                           db.collection(SEARCHES)
                                   .document(documentId)
                                   .get()
                                   .addOnCompleteListener(task1 -> {
                                      if (task1.isSuccessful()) {
                                          Log.d(TAG, SUCCESSFULLY_RETRIEVED_DATA);
                                          search =
                                                  Objects.requireNonNull(task1.getResult()
                                                  .toObject(Search.class));
                                          assert search != null;
                                          if (search.getSkills() == null) {
                                              search.setSkills(new ArrayList<>());
                                          }
                                          originalUserSkills = new ArrayList<>(search.getSkills());
                                          cancelView.setOnClickListener(view -> cancel());
                                          saveView.setOnClickListener(view ->
                                                  save(documentId, search));
                                          searchTextView.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

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
                                                  initRecyclerView(searchSkills, search);
                                              }

                                              @Override
                                              public void afterTextChanged(Editable editable) { }
                                          });
                                          initRecyclerView(skills, search);
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

    private void initRecyclerView(List<String> customSkills, Search search) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        SkillsAndInterestsRecyclerAdapter skillsAndInterestsRecyclerAdapter =
                new SkillsAndInterestsRecyclerAdapter(customSkills, search.getSkills(),
                        (position , checked) -> {
                    List<String> currentSearchSkills = search.getSkills();
                    if (checked) {
                        currentSearchSkills.add(customSkills.get(position));
                        search.setSkills(currentSearchSkills);
                        if ((position == customSkills.size() - 1)
                        && !(skills.contains(customSkills.get(position)))) {
                            skills.add(customSkills.get(position));
                        }
                    } else {
                        currentSearchSkills.removeIf(n -> n.equals(customSkills.get(position)));
                        search.setSkills(currentSearchSkills);
                    }
                        }, this);
        recyclerView.setAdapter(skillsAndInterestsRecyclerAdapter);

        for (int i = 0; i < customSkills.size(); i++) {
            if (search.getSkills().contains(customSkills.get(i))) {
                SkillsAndInterestsRecyclerAdapter.SkillsAndInterestsViewHolder viewHolder =
                        (SkillsAndInterestsRecyclerAdapter.SkillsAndInterestsViewHolder)
                        recyclerView.findViewHolderForAdapterPosition(i);
                if (null != viewHolder) {
                    viewHolder.setCheckboxView();
                }
            }
        }
    }

    private void save(String documentId, Search search) {
        final List<String> cleanList = Lists.newArrayList(Sets.newHashSet(search.getSkills()));
        search.setSkills(cleanList);

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

        db.collection(SEARCHES)
                .document(documentId)
                .update(search.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, SUCCESSFULLY_UPDATED);
                    db.collection(SKILLS_AND_INTERESTS)
                            .document(SKILLS_AND_INTERESTS)
                            .update(map)
                            .addOnSuccessListener(aVoid1 -> Log.d(TAG, SUCCESSFULLY_UPDATED))
                            .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
                    finish();
                })
                .addOnFailureListener(e -> Log.d(TAG, UNSUCCESSFULLY_UPDATED));
    }

    private void cancel() {
        originalUserSkills = Lists.newArrayList(Sets.newHashSet(originalUserSkills));
        if (!originalUserSkills.equals(Lists.newArrayList(Sets.newHashSet(search.getSkills())))) {
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
        cancel();
    }
}
