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
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Activities.MainActivity;
import com.santamaria.manejogastosmensuales.Domain.CategoryDefined;
import com.santamaria.manejogastosmensuales.Activities.CategoryDetailedActivity;
import com.santamaria.manejogastosmensuales.Adapter.RecyclerViewAdapter;
import com.santamaria.manejogastosmensuales.CategoryDialogFragment;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.Domain.CategoryMonth;
import com.santamaria.manejogastosmensuales.R;
import com.santamaria.manejogastosmensuales.app.MyApplication;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainFragment extends Fragment implements View.OnClickListener,
        RealmChangeListener<RealmList<Category>> {

    private static final int RESULT_CREATE_CATEGORY_DIALOG = 100;

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerViewCategories;
    private static RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private TextView tvTotal1;
    private float TOTAL;
    private TextView tvCurrency;

    private RealmList<Category> categories;

    private Realm realm;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm = Realm.getDefaultInstance();

        //get current month
        CategoryMonth categoryMonth = realm.where(CategoryMonth.class).equalTo(MyApplication.CURRENT_MONTH_COLUMN, true).findFirst();

        if (categoryMonth != null) {
            categories = categoryMonth.getCategoryList();
        }

        if (categories == null || categories.isEmpty()) {

            if (!MainActivity.settingsData.getCategoryDefinedList().isEmpty()) {

                for (CategoryDefined categoryDefined : MainActivity.settingsData.getCategoryDefinedList()) {
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
                        intent.putExtra(CategoryDetailedActivity.CATEGORY_ID_EXTRA, categories.get(position).getId());
                        startActivity(intent);

                    }
                }, getActivity().getMenuInflater(), this);

        recyclerViewCategories.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewCategories.setAdapter(recyclerViewAdapter);

        tvTotal1 = (TextView) view.findViewById(R.id.tvTotalTotal);
        tvCurrency = (TextView) view.findViewById(R.id.tvCurrency);
        tvCurrency.setText(MainActivity.settingsData.getCurrency());

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

        updateGrandtotal();

        return view;
    }

    public void updateGrandtotal() {

        TOTAL = 0;
        for (Category category : categories) {
            TOTAL += category.getTotal();
        }

        tvTotal1.setText(TOTAL + "");

    }

    private void addNewCategory(Category category) {

        realm.beginTransaction();
        realm.copyToRealm(category);

        //added to current list
        categories.add(category);
        realm.commitTransaction();

        if (recyclerViewAdapter != null)
            recyclerViewLayoutManager.scrollToPosition(recyclerViewAdapter.getItemCount() - 1);

    }

    private void editCategory(Category categoryOld, Category categoryNew) {

        realm.beginTransaction();
        categoryOld.setNombre(categoryNew.getNombre());

        if (!categoryNew.getPicture().isEmpty()) {
            categoryOld.setPicture(categoryNew.getPicture());
        }

        //categoryOld.setTotal(categoryNew.getTotal());
        realm.copyToRealmOrUpdate(categoryOld);
        realm.commitTransaction();

        updateGrandtotal();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fabAddCategory) {

            CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString(CategoryDialogFragment.TITLE_EXTRA, getString(R.string.Main_Fragment_fab_dialog_title_create_category));
            bundle.putInt(CategoryDialogFragment.TYPE_EXTRA, CategoryDialogFragment.CREATION_TYPE);
            categoryDialogFragment.setArguments(bundle);

            categoryDialogFragment.setTargetFragment(this, RESULT_CREATE_CATEGORY_DIALOG);
            categoryDialogFragment.show(getFragmentManager(), CategoryDialogFragment.CATEGORY_DIALOG_FRAGMENT_EXTRA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (RESULT_CREATE_CATEGORY_DIALOG == requestCode) {

                if (data.getExtras().containsKey(CategoryDialogFragment.CATEGORY_EXTRA)) {

                    Category category = data.getExtras().getParcelable(CategoryDialogFragment.CATEGORY_EXTRA);
                    addNewCategory(category);

                }
            } else if (RecyclerViewAdapter.RESULT_EDIT_CATEGORY_DIALOG == requestCode) {

                Category categoryOld = data.getExtras().getParcelable(CategoryDialogFragment.CATEGORY_OLD_EXTRA);
                Category categoryNew = data.getExtras().getParcelable(CategoryDialogFragment.CATEGORY_NEW_EXTRA);
                editCategory(categoryOld, categoryNew);
            }
        }
    }

    @Override
    public void onChange(RealmList<Category> element) {
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (categories.isEmpty()) {

            if (!MainActivity.settingsData.getCategoryDefinedList().isEmpty()) {

                for (CategoryDefined categoryDefined : MainActivity.settingsData.getCategoryDefinedList()) {
                    addNewCategory(new Category(categoryDefined.getCategoryName()));
                }
            }
        }

        updateGrandtotal();

    }


}
