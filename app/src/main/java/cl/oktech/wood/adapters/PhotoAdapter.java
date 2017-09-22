package cl.oktech.wood.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cl.oktech.wood.R;
import cl.oktech.wood.models.Photo;

/**
 * Created by cl on 18-07-17.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<Photo> mPhotos;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public PhotoAdapter(List<Photo> photos, Context context, OnItemClickListener listener) {
        mPhotos = photos;
        mContext = context;
        mOnItemClickListener = listener;
    }

    public void setPhotos(List<Photo> photos) {
        mPhotos = photos;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_photo_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, final int position) {
        final Photo photo        = mPhotos.get(position);
        Bitmap bitmapPhoto = BitmapFactory.decodeFile(photo.getPath());
        holder.mPhotoImageView.setImageBitmap(bitmapPhoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(photo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mPhotoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Photo photo, int position);
    }
}
