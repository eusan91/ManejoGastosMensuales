package com.santamaria.manejogastosmensuales.app;

import android.app.Application;
import android.content.res.Configuration;

import com.facebook.stetho.Stetho;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.Domain.CategoryDefined;
import com.santamaria.manejogastosmensuales.Domain.CategoryDetail;
import com.santamaria.manejogastosmensuales.Domain.CategoryMonth;
import com.santamaria.manejogastosmensuales.Domain.SettingsData;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Santamaria on 13/04/2017.
 */

public class MyApplication extends Application {

    public static AtomicInteger CategoryID = new AtomicInteger();
    public static AtomicInteger CategoryDetailID = new AtomicInteger();
    public static AtomicInteger CategoryMonthID = new AtomicInteger();
    public static AtomicInteger CategoryDefinedID = new AtomicInteger();

    public static String ID_COLUMN = "id";
    public static String CURRENT_MONTH_COLUMN = "currentMonth";
    public static String YEAR_COLUMN = "year";
    public static String MONTH_COLUMN = "month";
    public static String ENGLISH_LANG = "english";
    public static String SPANISH_LANG = "spanish";

    @Override
    public void onCreate() {
        super.onCreate();
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();

        CategoryID = setAtomicId(realm, Category.class);
        CategoryDetailID = setAtomicId(realm, CategoryDetail.class);
        CategoryMonthID = setAtomicId(realm, CategoryMonth.class);
        CategoryDefinedID = setAtomicId(realm, CategoryDefined.class);

        SettingsData settingsData = realm.where(SettingsData.class).findFirst();

        if (settingsData == null) {
            initSettings(realm, new SettingsData(1, null, "$", ENGLISH_LANG));
        } else {
            //change the language
            if (settingsData.getLanguage().compareTo(SPANISH_LANG)==0){
                Configuration config = new Configuration();
                config.setLocale(new Locale("es","Es"));
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            } else {
                Configuration config = new Configuration();
                config.setLocale(Locale.ENGLISH);
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }

        }

        loadData(realm, settingsData);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        realm.close();

    }

    private void loadData(Realm realm, SettingsData settingsData) {

        CategoryMonth categoryMonth = realm.where(CategoryMonth.class).equalTo(CURRENT_MONTH_COLUMN, true).findFirst();

        if (categoryMonth != null) {

            //si estamos en el mismo año
            if (categoryMonth.getYear() == (int)Calendar.getInstance().get(Calendar.YEAR)) {

                //obtiene el siguiente mes
                int nextMonth = categoryMonth.getMonth() + 1;

                //si ya estamos en el mismo mes de corte
                int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;

                if (nextMonth == currentMonth ) {

                    int maxDayOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

                    //verifica fechas
                    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                    //si es el dia de corte
                    if (currentDay == settingsData.getStartMonth()){
                        updateCategoryMonthNotCurrent(realm, categoryMonth);
                        initCategoryMonth(realm);

                        //si el corte es superior al último día disponible del mes y estamos en ese dia
                    } else if (maxDayOfMonth <  settingsData.getStartMonth() && currentDay == maxDayOfMonth){
                        updateCategoryMonthNotCurrent(realm, categoryMonth);
                        initCategoryMonth(realm);
                    } else if (settingsData.getStartMonth() < currentDay){
                        updateCategoryMonthNotCurrent(realm, categoryMonth);
                        initCategoryMonth(realm);
                    }

                    //si el mes actual es mayor que el mes actual (2  o + meses adelante)
                } else if (currentMonth > nextMonth) {
                    updateCategoryMonthNotCurrent(realm, categoryMonth);
                    initCategoryMonth(realm);
                }

            } else {
                //si no estamos en el mismo año, crea un nuevo month category
                updateCategoryMonthNotCurrent(realm, categoryMonth);
                initCategoryMonth(realm);

            }

        } else {
            //si es null, primera vez.
            initCategoryMonth(realm);
        }
    }

    private void setUpRealmConfig() {

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger setAtomicId(Realm realm, Class<T> anyClass) {

        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max(ID_COLUMN).intValue()) : new AtomicInteger();
    }

    private void initSettings(Realm realm, SettingsData settingsData) {

        realm.beginTransaction();
        realm.copyToRealm(settingsData);
        realm.commitTransaction();
    }

    private void initCategoryMonth(Realm realm) {

        realm.beginTransaction();
        realm.copyToRealm(new CategoryMonth());
        realm.commitTransaction();
    }

    private void updateCategoryMonthNotCurrent(Realm realm, CategoryMonth categoryMonth) {

        realm.beginTransaction();
        categoryMonth.setCurrentMonth(false);
        realm.copyToRealmOrUpdate(categoryMonth);
        realm.commitTransaction();
    }



}
