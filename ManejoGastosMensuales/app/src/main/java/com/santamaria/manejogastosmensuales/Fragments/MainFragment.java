package com.santamaria.manejogastosmensuales.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.RecyclerViewAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import java.util.LinkedList;
import java.util.List;

public class MainFragment extends Fragment {


    private FloatingActionButton fabAdd;
    List<Category> categoryList;
    RecyclerView recyclerViewCategories;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recyclerViewCategorias);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());

        categoryList = new LinkedList<>();

        categoryList.add(new Category("Gasolina", R.drawable.under_construct, (float) 0.0));
        categoryList.add(new Category("Servicios", R.drawable.under_construct, (float) 0.0));
        categoryList.add(new Category("Comida", R.drawable.under_construct, (float) 0.0));

        recyclerViewAdapter = new RecyclerViewAdapter(categoryList, R.layout.recycler_cardview_item,
                new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category category, int position) {

                Toast.makeText(getActivity(), "Prueba 1,2,3, me escuchan?", Toast.LENGTH_SHORT).show();

            }
        }, getActivity().getMenuInflater());

        recyclerViewCategories.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewCategories.setAdapter(recyclerViewAdapter);

        //fab action
        final FloatingActionButton fabAdd = (FloatingActionButton) view.findViewById(R.id.fabAddCategory);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertCreateCategory("Create new Category", "");

            }
        });

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


        //contextMenu
        //registerForContextMenu(recyclerViewCategories);

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
            case R.id.deleteCardView:
                removeCategory(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void alertCreateCategory(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (!title.isEmpty()) {
            builder.setTitle(title);
        }

        if (!message.isEmpty()) {
            builder.setMessage(message);
        }

        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_category, null);
        builder.setView(viewInflated);

        final EditText categoryNameInput = (EditText) viewInflated.findViewById(R.id.categoryNameInput);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String categoryName = categoryNameInput.getText().toString().trim();

                if (!categoryName.isEmpty()) {

                    addNewCategory(new Category(categoryName, R.drawable.under_construct, (float) 0));

                } else {
                    Toast.makeText(getActivity(), "The name is required to create a new Category", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
            }
        });

        builder.create().show();

    }

    private void addNewCategory(Category category) {

        int position = categoryList.size();
        categoryList.add(category);
        recyclerViewAdapter.notifyItemInserted(position);
        recyclerViewLayoutManager.scrollToPosition(position);

    }

    private void removeCategory(int position) {

        categoryList.remove(position);
        recyclerViewAdapter.notifyItemRemoved(position);
    }

}
