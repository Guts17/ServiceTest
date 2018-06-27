package Interface;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018-06-27.
 */

public interface ImageLoaderListener {
    void onProgress(int progress);
    void onSuccess(Bitmap bitmap);
    void onFailed();
    void onPaused();
    void onCanceled();
}
