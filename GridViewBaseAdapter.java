package com.example.camarasample;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GridViewBaseAdapter extends BaseAdapter {
    String mBasePath;
    Context mContext;
    Bitmap bm;
    String[] mlmgs;


    public String TAG = "Adapter";

    public GridViewBaseAdapter(Context context, String basePath) {

        this.mContext = context;
        this.mBasePath = basePath;

        File file = new File(mBasePath);
        if(!file.exists()) {
            if(!file.mkdir()) {
                Log.d(TAG, "문제 있지롱");
            }
        }
        mlmgs = file.list();


    }

    @Override
    public int getCount() {
        File dir = new File(mBasePath);
        mlmgs = dir.list();
        return mlmgs.length;
    }


    @Override
    public Object getItem(int position) {
        return position;
    }

    public String getItemPath(int position) {
        String path = mBasePath + File.separator + mlmgs[position];
        return path;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        ImageView imageViews;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageViews = new ImageView(mContext);


        } else {
            imageView = (ImageView) convertView;
            imageViews = (ImageView) convertView;

        }

        bm = BitmapFactory.decodeFile(mBasePath + File.separator + mlmgs[position]);
        //Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(getItemPath(position),MediaStore.Video.Thumbnails.MICRO_KIND);
        if(bm == BitmapFactory.decodeFile(mBasePath + File.separator + "VID_"+  ".mp4")) {
            Bitmap mThumbnail1 = ThumbnailUtils.createVideoThumbnail(getItemPath(position),MediaStore.Video.Thumbnails.MICRO_KIND);


            imageViews.setImageBitmap(mThumbnail1);

            if(bm !=null && !bm.isRecycled()) {
                bm.recycle();
            }

            return imageViews;
        }
        Bitmap mThumbnail = ThumbnailUtils.extractThumbnail(bm, 300, 300);
        imageView.setPadding(8,8,8,8);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        imageView.setImageBitmap(mThumbnail);

        if(bm !=null && !bm.isRecycled()) {
            bm.recycle();
        }
        return imageView;
    }


    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
