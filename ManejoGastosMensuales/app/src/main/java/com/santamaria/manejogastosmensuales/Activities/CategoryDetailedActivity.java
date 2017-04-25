package com.santamaria.manejogastosmensuales.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Adapter.CategoryDetailedAdapter;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.Domain.CategoryDetail;
import com.santamaria.manejogastosmensuales.R;
import com.santamaria.manejogastosmensuales.app.MyApplication;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class CategoryDetailedActivity extends AppCompatActivity implements View.OnClickListener,
        RealmChangeListener<Category> {

    private static final int DETAIL_CREATION = 100;
    private static final int DETAIL_EDITION = 101;

    public static final String CATEGORY_ID_EXTRA = "categoryID";
    public static final String CREATION_EXTRA = "CREATION";

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
    private FloatingActionButton fabAdd;

    private int categoryID;
    private Realm realm;
    private Category category;
    private CategoryDetail categoryDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detailed);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setToolbar();

        listViewDetail = (ListView) findViewById(R.id.listViewDetail);

        Intent intent = getIntent();
        categoryID = intent.getIntExtra(CATEGORY_ID_EXTRA, -1);
        boolean CREATION = intent.getBooleanExtra(CREATION_EXTRA, true);

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAddDetail);
        if (!CREATION) {
            fabAdd.hide();
        }

        if (categoryID >= 0) {
            realm = Realm.getDefaultInstance();

            category = realm.where(Category.class).equalTo(MyApplication.ID_COLUMN, categoryID).findFirst();
            category.addChangeListener(this);
            categoryDetailList = category.getCategoryDetailList();

            this.setTitle(category.getNombre());

            adapter = new CategoryDetailedAdapter(this, R.layout.category_detailed_item, categoryDetailList);

            listViewDetail.setAdapter(adapter);
        } else {
            finish();
        }

        registerForContextMenu(listViewDetail);

    }

    private void setToolbar() {
        setSupportActionBar(myToolbar);
    }

    public void fabAddNewDetail(View view) {

        createAlertDialog(DETAIL_CREATION);

    }

    private void createAlertDialog(int alertType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_detail, null);
        builder.setView(viewInflated);

        detailInput = (EditText) viewInflated.findViewById(R.id.newDetail);
        amountInput = (EditText) viewInflated.findViewById(R.id.newAmount);
        okButton = (TextView) viewInflated.findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
        okButton.setTag(alertType); // --> this way we can identify what to do in the button action

        cancelButton = (TextView) viewInflated.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);


        if (alertType == DETAIL_CREATION) {
            builder.setTitle(R.string.Category_Detailed_Act_Creation_title);
        } else {
            builder.setTitle(R.string.Category_Detailed_Act_Edition_title);
            detailInput.setText(categoryDetail.getDetail());
            amountInput.setText(categoryDetail.getAmount() + "");
        }

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void addNewCategoryDetail(CategoryDetail categoryDetail) {

        realm.beginTransaction();
        realm.copyToRealm(categoryDetail);
        category.getCategoryDetailList().add(categoryDetail);
        category.setTotal(category.getTotal() + categoryDetail.getAmount());
        realm.commitTransaction();
    }

    private void deleteCategoryDetail(CategoryDetail categoryDetail) {

        realm.beginTransaction();
        category.setTotal(category.getTotal() - categoryDetail.getAmount());
        categoryDetail.deleteFromRealm();
        realm.commitTransaction();
    }

    private void editCategoryDetail(CategoryDetail categoryDetail, String detail, float amount) {

        realm.beginTransaction();
        category.setTotal((category.getTotal() - categoryDetail.getAmount()) + amount);
        categoryDetail.setDetail(detail);
        categoryDetail.setAmount(amount);
        realm.copyToRealmOrUpdate(categoryDetail);
        realm.commitTransaction();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.okButton) {

            int alertType = (Integer) okButton.getTag();

            String detail = detailInput.getText().toString().trim();

            if (detail.isEmpty()) {
                detailInput.setError(getString(R.string.Category_Detailed_Act_Error_detail_name_empty));

            } else {

                String amount = amountInput.getText().toString().trim();

                if (amount.isEmpty()) {
                    amountInput.setError(getString(R.string.Category_Detailed_Act_Error_detail_amount_empty));

                } else {

                    float amountF = Float.parseFloat(amount);
                    if (alertType == DETAIL_CREATION) {
                        CategoryDetail categoryDetail = new CategoryDetail(detail, amountF);
                        addNewCategoryDetail(categoryDetail);
                        alertDialog.dismiss();
                    } else {
                        editCategoryDetail(categoryDetail, detail, amountF);
                        alertDialog.dismiss();
                    }
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

//ContextMenu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(categoryDetailList.get(info.position).getDetail());
        getMenuInflater().inflate(R.menu.options_category_detailed_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.deleteDetail:
                deleteCategoryDetail(categoryDetailList.get(info.position));
                return true;
            case R.id.editDetail:
                categoryDetail = categoryDetailList.get(info.position);
                createAlertDialog(DETAIL_EDITION);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    //End Context Menu
}
