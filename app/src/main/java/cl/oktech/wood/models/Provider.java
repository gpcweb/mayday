package cl.oktech.wood.models;

import com.google.gson.annotations.SerializedName;

import cl.oktech.wood.app.MyApplication;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cl on 04-07-17.
 */

public class Provider extends RealmObject {

    @SerializedName("name")
    private String mName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("photo_url")
    private String mPhoto;
    @SerializedName("api_token")
    private String mApiToken;
    private String mPassword;
    @PrimaryKey
    private int mId;
    private RealmList<Order> mOrders;

    public Provider(int id, String name, String email, String photo, String token) {
        mId    = MyApplication.ProviderID.incrementAndGet();
        mName  = name;
        mEmail = email;
        mPhoto = photo;
        mApiToken = token;
        mOrders = new RealmList<Order>();
    }

    // For api authentication
    public Provider(String email, String password){
        mEmail    = email;
        mPassword = password;
    }

    // For realm
    public Provider(){}

    public String getApiToken() {
        return mApiToken;
    }

    public void setApiToken(String apiToken) {
        mApiToken = apiToken;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public RealmList<Order> getOrders() {
        return mOrders;
    }
}
