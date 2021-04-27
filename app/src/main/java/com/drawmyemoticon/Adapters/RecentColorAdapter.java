package com.drawmyemoticon.Adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.drawmyemoticon.R;

import java.util.ArrayList;

public class RecentColorAdapter extends RecyclerView.Adapter<RecentColorAdapter.MyViewHolder> {
    ArrayList<Integer> colors;

    public interface OnItemClickListener {
        void onItemClick(int color) ;
    }

    private RecentColorAdapter.OnItemClickListener itemClickListener = null ;

    public void setOnItemClickListener(RecentColorAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener ;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        Button colorButton;
        public MyViewHolder(View v) {
            super(v);
            colorButton = v.findViewById(R.id.colorButton);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecentColorAdapter(ArrayList<Integer> colors) {
        this.colors = colors;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecentColorAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_color_button, parent, false);
        RecentColorAdapter.MyViewHolder vh = new RecentColorAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecentColorAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.colorButton.setBackgroundColor(colors.get(position));
        holder.colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(colors.get(position));
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return colors.size();
    }
}