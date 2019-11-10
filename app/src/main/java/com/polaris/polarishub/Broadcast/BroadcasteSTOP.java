package com.polaris.polarishub.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcasteSTOP extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Server is fail", Toast.LENGTH_SHORT).show();
        //在这里写上相关的处理代码，一般来说，不要此添加过多的逻辑或者是进行任何的耗时操作
        //因为广播接收器中是不允许开启多线程的，过久的操作就会出现报错
        //因此广播接收器更多的是扮演一种打开程序其他组件的角色，比如创建一条状态栏通知，或者启动某个服务
    }
}