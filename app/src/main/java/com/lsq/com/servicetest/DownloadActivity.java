package com.lsq.com.servicetest;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import service.DownloadService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadActivity extends AppCompatActivity {

    private DownloadService.DownloadBinder downloadBinder;
    @BindView(R.id.bt_startdownload)
    Button bt_start;
    @BindView(R.id.bt_pausedownload)
    Button bt_pause;
    @BindView(R.id.bt_canceldownload)
    Button bt_cancel;

    @OnClick(R.id.bt_startdownload)
    public void startDownload(){
        if(downloadBinder != null){
            downloadBinder.startDownload(getString(R.string.tomcat_server) + "resource/eclipse-oxygen-64.zip");
        }else {
            Toast.makeText(this,"Binder为Null",Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.bt_pausedownload)
    public void pauseDownload(){
        if(downloadBinder != null){
            downloadBinder.pauseDownload();
        }
    }
    @OnClick(R.id.bt_canceldownload)
    public void cancelDownload(){
        if(downloadBinder != null){
            downloadBinder.cancelDownload();
        }
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        Intent intent = new Intent(this,DownloadService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(DownloadActivity.this, "权限被拒绝，无法使用", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
