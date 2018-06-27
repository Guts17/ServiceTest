package com.lsq.com.servicetest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private TextView tv_hello;
    private Button bt_hello;
    private ImageView iv_img;
    private Button bt_upload;
    private Button bt_getlist;
    private Button bt_display;
    private ListView lv_pics;
    private String[] arr_pics;
    private ArrayList<String> list_pics = new ArrayList<>();
    private ArrayAdapter<String> adapter_pics;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("result");
            switch (msg.what){
                case 1:
                    tv_hello.setText(result);
                    break;
                case 2:
                    tv_hello.setText(result);
                    break;
                case 3:
                    //tv_hello.setText(result);
                    arr_pics = result.split("#");
                    for(String str:arr_pics){
                        list_pics.add("http://10.13.3.169:8080/Test/" + str);
                    }
                    adapter_pics.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_hello = (TextView) findViewById(R.id.tv_hello);
        bt_hello = (Button) findViewById(R.id.bt_hello);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        bt_upload = (Button) findViewById(R.id.bt_upload);
        bt_getlist= (Button) findViewById(R.id.bt_getlist);
        bt_display = (Button) findViewById(R.id.bt_display);
        lv_pics = (ListView) findViewById(R.id.lv_pics);
        adapter_pics = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_pics);
        lv_pics.setAdapter(adapter_pics);
        //iv_img.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.smzx));
        bt_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShowImagesActivity.class);
                startActivity(intent);
            }
        });
        bt_hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        execHelloService("lsq");
                    }
                }).start();
            }
        });
        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.smzx);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] b = baos.toByteArray();
                        uploadFile(Base64.encode(b),"smzx1.png");
                    }
                }).start();
            }
        });
        bt_getlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getFilesList();
                    }
                }).start();
            }
        });
        lv_pics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestPic(list_pics.get(position));
            }
        });
    }

    public void requestPic(final String url){
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
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_img.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void execHelloService(String userName) {
        String nameSpace = "http://ws.apache.org/axis2";
        String methodName = "sayHello";
        String endPoint = getString(R.string.tomcat_server) + "axis2/services/HelloWebService";
        String soapAction = "http://ws.apache.org/axis2/sayHello";
        SoapObject request = new SoapObject(nameSpace, methodName);
        request.addProperty("name", userName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = request;
        envelope.dotNet = true;
        HttpTransportSE transport = new HttpTransportSE(endPoint);
        transport.debug = true;
        try {
            transport.call(soapAction, envelope);
            Object object = envelope.getResponse();
            SoapObject response = (SoapObject) envelope.bodyIn;
//            String result1 = response.getProperty("sayHelloResponse").toString();
//            String result = response.getProperty("sayHelloResponse").toString();
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
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    public void uploadFile(String input,String filename) {
        String nameSpace = "http://ws.apache.org/axis2";
        String methodName = "uploadFile";
        String endPoint = getString(R.string.tomcat_server) + "axis2/services/UploadFileWebService";
        String soapAction = "http://ws.apache.org/axis2/uploadFile";
        SoapObject request = new SoapObject(nameSpace, methodName);
        request.addProperty("input", input);
        request.addProperty("filename",filename);
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
            msg.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("result",response.getProperty(0).toString());
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("Test",e.getMessage());
            e.printStackTrace();
        }
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
            msg.what = 3;
            Bundle bundle = new Bundle();
            bundle.putString("result",response.getProperty(0).toString());
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("Test",e.getMessage());
            e.printStackTrace();
        }
    }
}
