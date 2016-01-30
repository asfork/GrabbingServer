# 项目手册

## 1. 开发环境

IDE: [Android Studio(android app)](http://developer.android.com/sdk/index.html), [Intellij IDEA(server)](https://www.jetbrains.com/idea/)  
JDK: [ORACLE JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

## 2. 目录介绍

* `grabbing_v1.0.apk` 为安卓端应用，用于拍摄照片并与服务器进行相关通信。在 MIUI 上安装后需要在安全中心打开自启及摄像头等权限，具体步骤参看 `3. 安卓端使用介绍`。
* `server` 文件夹内为服务端程序，用于发送广播及接收图片等。启动方法参看 `4. 服务端使用介绍`
* `src` 内为项目源代码。其中 `Grabbing` 为安卓端开发目录，使用 Android Studio 的 `Open an existing Android Studio project` 打开此目录即可。`GrabbingServer` 为 服务端开发目录，使用 Intellij IDEA 或 Eclipse 打开此目录即可。
* `jre-8u72-windows-i586.exe` 为服务端的 JAVA 运行环境，也可从 [Oracle 官网](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 下载最新版本安装。

## 3. 安卓端使用介绍

首次安装应用后，为应用开启相应的权限

* 安全中心→授权管理→自启动管理，设置 Grabbing 允许自启
* 安全中心→授权管理→应用权限管理，打开 Grabbing 所有权限
* 首次打开 Grabbing 应用，可能需要手动点击 'START UDP LISTENER' 按钮，通知栏出现 'Broadcast Listener Service & Keeping listening' 即服务监听服务开启成功

红米 2 上默认的 MIUI 会强制终止应用在非前台状态的保持唤醒行为，导致 UDP 广播监听服务随设备进入休眠状态，不能持续监听。  
解决方案是保持 Grabbing 应用始终处于前台。

## 4. 服务端使用介绍

安装 `jre-8u72-windows-i586.exe` Java 运行环境，并添加 `JAVA_HOME` 等环境变量，具体步骤可参看 [如何安装和配置java环境，让电脑支持java运行](http://jingyan.baidu.com/article/e75aca85b29c3b142edac6a8.html)。

* `GrabbingServer.jar` 用于接收来自设备的广播及图片，需要提前启动

    ```sh
    java -jar GrabbingServer.jar
    ```

* `GrabbingBroadcast.jar` 用于向局域网内设备发送广播，在需要时调用即可。有如下几个可用参数

    > --address (-a) <192.0.0.*> : The IP address of the target device  
    --give (-g) <command>      : Give broadcast to all devices in LAN (default:grab)

* 向所有设备广播，让其拍照并上传照片

    ```sh
    java -jar GrabbingBroadcast.jar
    ```

    或者

    ```sh
    java -jar GrabbingBroadcast.jar -g grab
    ```

    或者

    ```sh
    java -jar GrabbingBroadcast.jar --give grab
    ```

* 获取局域网内所有可用的设备列表

    ```sh
    java -jar GrabbingBroadcast.jar -g devices
    ```

* 向某一台设备广播，让其拍照并上传照片

    ```sh
    java -jar GrabbingBroadcast.jar -a 192.168.1.9
    ```