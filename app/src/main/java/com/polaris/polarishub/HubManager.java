package com.polaris.polarishub;

import android.content.Intent;
import android.os.Environment;

import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;

import java.io.File;

@RestController
public class HubManager {
    //空路径获取用户名片（尚未完善）
    @RequestMapping("/")
    public String login(){

        String hello="hahaha";
        return hello;
    }
    //"/files"不加参数处理为获取文件目录（尚未开始进展）
    @GetMapping("/files")
    public void testFile(HttpRequest request, HttpResponse response){
        File polarisHubFolder = new File(Environment.getExternalStorageDirectory(), "PolarisHub");
        if (!polarisHubFolder.exists()) {
            polarisHubFolder.mkdir();
        }
        File testFile = new File(polarisHubFolder, "Butler1.3.3.2.apk");
        ResponseBody body = new FileBody(testFile);
        response.setBody(body);
    }
    //"/files"处理下载请求，使用路径变量读取文件名
    @GetMapping("/files/{filename}")
    public void returnFile(@PathVariable("filename")String filename, HttpResponse response){
        String fileName = filename;
        File polarisHubFolder = new File(Environment.getExternalStorageDirectory(), "PolarisHub");
        if (!polarisHubFolder.exists()) {
            polarisHubFolder.mkdir();
        }
        //System.out.println("PolarisHubFolder Exists: " + polarisHubFolder.exists());
        File phbFile = new File(polarisHubFolder, fileName);
        if (!phbFile.exists()) {
            //如果文件不存在，处理异常（尚未完善）
            long length = phbFile.length();
            ResponseBody body = new FileBody(phbFile);
            response.setBody(body);
        }else {
            //如果文件存在，以 file body 方式返回文件。
            long length = phbFile.length();
            ResponseBody body = new FileBody(phbFile);
            response.setBody(body);
        }
    }
}
