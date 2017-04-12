package com.santamaria.manejogastosmensuales.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by santamae on 4/10/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Category> categoryList;
    private int layout;
    private OnItemClickListener onItemClickListener;
    private Context context;
    MenuInflater menuInflater;

    public RecyclerViewAdapter(List<Category> categoryList, int layout, OnItemClickListener onItemClickListener, MenuInflater menuInflater) {
        this.categoryList = categoryList;
        this.layout = layout;
        this.onItemClickListener = onItemClickListener;
        this.menuInflater = menuInflater;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView categoryName;
        private TextView total;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.categoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            this.total = (TextView) itemView.findViewById(R.id.tvValorCantidad);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageViewCategory);

            itemView.setOnCreateContextMenuListener(this);

        }

        public void bind(final Category category, final OnItemClickListener listener) {

            this.categoryName.setText(category.getNombre());
            this.total.setText(category.getTotal() + "");

            Object currentPictureObject = category.getPicture();

            if (currentPictureObject instanceof String) {
                String currPicture = currentPictureObject.toString();

                if (currPicture.contains("http")) {
                    //url
                    Picasso.with(imageView.getContext()).load(currentPictureObject.toString()).fit().into(imageView);
                } else {
                    //create a file
                    File image = new File(currPicture);
                    Picasso.with(imageView.getContext()).load(image).fit().into(imageView);
                }

            } else if (currentPictureObject instanceof Integer) {
                Picasso.with(imageView.getContext()).load((int) currentPictureObject).fit().into(imageView);
            } else if (currentPictureObject instanceof Uri){
                Uri image = (Uri) currentPictureObject;
                Picasso.with(imageView.getContext()).load(image).fit().into(imageView);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(category, getAdapterPosition());
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle(categoryList.get(this.getAdapterPosition()).getNombre());
            menuInflater.inflate(R.menu.options_card_view_context_menu, menu);

            MenuItem.OnMenuItemClickListener list = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.editCardView:
                            return true;
                        case R.id.deleteCardView:
                            removeCategory(getAdapterPosition());
                            return true;
                    }

                    return true;
                }
            };

            for (int i = 0, n = menu.size(); i < n; i++)
                menu.getItem(i).setOnMenuItemClickListener(list);
        }
    }

    private void removeCategory(int position) {

        categoryList.remove(position);
        this.notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Category category, int position);
    }
}
