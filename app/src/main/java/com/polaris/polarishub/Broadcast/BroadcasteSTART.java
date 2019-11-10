package com.polaris.polarishub.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcasteSTART extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"Server is on", Toast.LENGTH_SHORT).show();
        //Toast.makeText(context,"Server is on", Toast.LENGTH_SHORT).show();

    }
}
