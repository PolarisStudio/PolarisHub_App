/*
 * Copyright © 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.polaris.polarishub.CoreService;
import com.polaris.polarishub.MainActivity;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class ServerManager extends BroadcastReceiver {

    private static final String ACTION = "com.yanzhenjie.andserver.receiver";

    private static final String CMD_KEY = "CMD_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";

    private static final int CMD_VALUE_START = 1;
    private static final int CMD_VALUE_ERROR = 2;
    private static final int CMD_VALUE_STOP = 4;

    /**
     * Notify serverStart.
     *
     * @param context context.
     */
    public static void onServerStart(Context context, String hostAddress) {
        sendBroadcast(context, CMD_VALUE_START, hostAddress);
    }

    /**
     * Notify serverStop.
     *
     * @param context context.
     */
    public static void onServerError(Context context, String error) {
        sendBroadcast(context, CMD_VALUE_ERROR, error);
    }

    /**
     * Notify serverStop.
     *
     * @param context context.
     */
    public static void onServerStop(Context context) {
        sendBroadcast(context, CMD_VALUE_STOP);
    }

    private static void sendBroadcast(Context context, int cmd) {
        sendBroadcast(context, cmd, null);
    }

    private static void sendBroadcast(Context context, int cmd, String message) {
        Intent broadcast = new Intent(ACTION);
        broadcast.putExtra(CMD_KEY, cmd);
        broadcast.putExtra(MESSAGE_KEY, message);
        context.sendBroadcast(broadcast);
    }

    private MainActivity mActivity;
    private Intent mService;

    public ServerManager(MainActivity activity) {             //构造方法 传入当前的 MainActivity类的 activity 为 mActivity；
        this.mActivity = activity;                             //并将 mService 这个 Intent 设置为: 从Mainactivity 跳转到 CoreService
        mService = new Intent(activity, CoreService.class);
    }

    /**
     * Register broadcast.
     */
    public void register() {                                  // 在 MainActivity 中 ，在 实例化ServerManager 后 使用
        IntentFilter filter = new IntentFilter(ACTION);       // 实例化一个 打开andserver.receiver 的 intent 的 filter（下面重写了OnReceive）
        mActivity.registerReceiver(this, filter);             // 调用 AppCompatActivity 自带的 registerReceiver() 方法， 传入 ServerManager作为上下文 和 intentFilter
    }

    /**
     * UnRegister broadcast.
     */
    public void unRegister() {
        mActivity.unregisterReceiver(this);
    }

    public void startServer() {                                             // 被 MainActivity 调用，去调用 AppCompatActivity 中的 startService（传入Intent：mService）（没错这是个Intent）
        mActivity.startService(mService);                                   // 到此为止的结果就是 打开 CoreService
        System.out.println("ServerManager.startServer: launch by intent:CoreService");
    }

    public void stopServer() {
        mActivity.stopService(mService);
    }

    @Override
    public void onReceive(Context context, Intent intent) {            //这个就是让view给个相应
        String action = intent.getAction();
        if (ACTION.equals(action)) {
            int cmd = intent.getIntExtra(CMD_KEY, 0);
            switch (cmd) {
                case CMD_VALUE_START: {
                    String ip = intent.getStringExtra(MESSAGE_KEY);
                    mActivity.onServerStart(ip);
                    break;
                }
                case CMD_VALUE_ERROR: {
                    String error = intent.getStringExtra(MESSAGE_KEY);
                    mActivity.onServerError(error);
                    break;
                }
                case CMD_VALUE_STOP: {
                    mActivity.onServerStop();
                    break;
                }
            }
        }
    }
}