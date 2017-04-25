package com.santamaria.manejogastosmensuales.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.CategoryDefinedAdapter;
import com.santamaria.manejogastosmensuales.Domain.CategoryDefined;
import com.santamaria.manejogastosmensuales.Domain.SettingsData;
import com.santamaria.manejogastosmensuales.R;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class DefineCategoriesActivity extends AppCompatActivity
        implements RealmChangeListener<RealmList<CategoryDefined>> {

    private EditText categoryNameInput = null;
    private ListView listView;
    private RealmList<CategoryDefined> categoryDefinedRealmList;
    private CategoryDefinedAdapter adapter;
    private Realm realm;
    private SettingsData settingsData;

    private final int CREATE_CATEGORY = 1;
    private final int UPDATE_CATEGORY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_categories);

        setTitle(getString(R.string.Define_Categories_Act_title));

        realm = Realm.getDefaultInstance();
        settingsData = MainActivity.settingsData;
        categoryDefinedRealmList = settingsData.getCategoryDefinedList();
        categoryDefinedRealmList.addChangeListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listviewDefineCategories);
        adapter = new CategoryDefinedAdapter(this, categoryDefinedRealmList, R.layout.listview_category_defined_item);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show AlertDialog
                createAlertDialog(CREATE_CATEGORY, null);

            }
        });


        //usado para definir el icono en el toolbar
        getSupportActionBar().setIcon(R.drawable.ic_create_categories_dark);

        //set context menu to listview
        registerForContextMenu(listView);
    }

    private void createAlertDialog(final int dialogType, final CategoryDefined categoryDefined) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_define_category_item, null);
        builder.setView(viewInflated);

        categoryNameInput = (EditText) viewInflated.findViewById(R.id.categoryNameDefinedInput);

        String positiveButtonText = "";
        if (dialogType == CREATE_CATEGORY) {
            builder.setTitle(R.string.Define_Categories_Act_Dialog_title_create_category);
            positiveButtonText = getString(R.string.Define_Categories_Act_Dialog_create_category_button_add);
        } else {
            builder.setTitle(R.string.Define_Categories_Act_Dialog_title_edit_category);
            positiveButtonText = getString(R.string.Define_Categories_Act_Dialog_edit_category_button_update);
            categoryNameInput.setText(categoryDefined.getCategoryName());
        }

        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String categoryName = categoryNameInput.getText().toString().trim();

                if (categoryName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.Define_Categories_Act_Dialog_error_category_name_empty, Toast.LENGTH_SHORT).show();
                } else {
                    if (dialogType == CREATE_CATEGORY) {
                        addNewCategory(categoryName);
                    } else {
                        editCategory(categoryName, categoryDefined);

                    }
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
        CategoryDefined categoryDefined = new CategoryDefined(categoryName);
        realm.copyToRealm(categoryDefined);
        settingsData.getCategoryDefinedList().add(categoryDefined);
        realm.commitTransaction();

    }

    private void editCategory(String categoryName, CategoryDefined categoryDefined) {

        realm.beginTransaction();
        categoryDefined.setCategoryName(categoryName);
        realm.copyToRealmOrUpdate(categoryDefined);
        realm.commitTransaction();

    }

    private void removeCategory(CategoryDefined categoryDefined) {

        realm.beginTransaction();
        categoryDefined.deleteFromRealm();
        realm.commitTransaction();

    }

    //context menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(categoryDefinedRealmList.get(info.position).getCategoryName());
        getMenuInflater().inflate(R.menu.options_card_view_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.editCardView:
                createAlertDialog(UPDATE_CATEGORY, categoryDefinedRealmList.get(info.position));
                return true;
            case R.id.deleteCardView:
                removeCategory(categoryDefinedRealmList.get(info.position));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onChange(RealmList<CategoryDefined> element) {
        adapter.notifyDataSetChanged();
    }
}
