package com.santamaria.manejogastosmensuales.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by santamae on 4/10/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Category> categoryList;
    private int layout;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(List<Category> categoryList, int layout, OnItemClickListener onItemClickListener) {
        this.categoryList = categoryList;
        this.layout = layout;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(categoryList.get(position), onItemClickListener);

    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();
        } else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private TextView categoryName;
        private TextView total;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.categoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            this.total = (TextView) itemView.findViewById(R.id.tvValorCantidad);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageViewCategory);
        }

        public void bind(final Category category, final OnItemClickListener listener){

            this.categoryName.setText(category.getNombre());
            this.total.setText(category.getTotal()+"");

            Object currentPictureObject = category.getPicture();

            if (currentPictureObject instanceof String){
                String currPicture = currentPictureObject.toString();

                if (currPicture.contains("http")){
                    //url
                    Picasso.with(imageView.getContext()).load( currentPictureObject.toString()).fit().into(imageView);
                } else {
                    //create a file
                }

            } else if (currentPictureObject instanceof Integer){
                Picasso.with(imageView.getContext()).load((int) currentPictureObject).fit().into(imageView);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(category, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Category category, int position);
    }
}
