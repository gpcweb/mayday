package cl.oktech.wood.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by cl on 16-07-17.
 */

public class LineItem extends RealmObject {

    @SerializedName("name")
    private String mName;
    @SerializedName("provision")
    private String mProvision;
    @SerializedName("id")
    private int mId;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getProvision() {
        return mProvision;
    }

    public void setProvision(String provision) {
        mProvision = provision;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
