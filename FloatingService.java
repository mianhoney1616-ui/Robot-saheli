package com.example.robotsaheli;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.*;
import android.widget.*;

public class FloatingService extends Service {
    WindowManager wm; View bubble; WindowManager.LayoutParams params; Handler handler=new Handler();
    int x=0, direction=1;
    Runnable motion=new Runnable(){ public void run(){
        if(bubble!=null && params!=null){
            x+=direction*12;
            int max=Math.max(1, getResources().getDisplayMetrics().widthPixels-180);
            if(x<0){x=0;direction=1;} if(x>max){x=max;direction=-1;}
            params.x=x; params.y=(int)(120+40*Math.sin(System.currentTimeMillis()/500.0));
            try{wm.updateViewLayout(bubble,params);}catch(Exception ignored){}
            handler.postDelayed(this,120);
        }
    }};
    @Override public void onCreate(){
        super.onCreate(); wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        bubble=new TextView(this); ((TextView)bubble).setText("🦸🏻‍♂️〰️"); ((TextView)bubble).setTextSize(34); ((TextView)bubble).setTextColor(Color.WHITE);
        bubble.setOnClickListener(v->{ ((TextView)bubble).setText("🦸🏻‍♂️〰️🔴"); });
        int type=Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;
        params=new WindowManager.LayoutParams(180,120,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        params.gravity=Gravity.TOP|Gravity.LEFT; params.x=20; params.y=180;
        try{wm.addView(bubble,params); handler.post(motion);}catch(Exception e){stopSelf();}
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel ch=new NotificationChannel("saheli","Robot Saheli",NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
            Notification n=new Notification.Builder(this,"saheli").setContentTitle("روبوٹ سہیلی").setContentText("تیرتا کردار چل رہا ہے").setSmallIcon(android.R.drawable.ic_dialog_info).build();
            startForeground(9,n);
        }
    }
    @Override public int onStartCommand(Intent i,int f,int id){return START_STICKY;}
    @Override public void onDestroy(){handler.removeCallbacks(motion); if(bubble!=null)try{wm.removeView(bubble);}catch(Exception ignored){} super.onDestroy();}
    @Override public android.os.IBinder onBind(Intent i){return null;}
}
