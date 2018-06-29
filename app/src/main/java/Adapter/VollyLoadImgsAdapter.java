package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.lsq.com.servicetest.R;

import java.util.List;

import model.ImageBean;

/**
 * Created by Administrator on 2018-06-29.
 */

public class VollyLoadImgsAdapter extends RecyclerView.Adapter<VollyLoadImgsAdapter.ViewHolder> {

    private List<ImageBean> list;
    private Context mContext;
    private RequestQueue mRequestQueue;

    public VollyLoadImgsAdapter(List<ImageBean> list, Context mContext,RequestQueue requestQueue) {
        this.list = list;
        this.mContext = mContext;
        this.mRequestQueue = requestQueue;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            textView = (TextView) itemView.findViewById(R.id.tv_imagename);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ImageBean imageBean = list.get(position);

        ImageRequest request = new ImageRequest(imageBean.getImgUrl(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                holder.imageView.setImageBitmap(bitmap);
            }
        }, 100, 100, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                holder.imageView.setImageResource(R.drawable.smzx);
                Log.d("VolleyTest","图片" + imageBean.getImgName() + "加载失败:" +volleyError.getMessage() );
            }
        });
        request.setTag("Test");
        mRequestQueue.add(request);
        holder.textView.setText(imageBean.getImgName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
