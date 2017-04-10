package com.santamaria.manejogastosmensuales.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.santamaria.manejogastosmensuales.Adapter.ListViewAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import java.util.LinkedList;
import java.util.List;

public class LastMonthFragment extends Fragment {

    List<Category> categoryList;
    ListView listViewCategorias;
    ListViewAdapter listViewAdapter;

    public LastMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_last_month, container, false);

        listViewCategorias = (ListView) view.findViewById(R.id.ListViewCategorias);

        categoryList = new LinkedList<>();

        categoryList.add(new Category("Gasolina", R.drawable.under_construct, (float) 100.0));
        categoryList.add(new Category("Servicios", R.drawable.under_construct, (float) 200.0));
        categoryList.add(new Category("Comida", R.drawable.under_construct, (float) 300.0));

        listViewAdapter = new ListViewAdapter(categoryList, getContext(), R.layout.listview_cardview_item);

        listViewCategorias.setAdapter(listViewAdapter);

        return view;
    }

}
