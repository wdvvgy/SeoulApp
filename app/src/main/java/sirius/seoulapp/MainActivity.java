package sirius.seoulapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import sirius.seoulapp.map.AutoSearchingReceiver;
import sirius.seoulapp.map.MapsFragment;
import sirius.seoulapp.seouldata.LoadingPlacesFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapsFragment mapsFragment;
    private MustVisitListFragment mustVisitListFragment;
    private final String TAG = getClass().getName();
    private LoadingPlacesFragment loadingPlacesFragment;
    private IntentFilter intentFilter;
    private AutoSearchingReceiver autoSearchingReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragments();
        InitViews();
        autoSearchingReceiver = new AutoSearchingReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(AutoSearchingReceiver.mBroadcastStringAction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(autoSearchingReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(autoSearchingReceiver);
    }

    private void InitViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, loadingPlacesFragment);
        fragmentTransaction.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
            Drawable menu = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_dehaze_white_24dp);
            supportActionBar.setHomeAsUpIndicator(menu);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.mapmarker) {
                            replaceFragment(R.id.fragment_container, mapsFragment, mustVisitListFragment);

                        } else if(menuItem.getItemId() == R.id.seelist){
                            replaceFragment(R.id.fragment_container, mustVisitListFragment, mapsFragment);
                        } else if(menuItem.getItemId() == R.id.autosearch){
                            if(!mapsFragment.getisRunningAutoSearch()){
                                mapsFragment.startAutoSearch();
                                menuItem.setTitle("관광지 자동검색 OFF");
                                menuItem.setChecked(true);
                            }
                            else{
                                mapsFragment.stopAutoSearch();
                                menuItem.setTitle("관광지 자동검색 ON");
                                menuItem.setChecked(false);
                            }
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        navigationView.getMenu().getItem(1).setChecked(true);

    }

    private void setFragments(){
        fragmentManager = getSupportFragmentManager();
        mapsFragment = new MapsFragment();
        mustVisitListFragment = new MustVisitListFragment();
        loadingPlacesFragment = new LoadingPlacesFragment();
        loadingPlacesFragment.setMapsFragment(mapsFragment);
        loadingPlacesFragment.setMustVisitListFragment(mustVisitListFragment);
    }

    private void replaceFragment(int containerId, Fragment fragment, Fragment oldfragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        if(fragment.isAdded()){
            fragmentTransaction.hide(oldfragment);
            fragmentTransaction.show(fragment);
        }
        else{
            fragmentTransaction.add(containerId, fragment);
            fragmentTransaction.show(fragment);
            fragmentTransaction.hide(oldfragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

}
