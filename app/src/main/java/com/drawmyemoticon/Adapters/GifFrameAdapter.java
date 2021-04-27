package com.drawmyemoticon.Adapters;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.drawmyemoticon.CanvasHandle.PaintStatus;
import com.drawmyemoticon.R;

import java.util.ArrayList;

public class GifFrameAdapter extends RecyclerView.Adapter<GifFrameAdapter.MyViewHolder> {
    ArrayList<Bitmap> imageList;

    public interface OnItemClickListener {
        void onFrameClick(Bitmap image);
        void onRemoveFrameButtonClick(int position);
    }

    private OnItemClickListener itemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView imageView, removeFrameButton;

        public MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.gifImageView);
            removeFrameButton = v.findViewById(R.id.removeFrameButton);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GifFrameAdapter(ArrayList<Bitmap> imageList) {
        this.imageList = imageList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GifFrameAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gif_frame_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.imageView.setImageBitmap(imageList.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onFrameClick(imageList.get(position));
                }
            }
        });
        holder.removeFrameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onRemoveFrameButtonClick(position);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
