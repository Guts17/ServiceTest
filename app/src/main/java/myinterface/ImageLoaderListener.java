package myinterface;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018-06-27.
 */

public interface ImageLoaderListener {
    void onProgress(int progress);
    void onSuccess(ImageView imageView,Bitmap bitmap,String url);
    void onFailed();
    void onPaused();
    void onCanceled();
}
