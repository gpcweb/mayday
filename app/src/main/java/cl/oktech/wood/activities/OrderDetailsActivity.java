package cl.oktech.wood.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cl.oktech.wood.R;
import cl.oktech.wood.adapters.LineItemAdapter;
import cl.oktech.wood.models.LineItem;
import cl.oktech.wood.models.Order;
import io.realm.Realm;

public class OrderDetailsActivity extends AppCompatActivity {

    @BindView(R.id.clientNameTextView) TextView mNameTextView;
    @BindView(R.id.clientRutTextView) TextView mRutTextView;
    @BindView(R.id.addressTextView) TextView mAddressTextView;
    @BindView(R.id.blockTextView) TextView mBlockTextView;
    @BindView(R.id.lineItemsRecyclerView) RecyclerView mLineItemsRecyclerView;
    @BindView(R.id.finishButton) Button mFinishButton;
    @BindView(R.id.orderDetailsToolbar) Toolbar mToolbar;

    private List<LineItem> mLineItems = new ArrayList<LineItem>();
    private LineItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Order mOrder;

    private final int PHONE_CALL_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        // Get order
        mOrder = getOrder();

        // Set and configure toolbar
        setAndConfigureToolbar();

        // Set order details and line items
        setOrderDetails();
        setOrderLineItems();

        // Set onClickListener to button
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCloseOrderActivity();
            }
        });
    }

    private void goToCloseOrderActivity() {
        Intent intent = new Intent(OrderDetailsActivity.this, CloseOrderActivity.class);
        intent.putExtra("orderNumber", mOrder.getOrderNumber());
        startActivity(intent);
    }

    private void setOrderDetails() {
        mNameTextView.setText(mOrder.getName());
        mRutTextView.setText(mOrder.getRut());
        mAddressTextView.setText(mOrder.getAddress());
        mBlockTextView.setText(mOrder.getBlock());
    }

    private void setOrderLineItems() {
        mLineItems = mOrder.getLineItems();
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new LineItemAdapter(mLineItems, this);
        mLineItemsRecyclerView.setLayoutManager(mLayoutManager);
        mLineItemsRecyclerView.setAdapter(mAdapter);
    }

    private void setAndConfigureToolbar() {
        mToolbar.setTitle("Detalle OT Nº " + mOrder.getOrderNumber());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private Order getOrder() {
        Intent intent = getIntent();
        int orderNumber = intent.getIntExtra("orderNumber", 0);
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Order.class).equalTo("mOrderNumber", orderNumber).findFirst();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_details_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.callClient:
                checkPermissionAndCallClient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissionAndCallClient() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermission(Manifest.permission.CALL_PHONE)) {
                callClient();
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_CODE);
                } else {
                    Toast.makeText(OrderDetailsActivity.this, R.string.grant_access, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            OlderVersions();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PHONE_CALL_CODE:

                String permission = permissions[0];
                int result        = grantResults[0];

                if (permission.equals(Manifest.permission.CALL_PHONE)) {
                    // If request is cancelled, the result arrays are empty.
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        callClient();
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

    private void OlderVersions() {
        if (CheckPermission(Manifest.permission.CALL_PHONE)) {
            callClient();
        } else {
            Toast.makeText(OrderDetailsActivity.this, R.string.declined_access, Toast.LENGTH_SHORT).show();
        }
    }

    private void callClient() {
        String phoneNumber = mOrder.getPhone();
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { return; }
        startActivity(intentCall);
    }

    private boolean CheckPermission(String permission) {
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

}
