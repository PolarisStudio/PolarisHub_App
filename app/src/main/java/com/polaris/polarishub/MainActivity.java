package com.polaris.polarishub;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.yanzhenjie.andserver.sample.ServerManager;

import com.polaris.polarishub.R;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ServerManager mServerManager;

    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnBrowser;
    private Button scanButt;
    private TextView mTvMessage;

    private Dialog mDialog;
    private String mRootUrl;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mBtnStart = findViewById(R.id.btn_start);
        mBtnStop = findViewById(R.id.btn_stop);
        mBtnBrowser = findViewById(R.id.btn_browse);
        scanButt = findViewById(R.id.scan_butt);
        mTvMessage = findViewById(R.id.tv_message);

        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnBrowser.setOnClickListener(this);
        scanButt.setOnClickListener(this);

        // AndServer run in the service.
        mServerManager = new ServerManager(this);  // 实例化 ServerManager 为 mServerManager
        mServerManager.register();                 // 调用ServerManager内部方法 register ，

        // startServer;
        mBtnStart.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServerManager.unRegister();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start: {
                System.out.println("Click Start");
                //showDialog();                     //点击Start，调用 ServerManager 的 startServer()
                mServerManager.startServer();     //综合所有代码 实际上就是 "打开CoreService"
                break;
            }
            case R.id.btn_stop: {
                //showDialog();
                mServerManager.stopServer();      //点击Stop，调用 ServerManager 的 stopServer()
                break;
            }
            case R.id.btn_browse: {                         //点击browse，用 VIEW intent 显示 【从 mRootUrl】= 本机8080接口 用 Uri.pars() 解析出的数据
                if (!TextUtils.isEmpty(mRootUrl)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW"); //
                    intent.setData(Uri.parse(mRootUrl));
                    startActivity(intent);
                }
                break;
            }
            case R.id.scan_butt: {       //扫码下载

                //Toast.makeText(this, "click", Toast.LENGTH_LONG).show();

                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                // 开始扫描
                intentIntegrator.initiateScan();
                break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Start notify.
     */
    public void onServerStart(String ip) {                          // onServerStart 传入ip
        closeDialog();                                              // 用ip生成Url：mRootUrl、login.html
        mBtnStart.setVisibility(View.GONE);
        mBtnStop.setVisibility(View.VISIBLE);
        mBtnBrowser.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(ip)) {
            List<String> addressList = new LinkedList<>();//用 String list 储存几个 url
            mRootUrl = "http://" + ip + ":8080/";
            addressList.add(mRootUrl);
            addressList.add("http://" + ip + ":8080/login.html");
            mTvMessage.setText(TextUtils.join("\n", addressList));//用textView打印所生成的url
        } else {
            mRootUrl = null;
            mTvMessage.setText("server_ip_error");
        }
    }

    /**
     * Error notify.
     */
    public void onServerError(String message) {
        closeDialog();
        mRootUrl = null;
        mBtnStart.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.GONE);
        mBtnBrowser.setVisibility(View.GONE);
        mTvMessage.setText(message);
    }

    /**
     * Stop notify.
     */
    public void onServerStop() {
        closeDialog();
        mRootUrl = null;
        mBtnStart.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.GONE);
        mBtnBrowser.setVisibility(View.GONE);
        mTvMessage.setText("server_stop_succeed");
    }

    private void showDialog() {
        if (mDialog == null) mDialog = new Dialog(this);
        if (!mDialog.isShowing()) mDialog.show();
    }

    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }
}