package com.project.fypapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class BasicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
   private final List<String> adapterItems;
   private final BasicOnClickListener onClickListener;

   class BasicViewHolder extends RecyclerView.ViewHolder {
       private final TextView textView;

       BasicViewHolder(@NonNull View itemView) {
           super(itemView);
           textView = itemView.findViewById(R.id.text_view);

           itemView.setOnClickListener(view -> onClickListener.onItemClick(view, getAdapterPosition()));
       }
   }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_basic, parent, false);
        return new BasicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String item = adapterItems.get(position);
        BasicViewHolder basicViewHolder = (BasicViewHolder) holder;
        basicViewHolder.textView.setText(item.trim());
    }

    @Override
    public int getItemCount() {
        return adapterItems.size();
    }

    public interface BasicOnClickListener {
        void onItemClick(View v, int position);
    }
}
