package com.santamaria.manejogastosmensuales.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.PagerAdapter;
import com.santamaria.manejogastosmensuales.R;

public class MainActivity extends AppCompatActivity {

    Toolbar myToolbar = null;
    TabLayout tabLayout = null;
    ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        setToolbar();
        setTabLayout();
        setViewPager();

        //usado para definir el icono en el toolbar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //Select the second tab as default one.
        tabLayout.getTabAt(1).select();

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
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
                Toast.makeText(this, "Me has presionado, me saldré", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
