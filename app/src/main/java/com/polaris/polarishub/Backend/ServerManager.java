package com.polaris.polarishub.Backend;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/*public class ServerManager {
    private Server mServer;
    public ServerManager(Context context){
        InetAddress inetAddress ;
        try{
            inetAddress = InetAddress.getByName(IpManager.getIpAddress(context));
            mServer = AndServer.serverBuilder()
                    .inetAddress(inetAddress)
                    .port(8080)
                    .timeout(10, TimeUnit.SECONDS)
                    .listener(new Server.ServerListener(){
                        @Override
                        public void onStart{

                        }
                        @Override
                        public void onStopped{

                        }
                        @Override
                        public void onExceptio{

                        }
                    })
                    .build();
        }catch(Exception e){
            Toast.makeText(context,"Ip 转换失败", Toast.LENGTH_LONG).show();
        }

    }

}*/
public class ServerManager {

    private Server mServer;

    /**
     * Create server.
     */
    public ServerManager(Context context) {
        try {
            InetAddress inetAddress = InetAddress.getByName(IpManager.getIpAddress(context));
            mServer = AndServer.serverBuilder(context)
                    .inetAddress(inetAddress)
                    .port(8080)
                    .timeout(10, TimeUnit.SECONDS)
                    .listener(new Server.ServerListener() {
                        @Override
                        public void onStarted() {
                            // TODO The server started successfully.
                        }

                        @Override
                        public void onStopped() {
                            // TODO The server has stopped.
                        }

                        @Override
                        public void onException(Exception e) {
                            // TODO An exception occurred while the server was starting.
                        }
                    })
                    .build();
        }catch(Exception UnknownHostException){

        }


    }

    /**
     * Start server.
     */
    public void startServer() {
        if (mServer.isRunning()) {
            // TODO The server is already up.
        } else {
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    public void stopServer() {
        if (mServer.isRunning()) {
            mServer.shutdown();
        } else {
            Log.w("AndServer", "The server has not started yet.");
        }
    }
}
