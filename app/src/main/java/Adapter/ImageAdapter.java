package Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Model.ImageBean;

public class ImageAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<ImageBean> imagesList;

    public ImageAdapter(Context mContext, ArrayList<ImageBean> imagesList) {
        this.mContext = mContext;
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object getItem(int i) {
        return imagesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        return null;
    }

    private class ViewHolder{
        public ImageView iv_img;
        public TextView tv_imgname;
    }
}
