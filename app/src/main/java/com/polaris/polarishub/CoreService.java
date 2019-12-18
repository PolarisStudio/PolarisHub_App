package com.polaris.polarishub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.polaris.polarishub.Tools.IpManager;
import com.polaris.polarishub.Tools.ServerManager;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class CoreService extends Service {


    private Server mServer;             //创建server


    @Override
    public void onCreate() {
        System.out.println("CoreService: onCreate");
        try{
            System.out.println("CoreSeivice: Start BUilding Server");
            mServer = AndServer.serverBuilder(this)
                    .inetAddress(InetAddress.getByName(IpManager.getIpAddress(this)))
                    .port(8080)
                    .timeout(10, TimeUnit.SECONDS)
                    .listener(new Server.ServerListener() {
                        @Override
                        public void onStarted() {
                            String hostAddress = mServer.getInetAddress().getHostAddress();
                            ServerManager.onServerStart(CoreService.this, hostAddress);
                        }

                        @Override
                        public void onStopped() {
                            ServerManager.onServerStop(CoreService.this);
                        }

                        @Override
                        public void onException(Exception e) {
                            ServerManager.onServerError(CoreService.this, e.getMessage());
                        }
                    })
                    .build();                                                                    //创建服务器
        }catch(UnknownHostException e){

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  //传入intent
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    /**
     * Start server.
     */
    private void startServer() {
        if (mServer.isRunning()) {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            ServerManager.onServerStart(CoreService.this, hostAddress);//最终实现 更新View
        } else {
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        mServer.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
