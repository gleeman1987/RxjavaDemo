package com.example.lenovo.rxjavademo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;

import rx.Subscription;

/**
 * OKLINE(Hangzhou) Co.,Ltd
 * Author:Zheng Jun
 * E-Mail:zhengjun@okline.cn
 * Date: 2016/8/25. 16:22
 */
public class Utils {
    private static final String SHAREDPREFERENCE_NAME = "config.xml";
    private static final String TIME_STAMP_KEY = "signin";
    private static SharedPreferences sharedPreferences;

    public static void unsubscribeRxJava(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static void showLog(String s) {
        String s1 = "";
        for (int i = 0; i < 20; i++) {
            s1 = s1 + "■";
        }
        System.out.println(s1+s+s1);
    }

    public static Uri string2bmp(Context context, String qr_code, String trade_no) {
        /**
         * 转换照片之前的准备工作
         */
        File externalFilesDir = context.getFilesDir();
        File file = new File(externalFilesDir, trade_no+".jpg");
        if (file.length()>0){
            return Uri.fromFile(file);
        } else {
            File[] files = externalFilesDir.listFiles();
            if (files != null && files.length>0) {
                for (File file1 : files) {
                    file1.delete();
                }
            }
        }

        /**
         * 开始转换照片
         */
        byte[] bytes = Base64.decode(qr_code, Base64.DEFAULT);
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            if (bitmap != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Uri.fromFile(file);
            } else {
                throw new Exception("Fail to generate QR_CODE bitmap");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void writePreference(Context context, long l){
        sharedPreferences = context.getSharedPreferences(SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(TIME_STAMP_KEY,l).apply();
        showLog("成功写入日期:"+ DateFormat.format("yyyy-MM-dd",l));
    }

    public static String readPreference(Context context){
        sharedPreferences = context.getSharedPreferences(SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
        long aLong = sharedPreferences.getLong(TIME_STAMP_KEY, 0);
        CharSequence format = DateFormat.format("yyyy-MM-dd", aLong);
        showLog("成功读取日期:"+ format);
        return format.toString();
    }
}
