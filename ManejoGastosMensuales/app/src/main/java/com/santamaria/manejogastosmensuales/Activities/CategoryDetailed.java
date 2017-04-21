package com.santamaria.manejogastosmensuales.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.santamaria.manejogastosmensuales.Adapter.CategoryDetailedAdapter;
import com.santamaria.manejogastosmensuales.Domain.CategoryDetail;
import com.santamaria.manejogastosmensuales.R;

import java.util.LinkedList;
import java.util.List;

public class CategoryDetailed extends AppCompatActivity {

    private ListView listViewDetail;
    private Toolbar myToolbar = null;
    CategoryDetailedAdapter adapter;
    List<CategoryDetail> categoryDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detailed);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setToolbar();

        listViewDetail = (ListView) findViewById(R.id.listViewDetail);

        CategoryDetail categoryDetail1 = new CategoryDetail("detalle1", 100);
        CategoryDetail categoryDetail2 = new CategoryDetail("detalle2", 200);
        CategoryDetail categoryDetail3 = new CategoryDetail("detalle3", 300);
        CategoryDetail categoryDetail4 = new CategoryDetail("detalle4", 400);

        categoryDetailList = new LinkedList<>();
        categoryDetailList.add(categoryDetail1);
        categoryDetailList.add(categoryDetail2);
        categoryDetailList.add(categoryDetail3);
        categoryDetailList.add(categoryDetail4);

        adapter = new CategoryDetailedAdapter(this, R.layout.category_detailed_item, categoryDetailList);

        listViewDetail.setAdapter(adapter);

    }

    private void setToolbar(){
        setSupportActionBar(myToolbar);
    }

    public void fabAddNewDetail(View view) {

        CategoryDetail categoryDetail = new CategoryDetail("detalle" + (adapter.getCount()+1), ((adapter.getCount()+1)*100));
        categoryDetailList.add(categoryDetail);

        adapter.notifyDataSetChanged();

    }
}
