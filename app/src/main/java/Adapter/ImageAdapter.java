package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsq.com.servicetest.R;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import myinterface.ImageLoaderListener;
import model.ImageBean;
import utils.ImageLoader;


public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ImageBean> imagesList;
    private ImageLoader imageLoader;
    private ImageLoaderListener listener = new ImageLoaderListener() {
        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess(ImageView imageView,Bitmap bitmap,String url) {
            if(url.equals(imageView.getTag()) && imageView.getTag() != null){
                imageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onFailed() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onCanceled() {

        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
//                    byte[] b = msg.getData().getByteArray("byte_img");
//                    if(b == null){
//                       Log.d("Test","字节数组为空");
//                        return;
//                    }
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(b,0,b.length);
                    Bitmap bitmap = (Bitmap) msg.obj;
                    ViewHolder viewHolder = (ViewHolder) msg.getData().getSerializable("viewHolder");
                    Log.d("handlerPicTest","url:" + msg.getData().getString("url") + "viewHolder:" + viewHolder.iv_image.getTag().toString());
//                    if(msg.getData().getString("url").contains(viewHolder.iv_image.getTag().toString())){
                    if(viewHolder.iv_image.getTag() != null && msg.getData().getString("url").equals(viewHolder.iv_image.getTag())){
                        viewHolder.iv_image.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    };

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
        ViewHolder viewHolder;
        if(view == null){
            view = View.inflate(mContext,R.layout.item_image,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.iv_image.setTag(imagesList.get(i).getImgUrl());
        viewHolder.iv_image .setImageResource(R.drawable.smzx);
        viewHolder.tv_imgname.setText(imagesList.get(i).getImgName());
        imageLoader = new ImageLoader(listener);
        imageLoader.execute(viewHolder.iv_image);
        //requestPic(imagesList.get(i).getImgUrl(),viewHolder);
        return view;
    }

    private class ViewHolder implements Serializable {
        public ImageView iv_image;
        public TextView tv_imgname;
        public ViewHolder(View view){
            iv_image = (ImageView) view.findViewById(R.id.iv_image);
            tv_imgname = (TextView) view.findViewById(R.id.tv_imagename);
        }
    }

    public void requestPic(final String url, final ViewHolder viewHolder){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url_img = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) url_img.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream is = connection.getInputStream();
//                    byte[] b = new byte[is.available()];
//                    is.read(b);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = bitmap;

                    Bundle bundle = new Bundle();
                    bundle.putString("url",url);
                    bundle.putSerializable("viewHolder",viewHolder);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    Log.d("requestPicTest","url:" + url + "viewHolder:" + viewHolder.iv_image.getTag().toString());
                    is.close();
                } catch (Exception e) {
                    Log.d("Test",e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
