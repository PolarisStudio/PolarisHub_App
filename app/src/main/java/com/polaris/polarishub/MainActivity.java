package com.polaris.polarishub;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.polaris.polarishub.Tools.IpManager;
import com.yanzhenjie.andserver.sample.ServerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ServerManager mServerManager;

    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnBrowser;
    public Button scanButt;
    public Button shareButt;
    private TextView mTvMessage;
    private ImageView qrForTest;

    public ProgressBar progress;
    public TextView downloadStatus;

    private Dialog mDialog;
    private String mRootUrl;

    static transient public File[] filelist ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }

        mBtnStart = findViewById(R.id.btn_start);
        mBtnStop = findViewById(R.id.btn_stop);
        mBtnBrowser = findViewById(R.id.btn_browse);
        scanButt = findViewById(R.id.scan_butt);
        shareButt = findViewById(R.id.share_butt);
        mTvMessage = findViewById(R.id.tv_message);
        qrForTest= findViewById(R.id.qr_for_test);

        mRootUrl = "http://" + IpManager.getIpAddress(this) + ":8080/files/Butler1.3.3.2.apk";
        System.out.println(mRootUrl);

        Bitmap qr = createQRcodeImage(mRootUrl,100,100);
        if(null!=qr){
            qrForTest.setImageBitmap(qr);
        }else{
            System.out.println("fail to get qrCodeImage");
            Toast.makeText(this,"fail to create qrcode",Toast.LENGTH_LONG).show();
            //qrForTest.setImageBitmap(qr);
        }


        Download("http://10.30.177.0:8080/files/Butler1.3.3.2.apk","Butler1.3.3.2.apk");

        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnBrowser.setOnClickListener(this);
        scanButt.setOnClickListener(this);
        shareButt.setOnClickListener(this);

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
        System.out.println(mRootUrl);
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
                    System.out.println(mRootUrl);
                    intent.setData(Uri.parse("http://" + IpManager.getIpAddress(this) + ":8080/files/Butler1.3.3.2.apk"));
                    startActivity(intent);
                }
                break;
            }
            case R.id.scan_butt: {       //扫码下载

                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                // 开始扫描
                intentIntegrator.initiateScan();
                break;
            }
            case R.id.share_butt: {
                //默认扫描polarishub文件夹
                File polarisHubFolder =new File(Environment.getExternalStorageDirectory(),"PolarisHub");
                while(!polarisHubFolder.exists()){
                    System.out.println("Download:"+polarisHubFolder.exists() );
                    polarisHubFolder.mkdir();
                }
                //filelist = scanFolder(polarisHubFolder.getPath());//扫描文件夹获取文件列表
                filelist = polarisHubFolder.listFiles();
                System.out.println("intent");
                Intent intent = new Intent("com.polaris.polarishub.ACTION_START");
                intent.addCategory("com.polaris.polarishub.FILELIST");
                startActivity(intent);
            }

        }
    }


    //扫描返回结果

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        System.out.println("result");
        if (resultCode == Activity.RESULT_OK) {
            System.out.println("result ok, code: "+resultCode);
            switch (resultCode){
                case -1:{
                    if (result != null) {
                        if (result.getContents() == null) {
                            Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(this, "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
                            if(CheckResult(result.getContents())==true){
                                String uri = result.getContents();
                                String filename = getFilenameFromUri(uri);
                                Download(uri,filename);
                            }
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                    break;
                }

            }

        }
    }




    //实现服务器功能

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
            mRootUrl = "http://" + IpManager.getIpAddress(this) + ":8080/files/Butler1.3.3.2.apk";
            addressList.add(mRootUrl);
            addressList.add("http:/" + ip + ":8080/login.html");
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

    public Boolean CheckResult (String result){
        return true; //冗余设计，尚未完善。
    }

    public String getFilenameFromUri(String uri){
        String filename = uri.substring(uri.lastIndexOf("/") + 1);
        return filename;
    }



    //打开文件列表界面

    public File[] scanFolder(String path){

        File file = new File(path);
        //判断是不是文件夹
        if(!file.isDirectory()){
            System.out.println(file.getName());
            return null;
        }else{
            //是文件夹，便遍历出里面所有的文件（文件，文件夹）
            File[] files = file.listFiles();
            for(int i=0;i<files.length;i++){
                //继续判断是文件夹还是文件
                if(!files[i].isDirectory()){
                    System.out.println(files[i].getName());
                }else{
                    scanFolder(path+"//"+files[i].getName());
                }
            }
            return files;
        }
    }

    //实现下载功能，显示弹窗


    private void Download(final String uri,final String filename) {

        //使用自定义对话框显示下载任务

        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.download_dialog,null);
        customizeDialog.setTitle("获取文件");
        customizeDialog.setView(dialogView);
        downloadStatus = (TextView) dialogView.findViewById(R.id.download_status ) ;
        progress = (ProgressBar) dialogView.findViewById(R.id.progress);
        downloadStatus.setText(uri);
        progress.setVisibility(View.GONE) ;
        final Button downloadConfirm = (Button)dialogView.findViewById(R.id.download_confirm);
        downloadConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //downloadStatus .setVisibility(View.VISIBLE) ;
                progress.setVisibility(View.VISIBLE) ;
                downloadConfirm.setVisibility(View.GONE) ;
                new DownloadTask(uri,filename).execute();
            }
        });
        customizeDialog.show();
    }
    class DownloadTask extends AsyncTask<Void,Integer,Boolean> {
        private String fileName;
        private String uri;
        public DownloadTask(String uri,String fileName){
            this.fileName = fileName;//传入的文件名，如Butler.apk
            this.uri = uri;//文件的uri，如http://10.30.177.8:8080/files/Butler.apk
        }

        @Override
        protected void onPreExecute(){
            downloadStatus.setText("开始下载 ...");
            progress.setProgress(0);
        }
        @Override
        protected Boolean doInBackground(Void... params){
            System.out.println("do in background" );

            try {
                //HTTP连接
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                //打开输入流
                InputStream inputStream = connection.getInputStream();
                if (inputStream == null) throw new RuntimeException("stream is null");
                //根据相应获取文件大小
                int fileSize = connection.getContentLength();
                System.out.println("fileSize:"+fileSize);
                System.out.println(Environment.getExternalStorageDirectory().exists());
                //打开或创建目录
                File polarisHubFolder =new File(Environment.getExternalStorageDirectory(),"PolarisHub");
                while(!polarisHubFolder.exists()){
                    System.out.println("Download:"+polarisHubFolder.exists() );
                    polarisHubFolder.mkdir();
                    //if(com.des.butler.MainActivity.DeveloperState==true){Toast.makeText(ItemEdit.this, "����Butler��"+Environment.getExternalStorageDirectory(), Toast.LENGTH_SHORT).show();}
                }
                System.out.println("PolarisHubFolder Exists: "+polarisHubFolder.exists() );
                //打开输出流
                FileOutputStream fos = new FileOutputStream(polarisHubFolder+"/"+fileName );
                System.out.println(fos);

                int count = 0;
                byte buf[] = new byte[1024];
                do{
                    int numread = inputStream.read(buf);
                    count += numread;
                    int progressPercent = (int)(((float)count / fileSize) * 100);
                    System.out.println("progressPercent:"+progressPercent);
                    publishProgress(progressPercent);
                    //更新进度
                    //mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numread <= 0){
                        //下载完成
                        break;
                    }
                    fos.write(buf,0,numread);
                }while(true);

                fos.close();
                inputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            downloadStatus.setText("下载进度："+values[0]);
            progress.setProgress(values[0]) ;
        }
        @Override
        protected void onPostExecute(Boolean result){
            //Toast.makeText(MainActivity.this,"Pose Execute",Toast.LENGTH_SHORT).show();
        }

    }

    //生成二维码

    public static Bitmap createQRcodeImage(String url,int w,int h)
    {
        try
        {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                System.out.println("URL problem");
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    }
                    else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        }
        catch (WriterException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

