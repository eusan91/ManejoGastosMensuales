package com.santamaria.manejogastosmensuales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.santamaria.manejogastosmensuales.Activities.MainActivity;
import com.santamaria.manejogastosmensuales.CategoryDialogFragment;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by santamae on 4/10/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements RealmChangeListener<RealmResults<Category>> {

    public static final int RESULT_EDIT_CATEGORY_DIALOG = 200;

    private List<Category> categoryList;
    private int layout;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private MenuInflater menuInflater;
    private Fragment fragment;
    private String currency;

    private Realm realm;

    public RecyclerViewAdapter(List<Category> categoryList, int layout, OnItemClickListener onItemClickListener, MenuInflater menuInflater, Fragment fragment) {
        this.categoryList = categoryList;
        this.layout = layout;
        this.onItemClickListener = onItemClickListener;
        this.menuInflater = menuInflater;
        realm = Realm.getDefaultInstance();
        this.fragment = fragment;
        this.currency = MainActivity.settingsData.getCurrency();
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



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private TextView categoryName;
        private TextView total;
        private ImageView imageView;
        private TextView currencyView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.categoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            this.total = (TextView) itemView.findViewById(R.id.tvValorCantidad);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageViewCategory);
            this.currencyView = (TextView) itemView.findViewById(R.id.currency);
            itemView.setOnCreateContextMenuListener(this);

        }

        public void bind(final Category category, final OnItemClickListener listener) {

            this.categoryName.setText(category.getNombre());
            this.total.setText(category.getTotal() + "");
            this.currencyView.setText(currency);
            String currentPictureObject = category.getPicture();

            if (currentPictureObject.isEmpty()){
                Picasso.with(imageView.getContext()).load(R.drawable.under_construct).fit().into(imageView);
            } else {
                Picasso.with(imageView.getContext()).load(Uri.parse(currentPictureObject)).fit().into(imageView);
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
            menu.setHeaderIcon(R.mipmap.ic_create_category);

            menuInflater.inflate(R.menu.options_card_view_context_menu, menu);

            for (int i = 0, n = menu.size(); i < n; i++)
                menu.getItem(i).setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.editCardView:
                    editCategory(categoryList.get(getAdapterPosition()));
                    return true;
                case R.id.deleteCardView:
                    removeCategory(categoryList.get(getAdapterPosition()));
                    return true;
                default:
                    return false;
            }
        }
    }

    private void editCategory(Category category) {

        CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", "Edit Category " + category.getNombre());
        bundle.putParcelable("category", category);
        bundle.putInt("type", CategoryDialogFragment.EDITION_TYPE);
        categoryDialogFragment.setArguments(bundle);

        categoryDialogFragment.setTargetFragment(fragment, RESULT_EDIT_CATEGORY_DIALOG);
        categoryDialogFragment.show(fragment.getFragmentManager(), "categoryDialogFragment");
        
    }

    private void removeCategory(Category category) {

        realm.beginTransaction();
        category.deleteFromRealm();
        realm.commitTransaction();
    }

    public interface OnItemClickListener {
        void onItemClick(Category category, int position);
    }

    @Override
    public void onChange(RealmResults<Category> element) {
        this.notifyDataSetChanged();
    }


}
