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

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the GPS; designed to be directly interacted with by client code as a bound service
 */
public class GPSLocationService extends Service {
    private final IBinder binder = new LocalBinder();
    private final long DEFAULT_UPDATE_TIME = 1000;
    private final float DEFAULT_UPDATE_DIST = 0.0f;
    private LocationManager lm;
    private LocalLocationListener ll;
    private boolean active;
    private boolean disabledWhileActive;
    private long updateTime;
    private float updateDist;
    private Location lastLoc;
    private List<Location> locationList;

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        ll = new LocalLocationListener();
        updateTime = DEFAULT_UPDATE_TIME;
        updateDist = DEFAULT_UPDATE_DIST;
        active = false;
        disabledWhileActive = false;
        locationList = new ArrayList<Location>();
    }

    @Override
    public void onDestroy() {
        if (active)
            stopTracking();
        super.onDestroy();
    }

    /**
     * Sets the time between requested updates, restarting tracking if changed
     *
     * @param time Time between updates in ms
     */
    void setUpdateTime(long time) {
        boolean needsRestart = active && time != updateTime;
        updateTime = time;
        if (needsRestart)
            startTracking();
    }

    long getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets the distance between requested updates, restarting tracking if changed
     *
     * @param dist Distance between updates in m
     */
    void setUpdateDist(float dist) {
        boolean needsRestart = active && dist != updateDist;
        updateDist = dist;
        if (needsRestart)
            startTracking();
    }

    float getUpdateDist() {
        return updateDist;
    }

    private void recordLocation(Location location) {
        lastLoc = location;
        locationList.add(location);
    }

    /**
     * Gets last recorded location
     *
     * @return
     */
    Location getLastLocation() {
        return lastLoc;
    }

    /**
     * Gets all locations recorded since the service was initialized or reset
     *
     * @param reset clears all recorded locations from the service
     * @return
     */
    List<Location> getLocations(boolean reset) {
        List<Location> out = locationList;
        if (reset) {
            locationList = new ArrayList<Location>();
        }
        return out;
    }

    /**
     * Begins GPS Tracking
     */
    void startTracking() {
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateTime, updateDist, ll);
        active = true;
        Toast.makeText(this, "GPS Tracking Enabled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stops GPS tracking
     */
    void stopTracking() {
        if (active) {
            lm.removeUpdates(ll);
            active = false;
            Toast.makeText(this, "GPS Tracking Disabled", Toast.LENGTH_SHORT).show();
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
        updateTime = intent.getLongExtra("time", DEFAULT_UPDATE_TIME);
        updateDist = intent.getFloatExtra("dist", DEFAULT_UPDATE_DIST);
        startTracking();
        return super.onStartCommand(intent, flags, startId);
    }

    public class LocalLocationListener implements LocationListener {
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
                Toast.makeText(getBaseContext(), "GPS enabled, resuming updates", Toast.LENGTH_SHORT).show();
                startTracking();
                disabledWhileActive = false;
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            if (active && s.equals(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getBaseContext(), "GPS disabled, pausing updates", Toast.LENGTH_SHORT).show();
                lm.removeUpdates(ll);
                disabledWhileActive = true;
            }
        }
    }
}
