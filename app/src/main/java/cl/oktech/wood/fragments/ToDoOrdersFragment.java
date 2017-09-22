package cl.oktech.wood.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cl.oktech.wood.ApiClient;
import cl.oktech.wood.R;
import cl.oktech.wood.adapters.OrderAdapter;
import cl.oktech.wood.models.Order;
import cl.oktech.wood.models.Provider;
import cl.oktech.wood.services.OrderService;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ToDoOrdersFragment extends Fragment {

    public static final String TAG = ToDoOrdersFragment.class.getSimpleName();


    private List<Order> mOrders = new ArrayList<Order>();
    private OrderAdapter mAdapter;
    private Provider mProvider;
    private RecyclerView.LayoutManager mLayoutManager;
    private Realm mRealm;

    @BindView(R.id.ordersRecyclerView) RecyclerView mOrdersRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;

    public ToDoOrdersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_orders, container, false);
        ButterKnife.bind(this, view);
        // Get realm default instance
        mRealm = Realm.getDefaultInstance();
        // Get provider from realm
        mProvider = getProvider();
        // Set adapter and layout manager to recyclerview (with empty orders)
        setLayoutAndAdapter();
        // Get orders from realm or api
        getOrders();
        // Set swipe on refresh behaviour
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrdersFromApi();
            }
        });

        return view;
    }

    private void setLayoutAndAdapter() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new OrderAdapter(mOrders, getActivity());
        mOrdersRecyclerView.setLayoutManager(mLayoutManager);
        mOrdersRecyclerView.setAdapter(mAdapter);
    }

    private Provider getProvider(){
        return mRealm.where(Provider.class).equalTo("mId", 1).findFirst();
    }

    private void getOrders() {
        mOrders = getOrdersFromRealm();
        if(mOrders.isEmpty()){
            Log.i(TAG, "Getting orders from api.");
            getOrdersFromApi();
        } else {
            Log.i(TAG, "Setting orders from realm.");
            mAdapter.setOrders(mOrders);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getOrdersFromApi(){
        OrderService orderService = ApiClient.getClient().create(OrderService.class);

        Call<List<Order>> call = orderService.getOrders(mProvider.getApiToken());
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call <List<Order>> call, Response<List<Order>> response) {
                Log.i(TAG, "Setting orders from api.");
                mOrders = response.body();
                copyToRealm();
                mAdapter.setOrders(mOrders);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call <List<Order>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private List<Order> getOrdersFromRealm() {
        return mRealm.where(Order.class).equalTo("mClosed", false).findAll();
    }

    private void copyToRealm() {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(mOrders);
        mRealm.commitTransaction();
    }

}
