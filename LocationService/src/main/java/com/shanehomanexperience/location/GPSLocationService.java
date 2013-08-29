package com.shanehomanexperience.location;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by shoman42 on 8/23/13.
 */
public class GPSLocationService extends Service {
    private final IBinder binder = new LocalBinder();
    private LocationManager lm;
    private LocalLocationListener ll;
    private boolean active;
    private boolean disabledWhileActive;
    private long updateTime;
    private float updateDist;

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        ll = new LocalLocationListener();
        updateTime=0;
        updateDist=0.0f;
        active=false;
        disabledWhileActive=false;
    }
    @Override
    public void onDestroy() {
        if (active)
            stopTracking();
        super.onDestroy();
    }

    void setUpdateTime(long time) {
        boolean needsRestart = active && time != updateTime;
        updateTime=time;
        if (needsRestart)
            startTracking();
    }
    long getUpdateTime() {return updateTime;}
    void setUpdateDist(float dist) {
        boolean needsRestart = active && dist != updateDist;
        updateDist=dist;
        if (needsRestart)
            startTracking();
    }
    float getUpdateDist() {return updateDist;}

    void recordLocation(Location location) {
        //TODO: still dont know how i want to save this
    }

    void startTracking() {
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,updateTime,updateDist,ll);
        active=true;
        Toast.makeText(this,"GPS Tracking Enabled",Toast.LENGTH_SHORT).show();
    }
    void stopTracking() {
        if (active) {
            lm.removeUpdates(ll);
            active=false;
            Toast.makeText(this,"GPS Tracking Disabled", Toast.LENGTH_SHORT).show();
        }
    }

    public class LocalBinder extends Binder {
        GPSLocationService getService() {
            return GPSLocationService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public class LocalLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            recordLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            if (active && s.equals(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getBaseContext(),"GPS enabled, resuming updates", Toast.LENGTH_SHORT).show();
                startTracking();
                disabledWhileActive = false;
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            if (active && s.equals(LocationManager.GPS_PROVIDER)){
                Toast.makeText(getBaseContext(),"GPS disabled, pausing updates",Toast.LENGTH_SHORT).show();
                lm.removeUpdates(ll);
                disabledWhileActive = true;
            }
        }
    };



}
