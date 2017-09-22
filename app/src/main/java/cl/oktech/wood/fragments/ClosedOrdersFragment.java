package cl.oktech.wood.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cl.oktech.wood.R;
import cl.oktech.wood.adapters.OrderAdapter;
import cl.oktech.wood.models.Order;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClosedOrdersFragment extends Fragment {


    public static final String TAG = ClosedOrdersFragment.class.getSimpleName();

    private List<Order> mOrders = new ArrayList<Order>();
    private OrderAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Realm mRealm;

    @BindView(R.id.ordersClosedRecyclerView) RecyclerView mOrdersRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;

    public ClosedOrdersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_closed_orders, container, false);
        ButterKnife.bind(this, view);
        // Get realm default instance
        mRealm  = Realm.getDefaultInstance();
        // Get orders from realm
        getOrders();
        // Set adapter and layout manager to recyclerview
        setLayoutAndAdapter();
        // Set swipe on refresh behaviour
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders();
                mAdapter.setOrders(mOrders);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
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

    private void getOrders() {
        mOrders =  mRealm.where(Order.class).equalTo("mClosed", true).findAll();
    }

}
