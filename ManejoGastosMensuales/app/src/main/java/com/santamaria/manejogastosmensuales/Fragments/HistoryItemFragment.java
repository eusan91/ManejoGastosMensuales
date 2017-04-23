package com.santamaria.manejogastosmensuales.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Activities.CategoryDetailedActivity;
import com.santamaria.manejogastosmensuales.Activities.MainActivity;
import com.santamaria.manejogastosmensuales.Adapter.ListViewAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.Domain.CategoryMonth;
import com.santamaria.manejogastosmensuales.R;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryItemFragment extends Fragment implements AdapterView.OnItemClickListener {

    private RealmList<Category> categories;
    private ListView listViewCategorias;
    private ListViewAdapter listViewAdapter;
    private TextView tvCurrency;
    private float TOTAL;
    private TextView tvTotal1;

    private int month;
    private int year;

    Realm realm;

    public HistoryItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_item, container, false);

        Bundle bundle = getArguments();

        if (bundle != null) {
            month = bundle.getInt("month");
            year = bundle.getInt("year");
        }

            realm = realm.getDefaultInstance();

            CategoryMonth lastCategoryMonth = realm.where(CategoryMonth.class).equalTo("year", year).findAll().where().equalTo("month", month).findFirst();

            if (lastCategoryMonth != null) {
                categories = lastCategoryMonth.getCategoryList();
            }

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
        intent.putExtra("categoryID", categories.get(position).getId());
        intent.putExtra("CREATION", false);
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
