package sirius.seoulapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapsFragment mapsFragment;
    private ParsingdataFragment parsingdataFragment;
    private final String TAG = "MainActivity";
    private LoadingPlacesFragment loadingPlacesFragment;
    public static final String mBroadcastStringAction = "sirius.seoulapp.broadcast.isdetected";

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragments();
        InitViews();
    }

    private void setFragments(){
        fragmentManager = getSupportFragmentManager();
        loadingPlacesFragment = new LoadingPlacesFragment();
        mapsFragment = new MapsFragment();
        parsingdataFragment = new ParsingdataFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("mapsFragment", mapsFragment);
        loadingPlacesFragment.setArguments(bundle);
    }

    private void InitViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        replaceFragment(R.id.fragment_container, loadingPlacesFragment);

//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
//        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
//        tabs.setupWithViewPager(viewPager);


        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
//            VectorDrawableCompat indicator
//                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
//            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Log.d(TAG, "onNavigationItemSelected");
                        if (menuItem.getItemId() == R.id.mapmarker) {
                            Log.d(TAG, "mapmarker");
                            replaceFragment(R.id.fragment_container, mapsFragment);
                        } else {
                            Log.d(TAG, "parsingdataFragment");
                            replaceFragment(R.id.fragment_container, parsingdataFragment);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void replaceFragment(int containerId, Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commit();
    }

//    // Add Fragments to Tabs
//    private void setupViewPager(ViewPager viewPager) {
//        Adapter adapter = new Adapter(getSupportFragmentManager());
//        adapter.addFragment(new MapsFragment(), "지도보기");
//        adapter.addFragment(new ParsingdataFragment(), "찾기");
////        adapter.addFragment(new CardContentFragment(), "Card");
//        viewPager.setAdapter(adapter);
//    }
//
//    static class Adapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public Adapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }

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
