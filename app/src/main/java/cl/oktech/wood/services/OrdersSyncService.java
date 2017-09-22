package cl.oktech.wood.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cl.oktech.wood.ApiClient;
import cl.oktech.wood.models.Order;
import cl.oktech.wood.models.Photo;
import cl.oktech.wood.models.Provider;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gabrielpoblete on 31-07-17.
 */

public class OrdersSyncService extends IntentService {

    private static final String TAG = OrdersSyncService.class.getSimpleName();
    private Realm mRealm;
    private Order mOrder = null;
    private Provider mProvider = null;

    public OrdersSyncService() {
        super(OrdersSyncService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mRealm = Realm.getDefaultInstance();
        // Get first order not synchronized
        mOrder = mRealm.where(Order.class).equalTo("mClosed", true)
                                          .equalTo("mSynchronized", false).findFirst();
        // Get provider
        mProvider = mRealm.where(Provider.class).equalTo("mId", 1).findFirst();

        if (mOrder != null){
            Log.d(TAG, "Orders syncing started.");
            try {
                if (uploadPhoto()){
                    Log.d(TAG, "Photo uploaded successfully.");
                    mRealm.beginTransaction();
                    mOrder.setSynchronized(true);
                    mRealm.commitTransaction();
                    Log.d(TAG, "Photo synchronized successfully.");
                } else {
                    Log.d(TAG, "Unable to upload photo, please try again.");
                }
            } catch (IOException e) {
                Log.d(TAG, "Error uploading photo.");
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Nothing to sync.");
        }
    }

    private boolean uploadPhoto() throws IOException {

        PhotoService photoService = ApiClient.getClient().create(PhotoService.class);

        Photo photo    = mOrder.getPhotos().get(0);
        File photoFile = new File(photo.getPath());

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("attachment", photoFile.getName(), requestFile);

        Call<ResponseBody> call = photoService.upload(mOrder.getOrderNumber(), mProvider.getApiToken(), body);
        Response response       = call.execute();

        return response.isSuccessful();
    }
}
