package cl.oktech.wood.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cl.oktech.wood.R;
import cl.oktech.wood.activities.OrderDetailsActivity;
import cl.oktech.wood.models.Order;

/**
 * Created by cl on 06-07-17.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> mOrders;
    private Context mContext;

    public OrderAdapter(List<Order> orders, Context context){
        mOrders  = orders;
        mContext = context;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, final int position) {
        final Order order = mOrders.get(position);
        holder.mOrderNumber.setText("" + order.getOrderNumber());
        holder.mBlock.setText(order.getBlock());
        holder.mAddress.setText(order.getAddress());

        if(!order.isClosed()){
            holder.mOrderState.setImageResource(R.drawable.ic_assignment_black);
        } else if (order.isClosed() && !order.isSynchronized()){
            holder.mOrderState.setImageResource(R.drawable.ic_assignment_turned_in_black);
        } else if (order.isClosed() && order.isSynchronized()) {
            holder.mOrderState.setImageResource(R.drawable.ic_cloud_done_black);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!order.isClosed()) {
                    Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                    intent.putExtra("orderNumber", order.getOrderNumber());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    public void setOrders(List<Order> orders) {
        mOrders = orders;
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mOrderNumber;
        private TextView mBlock;
        private TextView mAddress;
        private ImageView mOrderState;

        public ViewHolder(View itemView) {
            super(itemView);

            mOrderNumber = itemView.findViewById(R.id.orderNumberTextView);
            mBlock       = itemView.findViewById(R.id.blockTextView);
            mAddress     = itemView.findViewById(R.id.addressTextView);
            mOrderState  = itemView.findViewById(R.id.orderStateimageView);
        }
    }
}
