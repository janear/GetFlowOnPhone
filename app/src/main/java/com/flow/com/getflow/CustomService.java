package com.flow.com.getflow;

/**
 * Created by janear on 15-12-21.
 */

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CustomService extends Service {
    private static final String TAG = "FLOW_SERVICE " ;
    private static final String DYNAMICACTION="com.janear.dynamic";

    Timer timer;
    Handler handler;
    String package_name="";

    long timeInterval = 20;

    ArrayList<String> arraryList = new ArrayList<>();

    public void setInterval(long _interval){
        timeInterval = _interval;
    }

    public void setPackage_name(String _package_name){
        package_name = _package_name;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if(arraryList.size() > 0)
                   arraryList.clear();

                getAppTrafficListByPackageName();
                // 定时更新
                // 发送广播
                Intent intent = new Intent();
                intent.setAction(DYNAMICACTION);
                intent.putExtra("trafficstr", arraryList);
                sendBroadcast(intent);
                Log.d("Janear","发送消息");

//                Message msg = handler.obtainMessage();
//               msg.what = "";
//               handler.sendMessage( msg );

            }
        }, 1000, timeInterval * 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    public void getApplicationPackageInfo(PackageInfo info){
        String[] premissions = info.requestedPermissions;
        if (premissions != null && premissions.length > 0) {
            for (String premission : premissions) {
                if ("android.permission.INTERNET".equals(premission)) {
                    int uId = info.applicationInfo.uid;
                    long rx = TrafficStats.getUidRxBytes(uId);
                    long tx = TrafficStats.getUidTxBytes(uId);
                    if (rx < 0 || tx < 0) {
                            continue;
                    } else {
//                        float total = (rx + tx) / 1024.0;
                        Log.d("Janear","add到数组");
                        arraryList.add(info.packageName.toString() + ":" + (rx + tx) + " Bytes");
                        Log.d(TAG + info.packageName.toString() + ":", (rx + tx) + " Bytes");
                    }
                }
            }
        }
    }
    public void getAppTrafficListByPackageName(){
        PackageManager pm = getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : pinfos){
            String[] premissions = info.requestedPermissions;
            if(package_name.length() == 0){
                //获取所有app的流量信息
                getApplicationPackageInfo(info);
            }
            else if(info.packageName.toString().equalsIgnoreCase(package_name)){
                //获取指定的app的流量信息
                getApplicationPackageInfo(info);
                return;
            }
        }
    }

    public void stopTimer(){
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        stopTimer();
        Log.d(TAG, "onDestroy() executed");
    }

}
