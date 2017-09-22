package cl.oktech.wood.app;

import android.app.Application;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by cl on 11-07-17.
 */

public class MyApplication extends Application {

    public static AtomicInteger ProviderID = new AtomicInteger();
    //public static AtomicInteger OrderID = new AtomicInteger();
    public static AtomicInteger PhotoID = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize realm
        Realm.init(getApplicationContext());

        // Set default configuration
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        // Get order and provider id
        Realm realm = Realm.getInstance(config);
        PhotoID     = getId(realm, cl.oktech.wood.models.Photo.class);
        //ProviderID  = getId(realm, cl.oktech.wood.models.Provider.class);
        //OrderID     = getId(realm, cl.oktech.wood.models.Order.class);

        realm.close();

    }

    private <T extends RealmObject> AtomicInteger getId(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("mId").intValue()) : new AtomicInteger();
    }
}
