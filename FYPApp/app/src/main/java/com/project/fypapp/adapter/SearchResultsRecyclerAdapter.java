package com.project.fypapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.project.fypapp.R;
import com.project.fypapp.model.Retiree;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static com.project.fypapp.util.Constants.COULD_NOT_RETRIEVE_DATA;
import static com.project.fypapp.util.Constants.SUCCESSFULLY_RETRIEVED_DATA;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SearchResultsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Retiree> users;
    private final SearchResultsOnClickListener onClickListener;
    Context context;

    class SearchResultsViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profilePictureView;
        private final TextView nameView;
        private final TextView bioView;

        SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.name_view);
            bioView = itemView.findViewById(R.id.bio_view);
            profilePictureView = itemView.findViewById(R.id.profile_picture_view);

            itemView.setOnClickListener(view -> onClickListener.onItemClick(view, getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_search_result, parent, false);
        return new SearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchResultsViewHolder viewHolder = (SearchResultsViewHolder) holder;
        final Retiree retiree = users.get(position);
        viewHolder.nameView.setText(Retiree.getName(retiree));
        viewHolder.bioView.setText(retiree.getHeadline());
        Glide.with(context)
                .load(Uri.parse(retiree.getProfilePictureUri()))
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_120)
                .error(R.drawable.ic_baseline_person_120)
                .into(viewHolder.profilePictureView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface SearchResultsOnClickListener {
        void onItemClick(View v, int position);
    }
}
