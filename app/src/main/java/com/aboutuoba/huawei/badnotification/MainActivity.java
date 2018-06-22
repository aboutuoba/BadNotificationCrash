package com.aboutuoba.huawei.badnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RemoteViews;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    private void postNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("Hello " + notificationId);
        builder.setContentText("Bad Notification Crash");
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        builder.setContent(remoteViews);

        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 模拟应用某个时间发了一个自定义通知
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postNotification(1024);
            }
        }, 2000);

        // 模拟应用某个时间发了另一个自定义通知
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postNotification(2048);
            }
        }, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }
}
