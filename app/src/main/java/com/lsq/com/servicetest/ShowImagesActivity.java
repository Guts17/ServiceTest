package com.lsq.com.servicetest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import Adapter.ImageAdapter;
import Model.ImageBean;

public class ShowImagesActivity extends AppCompatActivity {

    private RecyclerView rv_images;
    private ListView lv_images;
    private ImageAdapter imageAdapter;
    private ArrayList<ImageBean> list_pics = new ArrayList<>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String[] arr_pics = msg.getData().getString("result").split("#");
                    for(String str:arr_pics){
                        list_pics.add(new ImageBean(str,"http://10.13.3.169:8080/Test/" + str,"",""));
//                        list_pics.add("http://192.168.1.102:8280/Test/" + str);
                    }
                    imageAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimages);

        lv_images = (ListView) findViewById(R.id.lv_images);
        //rv_images = (RecyclerView) findViewById(R.id.rv_images);
        imageAdapter = new ImageAdapter(ShowImagesActivity.this,list_pics);
        //rv_images.setAdapter(imageAdapter);
        lv_images.setAdapter(imageAdapter);

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
                    Toast.makeText(ShowImagesActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("Test",e.getMessage());
            e.printStackTrace();
        }
    }
}
