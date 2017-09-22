package cl.oktech.wood.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cl.oktech.wood.BuildConfig;
import cl.oktech.wood.R;
import cl.oktech.wood.adapters.PhotoAdapter;
import cl.oktech.wood.models.Order;
import cl.oktech.wood.models.Photo;
import io.realm.Realm;

public class CloseOrderActivity extends AppCompatActivity {

    @BindView(R.id.stateSipnner) Spinner mStateSpinner;
    @BindView(R.id.commentsEditText) EditText mCommentsEditText;
    @BindView(R.id.photoRecyclerView) RecyclerView mPhotoRecyclerView;
    @BindView(R.id.closeOrderToolbar) Toolbar mToolbar;
    @BindView(R.id.closeOrderButton) Button mCloseOrderButton;

    private PhotoAdapter mPhotoAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Photo> mPhotos = new ArrayList<Photo>();
    private Order mOrder;
    private Realm mRealm;

    private final int PICTURE_FROM_CAMERA_CODE = 100;
    private String mCurrentPhotoPath;
    public static final String TAG = CloseOrderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_order);
        ButterKnife.bind(this);

        // Get realm default instance
        mRealm = Realm.getDefaultInstance();

        // Get Order
        mOrder = getOrder();

        // Set and configure toolbar
        setAndConfigureToolbar();

        mCloseOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrder();
                goToDashboard();
            }
        });

        // Set recyclerview and spinner adapter
        setAdapters();
    }

    private void setAndConfigureToolbar() {
        mToolbar.setTitle("Cierre OT Nº " + mOrder.getOrderNumber());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void goToDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void updateOrder() {
        mRealm.beginTransaction();
        mOrder.setClosed(true);
        mOrder.setComments(mCommentsEditText.getText().toString());
        mOrder.setState(mStateSpinner.getSelectedItem().toString());
        mOrder.getPhotos().addAll(mPhotos);
        mRealm.commitTransaction();
    }

    private void setAdapters() {
        String[] items = new String[]{"Sin realizar", "Avance Parcial", "Realizado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mStateSpinner.setAdapter(adapter);

        mLayoutManager = new GridLayoutManager(CloseOrderActivity.this, 4);
        mPhotoAdapter  = new PhotoAdapter(mPhotos, CloseOrderActivity.this, new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Photo photo, int position) {
                DeletePhotoDialog(photo, position);
            }
        });
        mPhotoRecyclerView.setLayoutManager(mLayoutManager);
        mPhotoRecyclerView.setAdapter(mPhotoAdapter);
    }

    private void DeletePhotoDialog(Photo photo,  final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Borrar Foto");
        builder.setMessage("¿Esta seguro que desea borrar esta foto?");
        builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPhotos.remove(position);
                mPhotoAdapter.notifyItemRemoved(position);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_close_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.takePhoto:
                takePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void takePhoto() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) { }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(CloseOrderActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intentCamera, PICTURE_FROM_CAMERA_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICTURE_FROM_CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    mPhotos.add(new Photo(mCurrentPhotoPath));
                    mPhotoAdapter.notifyItemInserted(mPhotos.size() - 1);
                } else {
                    Toast.makeText(this, "There was an error with the picture, try again.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Order getOrder() {
        Intent intent = getIntent();
        int orderNumber = intent.getIntExtra("orderNumber", 0);
        return mRealm.where(Order.class).equalTo("mOrderNumber", orderNumber).findFirst();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



}
