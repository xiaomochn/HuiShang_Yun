package com.huishangyun.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class MyNotificationManager {
	private static NotificationManager notificationManager;
    private static final  int NOTIFCATION_CODE = 1016;

    /**
     * 发送通知栏消息
     * @param context
     * @param notif
     */
    public static void sendNotification(Context context, Notification notif) {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFCATION_CODE, notif);
    }

    /**
     * 清除通知栏消息
     * @param context
     */
    public static void cancelNotification(Context context) {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFCATION_CODE);
    }

    /**
     * 发送指定ID通知栏消息
     * @param context
     * @param notif
     * @param code
     */
    public static void sendNotification(Context context, Notification notif, int code) {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(code, notif);
    }

    /**
     * 清除指定通知栏消息
     * @param context
     */
    public static void cancelNotification(Context context, int code) {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(code);
    }
}
