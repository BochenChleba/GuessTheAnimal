package com.bochenchleba.guesstheanimal;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Robert on 2017-09-03.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;

    private List<LibraryEntry> entriesList;

    private RecyclerItemClickListener listener;

    RecyclerAdapter(Context context, List<LibraryEntry> entriesList,
                    RecyclerItemClickListener listener){

        this.context = context;
        this.entriesList = entriesList;
        this.listener = listener;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.recycler_item, parent, false);

        return new ViewHolder(itemView, entriesList, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {

        holder.image.setImageDrawable(null);

        LibraryEntry entry = entriesList.get(position);

        String name = entry.getName();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        holder.title.setText(name);
        holder.desc.setText(entry.getDesc());
        holder.image.setImageResource(entry.getImage());

        if (position%2==1)
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        else
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView title;
        TextView desc;

        List<LibraryEntry> entriesList;
        RecyclerItemClickListener listener;

        private ViewHolder (View item, List<LibraryEntry> entriesList,
                           RecyclerItemClickListener listener){
            super(item);

            this.listener = listener;
            this.entriesList = entriesList;

            title = item.findViewById(R.id.recycler_title);
            desc = item.findViewById(R.id.recycler_desc);
            image = item.findViewById(R.id.recycler_image);

            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();

            LibraryEntry entry = entriesList.get(pos);

            this.listener.onRecyclerItemClick(entry.getName());
        }
    }

    public interface RecyclerItemClickListener {
        void onRecyclerItemClick(String animalName);
    }
}
