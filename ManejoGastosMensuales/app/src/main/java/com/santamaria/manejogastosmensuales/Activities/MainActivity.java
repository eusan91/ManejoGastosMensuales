package com.santamaria.manejogastosmensuales.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.PagerAdapter;
import com.santamaria.manejogastosmensuales.Domain.SettingsData;
import com.santamaria.manejogastosmensuales.R;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        RealmChangeListener<SettingsData> {

    private Toolbar myToolbar = null;
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private DrawerLayout drawerLayout = null;
    private NavigationView navigationView = null;
    private NumberPicker startMonthPicker = null;
    private EditText currencyEditText = null;
    PagerAdapter pagerAdapter;

    public static SettingsData settingsData;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        //load settings
        loadSettings();

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        setToolbar();
        setTabLayout();
        setViewPager();

        //habilitar el icono
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Select the second tab as default one.
        tabLayout.getTabAt(1).select();
    }

    private void loadSettings() {

        settingsData = realm.where(SettingsData.class).findFirst();
    }

    private void setToolbar(){
        setSupportActionBar(myToolbar);
    }

    private void setTabLayout(){

        tabLayout.addTab(tabLayout.newTab().setText("Mes Anterior"));
        tabLayout.addTab(tabLayout.newTab().setText("Mes Actual"));
        tabLayout.addTab(tabLayout.newTab().setText("Historial"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setViewPager(){
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.exit_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.exit_menu_item:
                this.finish();
                return true;

            case android.R.id.home:
                //abrir menu contextual
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        boolean closeNavDrawer = false;

        switch (item.getItemId()){
            case R.id.defineCategories:
                closeNavDrawer = true;
                Intent intent = new Intent(this, DefineCategoriesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;

            case  R.id.startMonth:
                closeNavDrawer = true;
                createAlertDialogStartMonth();
                break;

            case R.id.currency:
                closeNavDrawer = true;
                createAlertDialogCurrency();
                break;
        }

        if (closeNavDrawer){
            drawerLayout.closeDrawers();
        }

        return closeNavDrawer;
    }

    private void createAlertDialogStartMonth() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Start of Month");
        builder.setIcon(R.drawable.ic_start_month);
        builder.setMessage("Define the day the app will reset data for new month:");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_define_start_month_item, null);
        builder.setView(viewInflated);

        startMonthPicker = (NumberPicker) viewInflated.findViewById(R.id.NumberPickerDay);
        startMonthPicker.setMinValue(1);
        startMonthPicker.setMaxValue(31);
        startMonthPicker.setValue(settingsData.getStartMonth());


        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int value = startMonthPicker.getValue();
                setStartOfMonth(value);

                navigationView.setCheckedItem(R.id.menu_none);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
                navigationView.setCheckedItem(R.id.menu_none);
            }
        });

        builder.create().show();
    }

    private void createAlertDialogCurrency() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Set Currency");
        builder.setIcon(R.drawable.ic_currency);
        builder.setMessage("Define currency the app will use:");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_define_currency_item, null);
        builder.setView(viewInflated);

        currencyEditText = (EditText) viewInflated.findViewById(R.id.currencyInput);
        currencyEditText.setText(settingsData.getCurrency());

        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String currency = currencyEditText.getText().toString().trim();

                if (currency.isEmpty()){
                    Toast.makeText(MainActivity.this, "Currency can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    setCurrency(currency);
                    navigationView.setCheckedItem(R.id.menu_none);
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
                navigationView.setCheckedItem(R.id.menu_none);
            }
        });

        builder.create().show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        navigationView.setCheckedItem(R.id.menu_none);
    }

    private void setStartOfMonth(int value){

        realm.beginTransaction();
        settingsData.setStartMonth(value);
        realm.copyToRealmOrUpdate(settingsData);
        realm.commitTransaction();

    }

    private void setCurrency(String currency){

        realm.beginTransaction();
        settingsData.setCurrency(currency);
        realm.copyToRealmOrUpdate(settingsData);
        realm.commitTransaction();

        //restart app to update currency textViews
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    @Override
    public void onChange(SettingsData element) {}
}
