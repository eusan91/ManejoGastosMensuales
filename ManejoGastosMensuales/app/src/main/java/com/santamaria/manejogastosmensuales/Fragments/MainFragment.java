package com.santamaria.manejogastosmensuales.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.ListViewAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import java.util.LinkedList;
import java.util.List;

public class MainFragment extends Fragment {


    private FloatingActionButton fabAdd;
    List<Category> categoryList;
    ListView listViewCategorias;
    ListViewAdapter listViewAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        listViewCategorias = (ListView) view.findViewById(R.id.ListViewCategorias);

        categoryList = new LinkedList<>();

        categoryList.add(new Category("Gasolina", R.drawable.under_construct, (float) 0.0));
        categoryList.add(new Category("Servicios", R.drawable.under_construct, (float) 0.0));
        categoryList.add(new Category("Comida", R.drawable.under_construct, (float) 0.0));

        listViewAdapter = new ListViewAdapter(categoryList, getContext(), R.layout.cardview_item);

        listViewCategorias.setAdapter(listViewAdapter);

        //fab action
        final FloatingActionButton fabAdd = (FloatingActionButton) view.findViewById(R.id.fabAddCategory);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertCreateCategory("Create new Category", "");

            }
        });

        return view;
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
                    categoryList.add(new Category(categoryName, R.drawable.under_construct, (float) 0));
                    listViewAdapter.notifyDataSetChanged();
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

}
