package com.shanehomanexperience.locationservice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by shoman42 on 8/23/13.
 */
public class LocationService extends Service {
    private final IBinder binder = new LocalBinder();
    private LocationManager lm;
    private LocalLocationListener ll;

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        ll = new LocalLocationListener();
    }

    void newLocation(Location location) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(ll);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    public class LocalLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            newLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
