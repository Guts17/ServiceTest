package com.lsq.com.servicetest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import adapter.VollyLoadImgsAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.ImageBean;

public class VolleyTestActivity extends AppCompatActivity {

    @BindView(R.id.rv_volleyimgs) RecyclerView rv_volleyimgs;
    private VollyLoadImgsAdapter adapter;
    private ArrayList<ImageBean> mList = new ArrayList<>();
    private RequestQueue requestQueue;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String[] arr = msg.getData().getString("result").split("#");
                    for(String str : arr){
                        ImageBean imageBean = new ImageBean(str,"http://10.13.3.169:8080/Test/" + str,"","");
//                        ImageBean imageBean = new ImageBean(str,"http://192.168.1.102:8280/Test/" + str,"","");
                        mList.add(imageBean);
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volleytest);

        ButterKnife.bind(this);
        GridLayoutManager manager = new GridLayoutManager(this,2);
        rv_volleyimgs.setLayoutManager(manager);
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        adapter = new VollyLoadImgsAdapter(mList,this,requestQueue);
        rv_volleyimgs.setAdapter(adapter);
        requestQueue.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getFilesList();
            }
        }).start();
    }

    public void getFilesList() {
        String nameSpace = "http://ws.apache.org/axis2";
        String methodName = "getFileLists";
        String endPoint = getString(R.string.tomcat_server) + "axis2/services/GetFileLists";
        String soapAction = "http://ws.apache.org/axis2/getFileLists";
        SoapObject request = new SoapObject(nameSpace, methodName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = request;
        envelope.dotNet = true;
        HttpTransportSE transport = new HttpTransportSE(endPoint);
        transport.debug = true;
        try {
            transport.call(soapAction, envelope);
            Object object = envelope.getResponse();
            SoapObject response = (SoapObject) envelope.bodyIn;
            Message msg = Message.obtain();
            msg.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("result",response.getProperty(0).toString());
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VolleyTestActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("Test",e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestQueue.cancelAll("Test");
    }
}
