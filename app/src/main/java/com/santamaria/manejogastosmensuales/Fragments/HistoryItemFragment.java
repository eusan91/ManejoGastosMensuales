package com.santamaria.manejogastosmensuales.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.santamaria.manejogastosmensuales.app.MyApplication;

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
            month = bundle.getInt(HistoryFragment.MONTH_BUNDLE_EXTRA);
            year = bundle.getInt(HistoryFragment.YEAR_BUNDLE_EXTRA);
        }
        CategoryMonth lastCategoryMonth;

        if (month == (int) Calendar.getInstance().get(Calendar.MONTH) + 1
                && year == (int) Calendar.getInstance().get(Calendar.YEAR)) {

            categories = null;

        } else {

            realm = realm.getDefaultInstance();

            lastCategoryMonth = realm.where(CategoryMonth.class).equalTo(MyApplication.YEAR_COLUMN, year).findAll().where().equalTo(MyApplication.MONTH_COLUMN, month).findFirst();

            if (lastCategoryMonth != null) {
                categories = lastCategoryMonth.getCategoryList();
            }
        }

        listViewCategorias = view.findViewById(R.id.ListViewCategorias);
        listViewAdapter = new ListViewAdapter(categories, getContext(), R.layout.listview_cardview_item);
        listViewCategorias.setAdapter(listViewAdapter);
        listViewCategorias.setOnItemClickListener(this);

        tvCurrency = view.findViewById(R.id.tvCurrency);
        tvCurrency.setText(MainActivity.settingsData.getCurrency());

        tvTotal1 = view.findViewById(R.id.tvTotalTotal);

        if (categories != null && categories.size() > 0) {
            updateGrandtotal();
        } else {

            RelativeLayout relativeLayout = view.findViewById(R.id.relativeLayoutExternal);
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
