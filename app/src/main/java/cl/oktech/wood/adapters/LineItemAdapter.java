package cl.oktech.wood.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cl.oktech.wood.R;
import cl.oktech.wood.models.LineItem;

/**
 * Created by cl on 16-07-17.
 */

public class LineItemAdapter extends RecyclerView.Adapter<LineItemAdapter.ViewHolder> {

    private List<LineItem> mLineItems;
    private Context mContext;

    public LineItemAdapter(List<LineItem> lineItems, Context context) {
        mLineItems = lineItems;
        mContext = context;
    }

    @Override
    public LineItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_line_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LineItemAdapter.ViewHolder holder, int position) {
        LineItem lineItem = mLineItems.get(position);
        holder.mProduct.setText(lineItem.getName());
        holder.mProvision.setText(lineItem.getProvision());

    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mProduct;
        private TextView mProvision;

        public ViewHolder(View itemView) {
            super(itemView);

            mProduct = itemView.findViewById(R.id.productTextView);
            mProvision = itemView.findViewById(R.id.provisionTextView);
        }
    }
}
