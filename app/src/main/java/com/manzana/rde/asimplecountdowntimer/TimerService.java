package com.manzana.rde.asimplecountdowntimer;

/**
 * Created by Rodolfo on 28/04/2015.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;


public class TimerService extends Service {
    public static final String UPDATE_STRING = "UPDATE_STRING";
    public static final String FINAL_STRING = "The End";
    public static final String STOP_STRING = "STOP";
    public static final String INTERVAL = "Interval";
    public static final String BROADCAST_ACTION = "com.manzana.rde.acountdowntimer.IntentService";
    public static final String BROADCAST_STOP_ACTION = "com.manzana.rde.acountdowntimer.TimerMainActivity";
    private long interval = -1;
    private long atarget;
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String resultTxt;
            long atime = atarget - SystemClock.elapsedRealtime() + 1000;
            if (atime <=atarget) {
                resultTxt = convertTimeToStr(atime);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(BROADCAST_ACTION);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(UPDATE_STRING, resultTxt);
                sendBroadcast(broadcastIntent);
            }
            if (atime >= 1000)
                handler.postDelayed(this, 10);
            else {
                if (interval > 0) {
                    Intent i = new Intent();
                    i.setClass(TimerService.this, DialogActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                Intent finalIntent = new Intent();
                finalIntent.setAction(BROADCAST_ACTION );
                finalIntent.addCategory(Intent.CATEGORY_DEFAULT);
                finalIntent.putExtra(FINAL_STRING, FINAL_STRING);
                sendBroadcast(finalIntent);
                stopSelf();
            }

        }
    };

    public String convertTimeToStr(long atime) {
        int secs = (int) (atime / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
//        hours = hours % 60;
        mins = mins % 60;
        secs = secs % 60;
        int msecs = (int)atime % 1000;
        int csecs = msecs / 10;

        return String.format("%02d:%02d:%02d", hours, mins, secs);



    }


    public TimerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (interval != -1) {
            return START_NOT_STICKY;
        }
        handler.removeCallbacks(runnable) ;
        interval = intent.getLongExtra(INTERVAL, -1);
        atarget = SystemClock.elapsedRealtime() + interval;

        handler.postDelayed(runnable, 0);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable) ;

    }
}
