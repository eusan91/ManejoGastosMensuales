package com.santamaria.manejogastosmensuales.app;

import android.app.Application;

import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.Domain.CategoryDefined;
import com.santamaria.manejogastosmensuales.Domain.CategoryDetail;
import com.santamaria.manejogastosmensuales.Domain.CategoryMonth;
import com.santamaria.manejogastosmensuales.Domain.SettingsData;

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


    @Override
    public void onCreate() {
        super.onCreate();
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();

        CategoryID = setAtomicId(realm, Category.class);
        CategoryDetailID = setAtomicId(realm, CategoryDetail.class);
        CategoryMonthID = setAtomicId(realm, CategoryMonth.class);
        CategoryDefinedID = setAtomicId(realm, CategoryDefined.class);

        SettingsData settingsData= realm.where(SettingsData.class).findFirst();

        if (settingsData == null){
            initSettings(realm, new SettingsData(1, null, "$"));
        }

        realm.close();

    }

    private void setUpRealmConfig(){

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger setAtomicId(Realm realm, Class<T> anyClass) {

        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }

    private void initSettings(Realm realm, SettingsData settingsData){

        realm.beginTransaction();
        realm.copyToRealm(settingsData);
        realm.commitTransaction();
    }


}
