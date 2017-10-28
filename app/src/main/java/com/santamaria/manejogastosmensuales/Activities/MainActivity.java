package com.santamaria.manejogastosmensuales.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.PagerAdapter;
import com.santamaria.manejogastosmensuales.Domain.SettingsData;
import com.santamaria.manejogastosmensuales.R;
import com.santamaria.manejogastosmensuales.app.MyApplication;

import java.util.Calendar;
import java.util.Locale;

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
    private RadioButton radioEnglish = null;
    private RadioButton radioSpanish = null;

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

    private void setToolbar() {
        setSupportActionBar(myToolbar);
    }

    private void setTabLayout() {

        tabLayout.addTab(tabLayout.newTab().setText(R.string.Main_Act_last_month));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Main_Act_current_month));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Main_Act_history));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

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

    private void setViewPager() {
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

        switch (item.getItemId()) {

            case R.id.exit_menu_item:
                this.finish();
                return true;

            case android.R.id.home:
                //open menu contextual
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        boolean closeNavDrawer = false;

        switch (item.getItemId()) {
            case R.id.defineCategories:
                closeNavDrawer = true;
                Intent intent = new Intent(this, DefineCategoriesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;

            case R.id.startMonth:
                closeNavDrawer = true;
                createAlertDialogStartMonth();
                break;

            case R.id.currency:
                closeNavDrawer = true;
                createAlertDialogCurrency();
                break;

            case R.id.language:
                closeNavDrawer = true;
                createAlertDialogLanguage();
                break;
        }

        if (closeNavDrawer) {
            drawerLayout.closeDrawers();
        }

        return closeNavDrawer;
    }

    private void restartApp() {
        Intent intent1 = getIntent();
        intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent1);
    }

    private void createAlertDialogStartMonth() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.Main_Act_Dialog_Start_of_Month);
        builder.setIcon(R.drawable.ic_start_month);
        builder.setMessage(R.string.Main_Act_Dialog_Start_of_Month_Message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_define_start_month_item, null);
        builder.setView(viewInflated);

        startMonthPicker = (NumberPicker) viewInflated.findViewById(R.id.NumberPickerDay);
        startMonthPicker.setMinValue(1);
        startMonthPicker.setMaxValue(31);
        startMonthPicker.setValue(settingsData.getStartMonth());


        builder.setPositiveButton(R.string.Main_Act_Dialog_Start_of_Month_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int value = startMonthPicker.getValue();
                setStartOfMonth(value);

                navigationView.setCheckedItem(R.id.menu_none);

            }
        }).setNegativeButton(R.string.Main_Act_Dialog_Start_of_Month_negative_button, new DialogInterface.OnClickListener() {
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

        builder.setTitle(R.string.Main_Act_Dialog_Set_Currency_Title);
        builder.setIcon(R.drawable.ic_currency);
        builder.setMessage(R.string.Main_Act_Dialog_Set_Currency_Message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_define_currency_item, null);
        builder.setView(viewInflated);

        currencyEditText = (EditText) viewInflated.findViewById(R.id.currencyInput);
        currencyEditText.setText(settingsData.getCurrency());

        builder.setPositiveButton(R.string.Main_Act_Dialog_Set_Currency_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String currency = currencyEditText.getText().toString().trim();

                if (currency.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.Main_Act_Dialog_Set_Currency_error_empty_currency, Toast.LENGTH_SHORT).show();
                } else {
                    setCurrency(currency);
                    navigationView.setCheckedItem(R.id.menu_none);
                }

            }
        }).setNegativeButton(R.string.Main_Act_Dialog_Set_Currency_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
                navigationView.setCheckedItem(R.id.menu_none);
            }
        });

        builder.create().show();
    }

    private void createAlertDialogLanguage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//R.string.Main_Act_Dialog_Set_Currency_Title
        builder.setTitle(R.string.Main_Act_Dialog_Set_Language_Title);
        builder.setIcon(R.drawable.ic_language);
        builder.setMessage(R.string.Main_act_Dialog_set_Language_Message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_define_language_item, null);
        builder.setView(viewInflated);

        radioEnglish = (RadioButton) viewInflated.findViewById(R.id.english);
        radioSpanish = (RadioButton) viewInflated.findViewById(R.id.spanish);

        if (settingsData.getLanguage().compareTo(MyApplication.ENGLISH_LANG) == 0) {
            radioEnglish.setChecked(true);
        } else {
            radioSpanish.setChecked(true);
        }

        //get the one from the system and check
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                boolean englishState = radioEnglish.isChecked();
                boolean spanishState = radioSpanish.isChecked();

                if (englishState && settingsData.getLanguage().compareTo(MyApplication.ENGLISH_LANG) != 0) {

                    updateLang(Locale.ENGLISH);
                    restartApp();

                } else if (spanishState && settingsData.getLanguage().compareTo(MyApplication.SPANISH_LANG) != 0) {

                    updateLang(new Locale("es", "ES"));
                    restartApp();

                }


            }
        }).setNegativeButton(R.string.Main_Act_Dialog_Set_Currency_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
                navigationView.setCheckedItem(R.id.menu_none);
            }
        });

        builder.create().show();
    }

    private void updateLang(Locale locale) {

        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        String newLang = "";

        if (locale == Locale.ENGLISH){
            newLang = MyApplication.ENGLISH_LANG;
        } else {
            newLang = MyApplication.SPANISH_LANG;
        }

        realm.beginTransaction();
        settingsData.setLanguage(newLang);
        realm.copyToRealmOrUpdate(settingsData);
        realm.commitTransaction();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        navigationView.setCheckedItem(R.id.menu_none);
    }

    private void setStartOfMonth(int value) {

        realm.beginTransaction();
        settingsData.setStartMonth(value);
        realm.copyToRealmOrUpdate(settingsData);
        realm.commitTransaction();
    }

    private void setCurrency(String currency) {

        realm.beginTransaction();
        settingsData.setCurrency(currency);
        realm.copyToRealmOrUpdate(settingsData);
        realm.commitTransaction();

        //restart app to update currency textViews
        restartApp();
    }

    @Override
    public void onChange(SettingsData element) {
    }
}
