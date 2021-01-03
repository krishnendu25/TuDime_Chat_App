package obj.quickblox.sample.chat.java.ui.activity;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.ui.Callback.ChatConstants;
import obj.quickblox.sample.chat.java.ui.Model.Location_Venues;
import obj.quickblox.sample.chat.java.ui.adapter.LocationsListAdapter;
import obj.quickblox.sample.chat.java.util.LocationTracker;

public class MapSendLocationActivity extends BaseActivity implements IJSONParseListener, View.OnClickListener, OnMapReadyCallback {
    double mCurrentLatitude;
    double mCurrentLongitude;
    private GoogleMap mGoogleMap;
    private TextView txvShowLocation;
    private TextView edtLocationSearch;
    private int Bounce_Count = 0;
    private LocationManager locationManager;
    private String PlaceApiKEY = "AIzaSyB7Uf8EW3nIERD6A6HBbc36hNlfxPfYgW0";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_send_location);
        hideActionbar();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), PlaceApiKEY);
        }
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        findViewById(R.id.imgBack).setOnClickListener(this);
        edtLocationSearch = (TextView) findViewById(R.id.edtLocationSearch);
        findViewById(R.id.txvSendLocation).setOnClickListener(this);
        txvShowLocation = (TextView) findViewById(R.id.txvShowLocation);
        txvShowLocation.setOnClickListener(this);
        edtLocationSearch.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frame);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edtLocationSearch:
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent df = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(MapSendLocationActivity.this);
                startActivityForResult(df, AUTOCOMPLETE_REQUEST_CODE);
            break;
            case R.id.imgBack:
                finish();
                break;
            case R.id.txvSendLocation:
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    try {
                        get_Current_User_Location();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendLocation(mCurrentLatitude, mCurrentLongitude);
                }
                break;

        }
    }

    private void get_Current_User_Location() {
        mGoogleMap.clear();
        /*  progressDialog.show();*/
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 199);

            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (location != null) {
                    try {
                        mCurrentLatitude = location.getLatitude();
                        mCurrentLongitude = location.getLongitude();
                        LatLng coordinate = new LatLng(mCurrentLatitude, mCurrentLongitude); //Store these lat lng values somewhere. These should be constant.
                        CameraUpdate locationn = CameraUpdateFactory.newLatLngZoom(
                                coordinate, 16);
                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(
                                location.getLatitude(), location.getLongitude()));
                        // ROSE color icon
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker));
                        markerOptions.position(coordinate);
                        markerOptions.draggable(true);
                        // adding markerOptions
                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        setMarkerBounce(marker);
                        CircleOptions circleOptions = new CircleOptions()
                                .center(coordinate)
                                .radius(250)
                                .strokeWidth(0)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.parseColor("#5B0091EA"));
                        // Supported formats are: #RRGGBB #AARRGGBB
                        //   #AA is the alpha, or amount of transparency
                        mGoogleMap.addCircle(circleOptions);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                        mGoogleMap.setMyLocationEnabled(true);


                    } catch (NullPointerException e) {
                        Log.e("", "moveCamera: NullPointerException: " + e.getMessage());
                    }

                } else if (location1 != null) {
                    try {
                        mCurrentLatitude = location1.getLatitude();
                        mCurrentLongitude = location1.getLongitude();
                        LatLng coordinate = new LatLng(mCurrentLatitude, mCurrentLongitude); //Store these lat lng values somewhere. These should be constant.
                        CameraUpdate locationn = CameraUpdateFactory.newLatLngZoom(
                                coordinate, 15);
                        // create markerOptions
                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(
                                location.getLatitude(), location.getLongitude()));
                        // ROSE color icon
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker));
                        markerOptions.position(coordinate);
                        markerOptions.draggable(true);
                        // adding markerOptions
                        mGoogleMap.addMarker(markerOptions);
                        CircleOptions circleOptions = new CircleOptions()
                                .center(coordinate)
                                .radius(250)
                                .strokeWidth(0)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.parseColor("#5B0091EA"));
                        mGoogleMap.addCircle(circleOptions);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        mGoogleMap.setMyLocationEnabled(true);

                    } catch (NullPointerException e) {
                        Log.e("", "moveCamera: NullPointerException: " + e.getMessage());
                    }
                } else if (location2 != null) {
                    try {
                        mCurrentLatitude = location2.getLatitude();
                        mCurrentLongitude = location2.getLongitude();
                        LatLng coordinate = new LatLng(mCurrentLatitude, mCurrentLongitude); //Store these lat lng values somewhere. These should be constant.
                        CameraUpdate locationn = CameraUpdateFactory.newLatLngZoom(
                                coordinate, 15);
                        // create markerOptions
                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(
                                location.getLatitude(), location.getLongitude()));
                        // ROSE color icon
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker));
                        markerOptions.position(coordinate);
                        markerOptions.draggable(true);
                        // adding markerOptions
                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        CircleOptions circleOptions = new CircleOptions()
                                .center(coordinate)
                                .radius(250)
                                .strokeWidth(0)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.parseColor("#5B0091EA"));
                        mGoogleMap.addCircle(circleOptions);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        mGoogleMap.setMyLocationEnabled(true);

                    } catch (NullPointerException e) {
                        Log.e("", "moveCamera: NullPointerException: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
                    /*  progressDialog.dismiss();*/
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendLocation(double lat, double lng) {
        ChatActivity.Class_Name = "MapSendLocationActivity";
        ChatActivity.SMS_VALUE = "http://www.google.com/maps/place/" + lat + "," + lng;
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker))).setDraggable(false);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                get_Current_User_Location();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

   /* private void hitApiRequest(int reqType) {
        String url;
        url = "https://api.foursquare.com/v2/venues/search?ll=" + 22.5688291 + "," + 88.4317944 + "&client_id=YKSFTFURQKGZUFYEFGUDXHRLUEGTLEOXMBYUTU3AYA4LLKX5&client_secret=BH3IPIEM33UPG14QTDVCICC142Q3M34TN0EVT55NNBOZY4GX&v=" + timeMilisToString(System.currentTimeMillis()) + "&query=" + mQueryString;
        // showProgressDialog(R.string.load);
        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, url,
                454, this, parms, false, false, Agent_Array_Object);


    }*/

    private String timeMilisToString(long milis) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd", Locale.GERMAN);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milis);
        return sd.format(calendar.getTime());
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
    }

    private void setMarkerBounce(final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + t);
                if (t > 0.0) {
                    Bounce_Count++;
                    handler.postDelayed(this, 16);
                } else {
                    if (Bounce_Count == 1)
                        setMarkerBounce(marker);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                mCurrentLatitude = place.getLatLng().latitude;
                mCurrentLongitude = place.getLatLng().longitude;
                mGoogleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(
                        place.getLatLng().latitude, place.getLatLng().longitude));
                // ROSE color icon
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker));
                markerOptions.position( place.getLatLng());
                markerOptions.draggable(true);
                // adding markerOptions
                Marker marker = mGoogleMap.addMarker(markerOptions);
                setMarkerBounce(marker);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                edtLocationSearch.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
