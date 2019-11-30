# PolarisHub_Android 技术文档

## 概述

PolarisHub_Android （PHA）延续了PolarisHub_Flask的基本设计，以“选择文件-点击生成二维码-扫码下载/访问链接下载”为基本操作，以“局域网文件互传工具”为定位，以“不消耗流量、速度快、无压缩”为主要优点进行设计。PHA使用Java语言以安卓原生的模式重写了Flask版本并与之兼容，是PolarisHub在安卓设备领域的扩展。

## 原理

与flask版本相同，分享文件时，PHA会在本地运行一个安卓原生服务器进行内容提供。访问者需提供一个REST风格的网络请求，服务器将根据请求的内容返回相应文件。同时PHA还提供扫码功能，通过扫描其他PolarisHub二维码开启下载任务。PHA提供本地文件管理页面以支持文件的增删改查功能，PHA会将与PHA关联过的文件统一存放在SD卡PolarisHub目录下以方便查找。

## 类

### 活动类

#### MainActivity

主界面控制，控制和响应服务器

#### FileList

文件列表页，用于罗列文件。

###服务类

#### CoreService

Servive类，在此服务中实例化AndServer和Server，从而实现服务器功能。

#### HubManager

服务器逻辑的实现。通过编译时注解 `@RestController` 实现对服务器的控制。

#### AppConfig

用于声明一些路径和设置

### 工具类

#### ServerManager

BroadcastReceiver类 作为广播接收器处理活动之间的通信

#### IpManager

获取本机IP

#### CrashHandler

在软件崩溃时调用，会生成错误日志并存放至SD卡中

#### TDPitem

文件列表的单元类，可以添加对文件信息进行处理的逻辑

#### TdpAdapter

列表匹配工具，用于将单元类逐一添加至列表中





## 依赖

### AndServer2.0.0



## 版本历史

### 0.测试版

####0.1

第一个可用版本，有扫一扫和打开目录功能，需手动添加文件到目录中。存在服务器不会自启的问题。（[下载链接](https://github.com/PolarisStudio/PolarisHub_App/blob/7cb13a3acd43dbf438ae329c5d9ff58ad7df3fd4/app/release/app-release.apk?raw=true)）

#### 0.2

在文件列表页面添加了“添加文件”按钮，可直接从文件管理器选择文件（[下载链接](https://github.com/PolarisStudio/PolarisHub_App/blob/0e185130de4cc1ae9919d005b86d2903e8ea6743/app/release/app-release.apk?raw=true)）

#### 0.3

添加了打印错误日志的功能（[下载链接](https://github.com/PolarisStudio/PolarisHub_App/blob/273dc88c7f2fb8cf44e85d60ec30534bb1a9ad6e/app/release/app-release.apk?raw=true)）

##### 0.3.1

修复了bitmap绘制导致崩溃的问题（[下载链接](https://github.com/PolarisStudio/PolarisHub_App/blob/04fbc22224e6d6dee94b54f040dfdeb3543c0a0a/app/release/app-release.apk?raw=true)）

##### 0.3.2

添加了长按PolarisHub图标进入开发者模式、长按扫一扫自动检测剪贴板的PolarisHub链接并开启下载的逻辑。（[下载链接](https://github.com/PolarisStudio/PolarisHub_App/blob/1d906bc5919e5e03d5666bc7026dad247705d5d8/app/release/app-release.apk?raw=true)）

#### 0.4

基本可用的版本系列

#####0.4.0

修复了服务器不会自启的问题，修复了进入开发者模式之后变不回正常模式的bug，是一个较为完整的版本。（[下载链接](https://github.com/PolarisStudio/PolarisHub_App/blob/d58b5c1242f3520cc398ef65cd10319838490f2a/app/release/app-release.apk?raw=true)）

### X.0下一步...

实现文件夹打包传送、服务器进程防杀

## 更多

[北极星工作室](
