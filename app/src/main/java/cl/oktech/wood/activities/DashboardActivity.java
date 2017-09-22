package cl.oktech.wood.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cl.oktech.wood.R;
import cl.oktech.wood.adapters.PagerAdapter;
import cl.oktech.wood.models.Provider;
import cl.oktech.wood.services.OrdersSyncService;
import io.realm.Realm;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.emailTextView) TextView mEmailTextView;
    @BindView(R.id.nameTextView) TextView mNameTextView;
    @BindView(R.id.dateTextView) TextView mDateTextView;
    @BindView(R.id.providerImageView) ImageView mProviderImageView;

    @BindView(R.id.dashboardToolbar) Toolbar mToolbar;
    @BindView(R.id.dashboardTabLayout) TabLayout mTabLayout;
    @BindView(R.id.dashboardViewPager) ViewPager mViewPager;

    private SharedPreferences mSharedPreferences;
    private Realm mRealm;

    public static final String TAG = DashboardActivity.class.getSimpleName();
    public static final int REQUEST_LOCATION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        // Set and configure toolbar
        setAndConfigureToolbar();

        mRealm = Realm.getDefaultInstance();

        // Get shared preferences
        mSharedPreferences = getSharedPreferences("MaydaySharedPreferences", Context.MODE_PRIVATE);

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.open_wo));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.closed_wo));

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Get provider from realm
        Provider provider = getProvider();

        // Set provider info and photo
        setProviderInfo(provider);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent ordersSyncServiceIntent = new Intent(DashboardActivity.this, OrdersSyncService.class);
        startService(ordersSyncServiceIntent);
    }

    private void setAndConfigureToolbar() {
        mToolbar.setTitle(R.string.dashboard_title);
        setSupportActionBar(mToolbar);
    }

    private Provider getProvider(){
        return mRealm.where(Provider.class).equalTo("mId", 1).findFirst();
    }

    private void setProviderInfo(Provider provider) {
        String today = getDate();
        mNameTextView.setText(provider.getName());
        mEmailTextView.setText(provider.getEmail());
        mDateTextView.setText(today);
        Picasso.with(DashboardActivity.this).load(provider.getPhoto()).transform(new CropCircleTransformation()).into(mProviderImageView);
    }

    private String getDate() {
        Date todaysDate = new Date();
        DateFormat df   = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(todaysDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showMap:
                checkPermissionsAndGoToMapsActivity();
                return true;
            case R.id.logOut:
                logOut();
                return true;
            case R.id.logOutAndForget:
                removeSharedPreferences();
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeSharedPreferences() {
        mSharedPreferences.edit().clear().apply();
    }

    private void logOut() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void checkPermissionsAndGoToMapsActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(CheckPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                goToOrdersMap();
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
                } else {
                    Toast.makeText(DashboardActivity.this, R.string.grant_access, Toast.LENGTH_SHORT).show();
                }
            }
        } else  {
            OlderVersions();
        }
    }

    private void goToOrdersMap(){
        Intent intent = new Intent(this, OrdersMapActivity.class);
        startActivity(intent);
    }

    private void OlderVersions() {
        if (CheckPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            goToOrdersMap();
        } else {
            Toast.makeText(DashboardActivity.this, R.string.declined_access, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:

                String permission = permissions[0];
                int result        = grantResults[0];

                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If request is cancelled, the result arrays are empty.
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        goToOrdersMap();
                    } else {
                        Toast.makeText(this, R.string.declined_access, Toast.LENGTH_LONG).show();
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
    }

    private boolean CheckPermission(String permission) {
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
