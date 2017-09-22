package cl.oktech.wood.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cl on 05-07-17.
 */

public class Order extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private int mOrderNumber;

    @SerializedName("block")
    private String mBlock;

    @SerializedName("county")
    private String mCounty;

    @SerializedName("address")
    private String mAddress;

    @SerializedName("name")
    private String mName;

    @SerializedName("rut")
    private String mRut;

    @SerializedName("phone")
    private String mPhone;

    @SerializedName("reserved_on")
    private String mReservedOn;

    @SerializedName("observations")
    private String mObservations;

    @SerializedName("latitude")
    private double mLatitude;

    @SerializedName("longitude")
    private double mLongitude;

    @SerializedName("line_items")
    private RealmList<LineItem> mLineItems;

    private RealmList<Photo> mPhotos;

    private boolean mClosed;

    private boolean mSynchronized;

    private String mComments;

    private String mState;

    public Order(int orderNumber, String block, String county, String address, String name, String rut, String phone, String reservedOn, String observations, double latitude, double longitude, boolean closed) {
        mOrderNumber = orderNumber;
        mBlock = block;
        mCounty = county;
        mAddress = address;
        mName = name;
        mRut = rut;
        mPhone = phone;
        mReservedOn = reservedOn;
        mObservations = observations;
        mLatitude = latitude;
        mLongitude = longitude;
        mLineItems = new RealmList<LineItem>();
        mClosed = closed;
        mSynchronized = false;
    }

    // For realm
    public Order(){}

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public int getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        mOrderNumber = orderNumber;
    }

    public String getBlock() {
        return mBlock;
    }

    public void setBlock(String block) {
        mBlock = block;
    }

    public String getCounty() {
        return mCounty;
    }

    public void setCounty(String county) {
        mCounty = county;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getRut() {
        return mRut;
    }

    public void setRut(String rut) {
        mRut = rut;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getReservedOn() {
        return mReservedOn;
    }

    public void setReservedOn(String reservedOn) {
        mReservedOn = reservedOn;
    }

    public String getObservations() {
        return mObservations;
    }

    public void setObservations(String observations) {
        mObservations = observations;
    }

    public RealmList<LineItem> getLineItems() {
        return mLineItems;
    }

    public RealmList<Photo> getPhotos() {
        return mPhotos;
    }

    public boolean isClosed() {
        return mClosed;
    }

    public void setClosed(boolean closed) {
        mClosed = closed;
    }

    public boolean isSynchronized() {
        return mSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        mSynchronized = aSynchronized;
    }

    public String getComments() {
        return mComments;
    }

    public void setComments(String comments) {
        mComments = comments;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }
}
