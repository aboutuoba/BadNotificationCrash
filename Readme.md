# RemoteServiceException: Bad notification posted from package *

## 背景

过去有一个奇异的Crash，出现概率比较低，后来Huawei 8.0机器出来后突然暴增，统计了一下大概5%左右的概率会出现

首先确定这不是因为Notification数据填充问题(使用了非@RemoteView修饰的View……)

这个Crash有如下特征

- Huawei 8.0高频出现 (以往系统版本也会出现，但概率很小，系统实现机制存在这种概率)

- 只在更新后一段时间内出现

```
06-23 11:02:35.482 E/AndroidRuntime(19470): FATAL EXCEPTION: main
06-23 11:02:35.482 E/AndroidRuntime(19470): Process: com.tencent.android.qqdownloader, PID: 19470
06-23 11:02:35.482 E/AndroidRuntime(19470): android.app.RemoteServiceException: Bad notification posted from package com.tencent.android.qqdownloader: Couldn't expand RemoteViews for: StatusBarNotification(pkg=com.tencent.android.qqdownloader user=UserHandle{0} id=1024 tag=null key=0|com.tencent.android.qqdownloader|1024|null|10429: Notification(channel=null pri=0 contentView=com.tencent.android.qqdownloader/0x7f09001c vibrate=null sound=null defaults=0x0 flags=0x0 color=0x00000000 vis=PRIVATE))
06-23 11:02:35.482 E/AndroidRuntime(19470):     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2101)
06-23 11:02:35.482 E/AndroidRuntime(19470):     at android.os.Handler.dispatchMessage(Handler.java:108)
06-23 11:02:35.482 E/AndroidRuntime(19470):     at android.os.Looper.loop(Looper.java:166)
06-23 11:02:35.482 E/AndroidRuntime(19470):     at android.app.ActivityThread.main(ActivityThread.java:7425)
06-23 11:02:35.482 E/AndroidRuntime(19470):     at java.lang.reflect.Method.invoke(Native Method)
06-23 11:02:35.482 E/AndroidRuntime(19470):     at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:245)
06-23 11:02:35.482 E/AndroidRuntime(19470):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:921)
```

## 复现

```
06-23 11:02:35.470 W/asset   ( 1567): Asset path /data/app/com.tencent.android.qqdownloader-v-i_PanAFoqhFk4Rk17l0g==/base.apk is neither a directory nor file (type=1).
06-23 11:02:35.470 E/ResourcesManager( 1567): failed to add asset path /data/app/com.tencent.android.qqdownloader-v-i_PanAFoqhFk4Rk17l0g==/base.apk
06-23 11:02:35.470 E/RemoteViews( 1567): Package name com.tencent.android.qqdownloader not found
06-23 11:02:35.470 W/ResourceType( 1567): For resource 0x7f09001c, entry index(28) is beyond type entryCount(6)
06-23 11:02:35.470 W/ResourceType( 1567): Failure getting entry for 0x7f09001c (t=8 e=28) (error -75)
06-23 11:02:35.473 E/StatusBar( 1567): couldn't inflate view for notification com.tencent.android.qqdownloader/0x400
06-23 11:02:35.473 E/StatusBar( 1567): android.content.res.Resources$NotFoundException: Resource ID #0x7f09001c
06-23 11:02:35.473 E/StatusBar( 1567):  at android.content.res.ResourcesImpl.getValue(ResourcesImpl.java:279)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.content.res.Resources.loadXmlResourceParser(Resources.java:2310)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.content.res.Resources.getLayout(Resources.java:1293)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.view.LayoutInflater.inflate(LayoutInflater.java:421)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.widget.RemoteViews.inflateView(RemoteViews.java:3498)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.widget.RemoteViews.apply(RemoteViews.java:3441)
06-23 11:02:35.473 E/StatusBar( 1567):  at com.android.systemui.statusbar.BaseStatusBar.inflateViews(BaseStatusBar.java:2029)
06-23 11:02:35.473 E/StatusBar( 1567):  at com.android.systemui.statusbar.BaseStatusBar.createNotificationViews(BaseStatusBar.java:2605)
06-23 11:02:35.473 E/StatusBar( 1567):  at com.android.systemui.statusbar.phone.PhoneStatusBar.addNotification(PhoneStatusBar.java:1879)
06-23 11:02:35.473 E/StatusBar( 1567):  at com.android.systemui.statusbar.BaseStatusBar$7$2.run(BaseStatusBar.java:798)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.os.Handler.handleCallback(Handler.java:808)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.os.Handler.dispatchMessage(Handler.java:101)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.os.Looper.loop(Looper.java:166)
06-23 11:02:35.473 E/StatusBar( 1567):  at android.app.ActivityThread.main(ActivityThread.java:7425)
06-23 11:02:35.473 E/StatusBar( 1567):  at java.lang.reflect.Method.invoke(Native Method)
06-23 11:02:35.473 E/StatusBar( 1567):  at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:245)
06-23 11:02:35.473 E/StatusBar( 1567):  at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:921)

```

抓到log显示是SystemUI在加载自定义通知资源时发生异常，前面显示是apk不存在，进而无法加载在对应资源

这里的Log不一定一致，但都是对应应用的apk不存在，加载自定义通知布局id可能会加载在厂商自身的，非RemoteView

前面提到这个Crash`只在更新后一段时间内出现`，因为应用更新后，安装路径会变，这里SystemUI用更新前的apk来加载资源

## 对比

前面确定了这个Crash的直接原因是SystemUI用应用更新前的apk来加载资源，但应用更新后旧的apk也会被删除，导致无法加载到自定义通知的资源，所以导致异常，异常在SystemUI内部会被捕获，但最终会一级级传递到应用的ActivityThread里

因为这个Crash正常情况下不容易复现，通过分析应用自身代码和系统实现很难找到原因

但通过对比其它应用，似乎其它应用并不存在这个问题

所以……

## 分析

这里省略……你懂的分析过程

最终定位到这是Huawei`智能通知管理`导致的问题

`智能通知管理`打开后会对部分应用的通知进行优化管理，确实提高了手机用户使用体验，但这个功能实现可能存在bug

打开`智能通知管理`后，只要在名单中，发送自定义通知就有概率会触发

所以简单写了这个Demo，模拟应用启动后发送了2条自定义通知，基本是100%必现

随便拿了应用宝，QQ浏览器，腾讯新闻，360手机助手的包名来试，都会Crash

目前试了腾讯手机管理的包名是没这个问题，其它自己随便写的也没这个问题

## 验证

替换自己想要测的包名，编出包，可以手动安装打开，重复这个过程

或者用目录下`triggerCrash.sh`这个脚本自动操作
