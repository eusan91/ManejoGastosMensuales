package com.santamaria.manejogastosmensuales.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Activities.CategoryDetailedActivity;
import com.santamaria.manejogastosmensuales.Activities.MainActivity;
import com.santamaria.manejogastosmensuales.Adapter.ListViewAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.Domain.CategoryMonth;
import com.santamaria.manejogastosmensuales.R;
import com.santamaria.manejogastosmensuales.app.MyApplication;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class LastMonthFragment extends Fragment implements AdapterView.OnItemClickListener {

    private RealmList<Category> categories;
    private ListView listViewCategorias;
    private ListViewAdapter listViewAdapter;
    private TextView tvCurrency;
    private float TOTAL;
    private TextView tvTotal1;

    Realm realm;

    public LastMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm = realm.getDefaultInstance();

        Number idNumber = realm.where(CategoryMonth.class).equalTo(MyApplication.CURRENT_MONTH_COLUMN, false).findAll().where().max(MyApplication.ID_COLUMN);
        CategoryMonth lastCategoryMonth = null;
        if (idNumber != null) {
            lastCategoryMonth = realm.where(CategoryMonth.class).equalTo(MyApplication.ID_COLUMN, idNumber.intValue()).findFirst();
        }

        if (lastCategoryMonth != null) {
            categories = lastCategoryMonth.getCategoryList();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_last_month, container, false);

        listViewCategorias = (ListView) view.findViewById(R.id.ListViewCategorias);
        listViewAdapter = new ListViewAdapter(categories, getContext(), R.layout.listview_cardview_item);
        listViewCategorias.setAdapter(listViewAdapter);
        listViewCategorias.setOnItemClickListener(this);

        tvCurrency = (TextView) view.findViewById(R.id.tvCurrency);
        tvCurrency.setText(MainActivity.settingsData.getCurrency());

        tvTotal1 = (TextView) view.findViewById(R.id.tvTotalTotal);

        if (categories != null && categories.size() > 0) {
            updateGrandtotal();
        } else {

            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutExternal);
            relativeLayout.setVisibility(View.INVISIBLE);

        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent intent = new Intent(getContext(), CategoryDetailedActivity.class);
        intent.putExtra(CategoryDetailedActivity.CATEGORY_ID_EXTRA, categories.get(position).getId());
        intent.putExtra(CategoryDetailedActivity.CREATION_EXTRA, false);
        startActivity(intent);

    }

    public void updateGrandtotal() {

        TOTAL = 0;
        for (Category category : categories) {
            TOTAL += category.getTotal();
        }

        tvTotal1.setText(TOTAL + "");

    }
}
