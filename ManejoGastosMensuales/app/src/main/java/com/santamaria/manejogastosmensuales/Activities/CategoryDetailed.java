package com.santamaria.manejogastosmensuales.Activities;

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
import com.santamaria.manejogastosmensuales.Domain.CategoryDetail;
import com.santamaria.manejogastosmensuales.R;

import java.util.LinkedList;
import java.util.List;

public class CategoryDetailed extends AppCompatActivity implements View.OnClickListener {

    private ListView listViewDetail;
    private Toolbar myToolbar = null;
    CategoryDetailedAdapter adapter;
    List<CategoryDetail> categoryDetailList;

    // variables for AlertDialog
    private EditText detailInput;
    private EditText amountInput;
    private TextView okButton;
    private TextView cancelButton;
    private AlertDialog alertDialog;

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
        categoryDetailList.add(categoryDetail);
        adapter.notifyDataSetChanged();
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
}
