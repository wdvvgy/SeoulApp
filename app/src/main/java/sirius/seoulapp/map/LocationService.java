package sirius.seoulapp.map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by SIRIUS on 2016-09-08.
 */
public class LocationService extends IntentService implements android.location.LocationListener, Serializable {

    public static final String BROADCAST_ACTION = "sirius.seoulapp.broadcast.Location";
    private final String TAG = getClass().getName();
    private LocationManager locationManager;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public Location previousBestLocation = null;
    private Intent intent;
    private LatLng currentPosition;

    public LocationService() {
        super("LocationService");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, this, Looper.getMainLooper());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, this, Looper.getMainLooper());
    }

    public void onLocationChanged(final Location loc) {
        Log.i(TAG, "Location changed");
        if(isBetterLocation(loc, previousBestLocation)) {
            //Message message = mHandler.obtainMessage();
            currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
            intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("currentPosition", currentPosition);
            //sendMessageToHandler(message, 1, intent);
            sendBroadcast(intent);
        }
    }

    public void onProviderDisabled(String provider) {
        //Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
    }

    public void onProviderEnabled(String provider) {
        //Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
