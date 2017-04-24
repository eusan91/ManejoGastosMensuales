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
import com.santamaria.manejogastosmensuales.Domain.CategoryMonth;
import com.santamaria.manejogastosmensuales.R;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class LastMonthFragment extends Fragment {

    private RealmList<Category> categories;
    ListView listViewCategorias;
    ListViewAdapter listViewAdapter;

    Realm realm;

    public LastMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm = realm.getDefaultInstance();

        int id = realm.where(CategoryMonth.class).equalTo("currentMonth", false).findAll().where().max("id").intValue();
        CategoryMonth lastCategoryMonth = realm.where(CategoryMonth.class).equalTo("id", id).findFirst();

        if (lastCategoryMonth != null ){
         categories = lastCategoryMonth.getCategoryList();
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_last_month, container, false);

        listViewCategorias = (ListView) view.findViewById(R.id.ListViewCategorias);

        listViewAdapter = new ListViewAdapter(categories, getContext(), R.layout.listview_cardview_item);

        listViewCategorias.setAdapter(listViewAdapter);

        return view;
    }

}
