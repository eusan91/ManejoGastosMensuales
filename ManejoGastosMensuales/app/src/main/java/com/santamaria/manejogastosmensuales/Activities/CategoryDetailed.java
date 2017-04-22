package com.santamaria.manejogastosmensuales.Activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Adapter.CategoryDetailedAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.Domain.CategoryDetail;
import com.santamaria.manejogastosmensuales.R;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class CategoryDetailed extends AppCompatActivity implements View.OnClickListener, RealmChangeListener<Category> {

    private ListView listViewDetail;
    private Toolbar myToolbar = null;
    private CategoryDetailedAdapter adapter;
    private RealmList<CategoryDetail> categoryDetailList;

    // variables for AlertDialog
    private EditText detailInput;
    private EditText amountInput;
    private TextView okButton;
    private TextView cancelButton;
    private AlertDialog alertDialog;

    private int categoryID;
    private Realm realm;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detailed);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setToolbar();

        listViewDetail = (ListView) findViewById(R.id.listViewDetail);

        Intent intent = getIntent();
        categoryID = intent.getIntExtra("categoryID",-1);

        if (categoryID >= 0) {
            realm = Realm.getDefaultInstance();

            category = realm.where(Category.class).equalTo("id", categoryID).findFirst();
            category.addChangeListener(this);
            categoryDetailList = category.getCategoryDetailList();

            this.setTitle(category.getNombre());

            adapter = new CategoryDetailedAdapter(this, R.layout.category_detailed_item, categoryDetailList);

            listViewDetail.setAdapter(adapter);
        } else {
            finish();
        }

    }

    private void setToolbar(){
        setSupportActionBar(myToolbar);
    }

    public void fabAddNewDetail(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create new category detail");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_detail, null);
        builder.setView(viewInflated);

        detailInput = (EditText) viewInflated.findViewById(R.id.newDetail);
        amountInput = (EditText) viewInflated.findViewById(R.id.newAmount);
        okButton = (TextView) viewInflated.findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
        cancelButton = (TextView) viewInflated.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        alertDialog = builder.create();
        alertDialog.show();

    }


    private void addNewCategoryDetail(CategoryDetail categoryDetail) {

        realm.beginTransaction();
        realm.copyToRealm(categoryDetail);
        category.getCategoryDetailList().add(categoryDetail);
        category.setTotal(category.getTotal()+categoryDetail.getAmount());
        realm.commitTransaction();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.okButton) {

            String detail = detailInput.getText().toString().trim();

            if (detail.isEmpty()) {
                detailInput.setError("Detail name should not be blank");

            } else {

                String amount = amountInput.getText().toString().trim();

                if (amount.isEmpty()) {
                    amountInput.setError("Amount should not be blank");

                } else {

                    float amountF = Float.parseFloat(amount);

                    CategoryDetail categoryDetail = new CategoryDetail(detail, amountF);
                    addNewCategoryDetail(categoryDetail);
                    alertDialog.dismiss();

                }
            }

        } else if (view.getId() == R.id.cancelButton){
            alertDialog.dismiss();
        }
    }

    @Override
    public void onChange(Category element) {
        adapter.notifyDataSetChanged();
    }
}
