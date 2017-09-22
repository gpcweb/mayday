package cl.oktech.wood.models;

import cl.oktech.wood.app.MyApplication;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cl on 21-07-17.
 */

public class Photo extends RealmObject {

    @PrimaryKey
    private int mId;

    private String mPath;

    public Photo(){

    }

    public Photo(String path) {
        mId   = MyApplication.PhotoID.incrementAndGet();
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }
}
