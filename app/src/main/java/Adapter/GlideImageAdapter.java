package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lsq.com.servicetest.R;

import java.util.List;

import model.ImageBean;

public class GlideImageAdapter extends RecyclerView.Adapter<GlideImageAdapter.ViewHolder> {

    private List<ImageBean> list;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            textView = (TextView) itemView.findViewById(R.id.tv_imagename);
        }
    }

    public GlideImageAdapter(Context context,List<ImageBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageBean imageBean = list.get(position);
        Glide.with(mContext).load(imageBean.getImgUrl()).into(holder.imageView);
//        holder.imageView.setImageBitmap(null);
        holder.textView.setText(imageBean.getImgName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
