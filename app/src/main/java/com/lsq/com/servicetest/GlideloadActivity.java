package com.lsq.com.servicetest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import Adapter.GlideImageAdapter;
import Model.ImageBean;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GlideloadActivity extends AppCompatActivity {

    @BindView(R.id.rv_glideimgs) RecyclerView rv_glideimgs;
    private ArrayList<ImageBean> imgList = new ArrayList<>();
    private GlideImageAdapter glideImageAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String[] arr = msg.getData().getString("result").split("#");
                    for(String str : arr){
                        ImageBean imageBean = new ImageBean(str,"http://192.168.1.102:8280/Test/" + str,"","");
                        imgList.add(imageBean);
                    }
                    glideImageAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glideload);
        ButterKnife.bind(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_glideimgs.setLayoutManager(manager);
        glideImageAdapter = new GlideImageAdapter(this,imgList);
        rv_glideimgs.setAdapter(glideImageAdapter);
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
                    Toast.makeText(GlideloadActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("Test",e.getMessage());
            e.printStackTrace();
        }
    }

}
