package obj.quickblox.sample.chat.java.ui.activity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String BASE_URL_FOURSQAURE = "https://api.foursquare.com/v2/venues/search";
    private String FOURSQUARE_CLIENT_ID = "YKSFTFURQKGZUFYEFGUDXHRLUEGTLEOXMBYUTU3AYA4LLKX5";
    private String FOURSQUARE_CLIENT_SECRET = "BH3IPIEM33UPG14QTDVCICC142Q3M34TN0EVT55NNBOZY4GX";
    private String mQueryString;
    private GoogleMap mGoogleMap;
    private String mLocality;
    private TextView txvShowLocation;
    private EditText edtLocationSearch;
    private ListView listviewLocations;
    private boolean mIsViewHidden = true;
    private float density;
    private int end;
    private int start;
    private int Bounce_Count = 0;
    private ArrayList<Location_Venues> mVenuesList = new ArrayList<>();
    private LocationsListAdapter adapter;
    private LocationTracker locationTracker;
    private boolean isInitialized = false;
    private LocationManager locationManager;
 /*   private BroadcastReceiver mLocationBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mCurrentLatitude = intent.getDoubleExtra(AppConstants.EXTRA_LOCATION_LATITUDE, 0.0);
            mCurrentLongitude = intent.getDoubleExtra(AppConstants.EXTRA_LOCATION_LONGITUDE, 0.0);
            mLocality = getLocality(new LatLng(mCurrentLatitude, mCurrentLatitude), MapSendLocationActivity.this);
            hitApiRequest(ApiConstants.REQUEST.UPDATE_LOCATION);
            locationTracker.disconnectClient();
            isInitialized = true;
        }
    };*/
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
            if (chars.length() > 0) {
                mQueryString = chars.toString();
                hitApiRequest(0);
            } else {
                mQueryString = "";
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_send_location);
        hideActionbar();
        locationTracker = new LocationTracker(this);
        density = getResources().getDisplayMetrics().density;
        findViewById(R.id.imgBack).setOnClickListener(this);
        edtLocationSearch = (EditText) findViewById(R.id.edtLocationSearch);
        listviewLocations = (ListView) findViewById(R.id.listviewLocations);
        findViewById(R.id.txvSendLocation).setOnClickListener(this);
        txvShowLocation = (TextView) findViewById(R.id.txvShowLocation);
        txvShowLocation.setOnClickListener(this);
        listviewLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  sendLocation(mVenuesList.get(position).getLocation().getLat(), mVenuesList.get(position).getLocation().getLng(), mVenuesList.get(position).getName());
                sendLocation(Double.valueOf(mVenuesList.get(position).getLat()), Double.valueOf(mVenuesList.get(position).getLng()), mVenuesList.get(position).getName());
            }
        });
        hitApiRequest(ApiConstants.REQUEST.UPDATE_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frame);
        mapFragment.getMapAsync(this);
        adapter = new LocationsListAdapter(this);
        listviewLocations.setAdapter(adapter);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.txvShowLocation:
                // animateListView();
                showHideListView();
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
                    sendLocation(mCurrentLatitude, mCurrentLongitude, mLocality);
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

    private void sendLocation(double lat, double lng, String locality) {
       /* Intent intent = new Intent();
        intent.putExtra(ChatConstants.EXTRA_CHAT_LOCATION, lat + "," + lng + "," + locality);
        setResult(RESULT_OK, intent);*/
       ChatActivity.Class_Name="MapSendLocationActivity";
       ChatActivity.SMS_VALUE="http://www.google.com/maps/place/"+lat+","+lng;


        finish();




    }

    private void showHideListView() {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listviewLocations.getLayoutParams();
        if (mIsViewHidden) {
            txvShowLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_hide, 0, 0, 0);
            txvShowLocation.setText(getString(R.string.hide_locations));
            // params.height = (int) (250 * density);
            end = 250;
            start = 0;
        } else {
            txvShowLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_show, 0, 0, 0);
            txvShowLocation.setText(getString(R.string.show_locations));
            // params.height = (int) (0 * density);
            end = 0;
            start = 250;
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (start != end) {
                        if (mIsViewHidden) {
                            start = start + 5;
                        } else {
                            start = start - 5;
                        }
                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                params.height = (int) (start * density);
                                listviewLocations.setLayoutParams(params);
                            }
                        });
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (start == end) {
                        mIsViewHidden = !mIsViewHidden;
                    }
                }
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtLocationSearch.addTextChangedListener(mTextWatcher);
     /*   LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBroadcast, new IntentFilter(AppConstants.LOCAL_BRAODCAST_LOCATION));*/

   /*     if (!isInitialized && !StringUtils.isNullOrEmpty(SharedPrefsHelper.getInstance().getLatitude())) {
            mCurrentLatitude = Double.parseDouble(ChatPrefrence.getInstance(this).getLatitude());
            mCurrentLongitude = Double.parseDouble(ChatPrefrence.getInstance(this).getLongitude());
            mLocality = getLocality(new LatLng(mCurrentLatitude, mCurrentLatitude), this);
            hitApiRequest(ChatConstants.ApiConstants.REQUEST_FOURSQUARE_NEARBY_PLACES);

            isInitialized = true;
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcast);*/
        edtLocationSearch.removeTextChangedListener(mTextWatcher);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        // Showing the current location in Google Map
        mGoogleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
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

    /**
     * To know the location of the current marker, if searched from LatLng
     *
     * @param location
     * @param context
     * @return
     */
    public String getLocality(LatLng location, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            StringBuilder builder = new StringBuilder();
            if (addresses != null && addresses.size() > 0) {
                int addressLineCount = addresses.get(0).getMaxAddressLineIndex();
                for (int i = 0; i < addressLineCount; i++) {
                    builder.append(addresses.get(0).getAddressLine(i));
                    if (i != (addressLineCount - 1)) {
                        builder.append(", ");
                    }
                }
                return builder.toString();
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void hitApiRequest(int reqType) {
        String url;
   /*     Class className;
        switch (reqType) {
           //
            case 0:
                StringBuilder builder = new StringBuilder();
                builder.append(BASE_URL_FOURSQAURE);
                builder.append("?ll=").append(mCurrentLatitude + "," + mCurrentLongitude);
                builder.append("&client_id=").append(FOURSQUARE_CLIENT_ID);
                builder.append("&client_secret=").append(FOURSQUARE_CLIENT_SECRET);
                builder.append("&v=").append(timeMilisToString(System.currentTimeMillis()));
                if (!StringUtils.isNullOrEmpty(mQueryString)) {
                    builder.append("&query=").append(mQueryString);
                }
                url = builder.toString();
                className = FourSquareNearbyPlaceResponseModel.class;
                break;
            default:
                url = "";
                className = null;
                break;
        }
*/
        url = "https://api.foursquare.com/v2/venues/search?ll=" + 22.5688291 + "," + 88.4317944 + "&client_id=YKSFTFURQKGZUFYEFGUDXHRLUEGTLEOXMBYUTU3AYA4LLKX5&client_secret=BH3IPIEM33UPG14QTDVCICC142Q3M34TN0EVT55NNBOZY4GX&v=" + timeMilisToString(System.currentTimeMillis()) + "&query=" + mQueryString;
        // showProgressDialog(R.string.load);
        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, url,
                454, this, parms, false, false, Agent_Array_Object);


    }

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
        try {
            if (requestCode == 454) {
                //    hideProgressDialog();
                Object o = response;
                Log.e("mVenuesList-->", response.toString());
                JSONObject meta = response.getJSONObject("meta");
                JSONObject responseE = response.getJSONObject("response");
                JSONArray venues = responseE.getJSONArray("venues");
                mVenuesList.clear();
                if (meta.getString("code").equals("200")) {
                    for (int i = 0; i < venues.length(); i++) {
                        JSONObject V_Obj = venues.getJSONObject(i);
                        String lat = V_Obj.getJSONObject("location").getString("lat");
                        String lng = V_Obj.getJSONObject("location").getString("lng");
                        String distance = V_Obj.getJSONObject("location").getString("distance");
                        Location_Venues L = new Location_Venues(V_Obj.getString("name"), lat, lng, distance);
                        mVenuesList.add(L);
                    }
                    adapter.setListData(mVenuesList);
                    adapter.notifyDataSetChanged();

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

}
