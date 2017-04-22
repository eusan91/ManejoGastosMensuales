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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Activities.CategoryDetailed;
import com.santamaria.manejogastosmensuales.Adapter.RecyclerViewAdapter;
import com.santamaria.manejogastosmensuales.CategoryDialogFragment;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainFragment extends Fragment implements View.OnClickListener, RealmChangeListener<RealmResults<Category>> {

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

        categories.addChangeListener(this);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recyclerViewCategorias);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());

        recyclerViewAdapter = new RecyclerViewAdapter(categories, R.layout.recycler_cardview_item,
                new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Category category, int position) {

                        Intent intent = new Intent(getContext(), CategoryDetailed.class);
                        intent.putExtra("categoryID", categories.get(position).getId());
                        startActivity(intent);

                    }
                }, getActivity().getMenuInflater());

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Context Menu");
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.options_card_view_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.editCardView:
                Toast.makeText(getActivity(), "El Item a editar es: " + info.position, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void addNewCategory(Category category) {

        realm.beginTransaction();
        realm.copyToRealm(category);
        realm.commitTransaction();

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fabAddCategory) {

            CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString("title", "Create new Category");
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
            }
        }
    }

    @Override
    public void onChange(RealmResults<Category> element) {
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewLayoutManager.scrollToPosition(recyclerViewAdapter.getItemCount()-1);
    }
}
