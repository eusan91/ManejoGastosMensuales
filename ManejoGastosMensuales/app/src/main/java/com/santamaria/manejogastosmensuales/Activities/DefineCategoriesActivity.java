package com.santamaria.manejogastosmensuales.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.CategoryDefinedAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class DefineCategoriesActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<CategoryDefined>> {

    private EditText categoryNameInput = null;
    private ListView listView;
    private RealmResults<CategoryDefined> categoryDefinedRealmResults;
    private CategoryDefinedAdapter adapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_categories);

        setTitle("Define category list");

        realm = Realm.getDefaultInstance();
        categoryDefinedRealmResults = realm.where(CategoryDefined.class).findAll();
        categoryDefinedRealmResults.addChangeListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listviewDefineCategories);
        adapter = new CategoryDefinedAdapter(this, categoryDefinedRealmResults, R.layout.listview_category_defined_item);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show AlertDialog
                createAlertDialog();

            }
        });
    }

    private void createAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Create new Category");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_define_category_item, null);
        builder.setView(viewInflated);

        categoryNameInput = (EditText) viewInflated.findViewById(R.id.categoryNameDefinedInput);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String categoryName = categoryNameInput.getText().toString().trim();

                if (categoryName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Category name can not be empty value", Toast.LENGTH_SHORT).show();
                } else {
                    addNewCategory(categoryName);
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

    private void addNewCategory(String categoryName) {

        realm.beginTransaction();
        realm.copyToRealm(new CategoryDefined(categoryName));
        realm.commitTransaction();


    }

    @Override
    public void onChange(RealmResults<CategoryDefined> element) {
        adapter.notifyDataSetChanged();
    }
}
