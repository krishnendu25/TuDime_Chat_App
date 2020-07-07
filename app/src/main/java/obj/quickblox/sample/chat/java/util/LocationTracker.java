package obj.quickblox.sample.chat.java.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Date;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;

public class LocationTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient		mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Location			mCurrentLocation;
    private String				mLastUpdateTime;
    private Context mContext;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int	CONNECTION_FAILURE_RESOLUTION_REQUEST	= 9000;
    private static final String	TAG										= "LocationTracker";

    public LocationTracker(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        buildGoogleApiClient();
        createLocationRequest();
        connectClient();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000); // 60 sec
        mLocationRequest.setSmallestDisplacement(1000); // 1 km
        // mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public LatLng getLocation() {
        if (mCurrentLocation == null) {
            if (mLastLocation != null) {
                return new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
            return null;
        }
        return new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    }

    public boolean isClientConnected() {
        return mGoogleApiClient == null ? false : mGoogleApiClient.isConnected();
    }

    public void disconnectClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void connectClient() {
        if (!isClientConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection falied!");
        try {
            // Start an Activity that tries to resolve the error
            connectionResult.startResolutionForResult((Activity) mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
        } catch (IntentSender.SendIntentException e) {
            // Log the error

            e.printStackTrace();

        }

        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((Activity) mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();

            }
        } else {
            /*
             * If no resolution is available, display a dialog to the user with
             * the error.
             */

        }

        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((Activity) mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the user with
             * the error.
             */

        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            broadcastLocation(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Log.d(TAG, "connection Suspended!");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        broadcastLocation(mCurrentLocation);
    }

    private void broadcastLocation(Location location) {
        if (location == null) {
            return;
        }
        Intent locationIntent = new Intent(AppConstants.LOCAL_BRAODCAST_LOCATION);
        locationIntent.putExtra(AppConstants.EXTRA_LOCATION_LATITUDE, location.getLatitude());
        locationIntent.putExtra(AppConstants.EXTRA_LOCATION_LONGITUDE, location.getLongitude());
       /* SharedPrefsHelper.getInstance().setLatitude(location.getLatitude() + "");
        SharedPrefsHelper.getInstance().setLongitude(location.getLongitude() + "");*/

        Log.e(TAG, "LatLng" + location.getLatitude() + " ---- " + location.getLongitude() + " ---- " + System.currentTimeMillis());

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(locationIntent);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle(""+mContext.getString(R.string.GPS_settings));

        // Setting Dialog Message
        alertDialog.setMessage(""+mContext.getString(R.string.GPS_is_not_enabled_do_you_want_to));

        // On pressing the Settings button.
        alertDialog.setPositiveButton(""+mContext.getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton(""+mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
