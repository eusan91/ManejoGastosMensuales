package com.santamaria.manejogastosmensuales.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santamaria.manejogastosmensuales.Activities.MainActivity;
import com.santamaria.manejogastosmensuales.Domain.CategoryDefined;
import com.santamaria.manejogastosmensuales.Activities.CategoryDetailedActivity;
import com.santamaria.manejogastosmensuales.Adapter.RecyclerViewAdapter;
import com.santamaria.manejogastosmensuales.CategoryDialogFragment;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainFragment extends Fragment implements View.OnClickListener,
        RealmChangeListener<RealmResults<Category>> {

    private static final int RESULT_CREATE_CATEGORY_DIALOG = 100;

    private FloatingActionButton fabAdd;
    RecyclerView recyclerViewCategories;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    private RealmResults<Category> categories;

    private Realm realm;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm = Realm.getDefaultInstance();

        categories = realm.where(Category.class).findAll();

        if (categories.isEmpty()){

            if (!MainActivity.settingsData.getCategoryDefinedList().isEmpty()){

                for (CategoryDefined categoryDefined: MainActivity.settingsData.getCategoryDefinedList()) {
                    addNewCategory(new Category(categoryDefined.getCategoryName()));
                }
            }
        }

        categories.addChangeListener(this);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recyclerViewCategorias);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());

        recyclerViewAdapter = new RecyclerViewAdapter(categories, R.layout.recycler_cardview_item,
                new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Category category, int position) {

                        Intent intent = new Intent(getContext(), CategoryDetailedActivity.class);
                        intent.putExtra("categoryID", categories.get(position).getId());
                        startActivity(intent);

                    }
                }, getActivity().getMenuInflater(), this);

        recyclerViewCategories.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewCategories.setAdapter(recyclerViewAdapter);

        //fab action
        fabAdd = (FloatingActionButton) view.findViewById(R.id.fabAddCategory);

        fabAdd.setOnClickListener(this);

        //recycler view scroll
        recyclerViewCategories.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 || dy < 0 || dx > 0 || dx < 0) {
                    fabAdd.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabAdd.show();
                }
            }
        });

        //cuando el layout no va a cambiar, esta propiedad mejora el performace.
        recyclerViewCategories.setHasFixedSize(true);

        //sencilla animacion...
        recyclerViewCategories.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    private void addNewCategory(Category category) {

        realm.beginTransaction();
        realm.copyToRealm(category);
        realm.commitTransaction();
        recyclerViewLayoutManager.scrollToPosition(recyclerViewAdapter.getItemCount()-1);

    }

    private void editCategory(Category categoryOld, Category categoryNew) {

        realm.beginTransaction();
        categoryOld.setNombre(categoryNew.getNombre());

        if (!categoryNew.getPicture().isEmpty()){
            categoryOld.setPicture(categoryNew.getPicture());
        }
        realm.copyToRealmOrUpdate(categoryOld);
        realm.commitTransaction();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fabAddCategory) {

            CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString("title", "Create new Category");
            bundle.putInt("type", CategoryDialogFragment.CREATION_TYPE);
            categoryDialogFragment.setArguments(bundle);

            categoryDialogFragment.setTargetFragment(this, RESULT_CREATE_CATEGORY_DIALOG);
            categoryDialogFragment.show(getFragmentManager(), "categoryDialogFragment");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (RESULT_CREATE_CATEGORY_DIALOG == requestCode){

                if (data.getExtras().containsKey("Category")) {

                    Category category = data.getExtras().getParcelable("Category");
                    addNewCategory(category);

                }
            } else if (RecyclerViewAdapter.RESULT_EDIT_CATEGORY_DIALOG == requestCode){

                Category categoryOld = data.getExtras().getParcelable("CategoryOld");
                Category categoryNew = data.getExtras().getParcelable("CategoryNew");
                editCategory(categoryOld, categoryNew);
            }
        }
    }



    @Override
    public void onChange(RealmResults<Category> element) {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (categories.isEmpty()){

            if (!MainActivity.settingsData.getCategoryDefinedList().isEmpty()){

                for (CategoryDefined categoryDefined: MainActivity.settingsData.getCategoryDefinedList()) {
                    addNewCategory(new Category(categoryDefined.getCategoryName()));
                }
            }
        }

    }
}
