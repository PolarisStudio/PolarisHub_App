package com.polaris.polarishub;

import android.content.Intent;
import android.widget.Toast;

import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.ResponseBody;
import com.yanzhenjie.andserver.annotation.RestController;


@Controller
public class HubManager {
    @RequestMapping("/")
    public String login(){
        String hello="forward:index.html";
        Intent intent = new Intent("com.polaris.polarishub.ACCESS_REQUEST");
        return hello;
    }
}
