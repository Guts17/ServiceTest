package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Interface.ImageLoaderListener;

/**
 * Created by Administrator on 2018-06-27.
 */

public class ImageLoader extends AsyncTask<ImageView,Integer,Bitmap>{

    private ImageLoaderListener listener;
    private ImageView imageView;
    private String url;

    public ImageLoader(ImageLoaderListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(ImageView... params) {
        this.imageView = params[0];
        this.url = params[0].getTag().toString();
        Bitmap bitmap = null;
        try {
            URL url = new URL(params[0].getTag().toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        listener.onSuccess(imageView,bitmap,url);
    }
}
