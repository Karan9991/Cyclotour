package com.example.cyclotourhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cyclotourhub.drawRoute.FetchURL;
import com.example.cyclotourhub.drawRoute.GpsTracker;
import com.example.cyclotourhub.drawRoute.TaskLoadedCallback;
import com.example.cyclotourhub.model.CardModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartCycling extends AppCompatActivity implements View.OnClickListener {

    private GoogleMap mMap;
    MapView map;

    private MarkerOptions place1, place2, place3, place4;
    private Polyline currentPolyline;

    TextView tvspeed, tvkms;
    Button btnplay, btnpause, btnstop;
    GpsTracker gpsTracker;
    String name, startTime, endTime;
    String address;
    String trackid;
    String trackname, token;
    double slat;
    double slng;
    double elat;
    double elng;
    double distance;
    boolean play;
    Date currentTime;

    MyLocationNewOverlay mLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkPermission(StartCycling.this)) {
            requestPermission(StartCycling.this,1);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_start_cycling);

        //getting intent
        Intent bb = getIntent();
        if (bb!=null)
        {
            name = bb.getStringExtra("namea");
            address = bb.getStringExtra("addressa");
            trackid = bb.getStringExtra("trackida");
            trackname = bb.getStringExtra("tracknamea");
            slat = bb.getDoubleExtra("slata", 0);
            slng = bb.getDoubleExtra("slnga",0);
            elat = bb.getDoubleExtra("elata",0);
            elng = bb.getDoubleExtra("elnga",0);
            distance = bb.getDoubleExtra("distancea",0);
            Log.i("dissssss"," "+distance);
            Log.i("Intentttttttttttt"," "+slng);

        }
        SharedPreferences sh = getSharedPreferences("user", MODE_PRIVATE);
        token = sh.getString("token", null);

        map = (MapView) findViewById(R.id.map);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(slat, slng);
        IMapController mapController = map.getController();
        mapController.setZoom(10.0);
        mapController.setCenter(startPoint);

        //0. Using the Marker overlay
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Start point");
        startMarker.setDraggable(true);
        startMarker.setOnMarkerDragListener(new OnMarkerDragListenerDrawer());
        map.getOverlays().add(startMarker);
        RoadManager roadManager = new OSRMRoadManager(this);
        //2. Playing with the RoadManager
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(elat, elng);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        if (road.mStatus != Road.STATUS_OK)
            Toast.makeText(this, "Error when loading the road - status=" + road.mStatus, Toast.LENGTH_SHORT).show();

        org.osmdroid.views.overlay.Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);

        //3. Showing the Route steps on the map
        FolderOverlay roadMarkers = new FolderOverlay();
        map.getOverlays().add(roadMarkers);
        Drawable nodeIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
        for (int i = 0; i < road.mNodes.size(); i++) {
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

            //4. Filling the bubbles
            nodeMarker.setTitle("Step " + i);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
            Drawable iconContinue = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_continue, null);
            nodeMarker.setImage(iconContinue);
            //4. end
            roadMarkers.add(nodeMarker);
        }
        //show current location
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvspeed = findViewById(R.id.tvspeed);
        tvkms = findViewById(R.id.tvkms);
        btnplay = findViewById(R.id.btnplay);
        btnpause = findViewById(R.id.btnpause);
        btnstop = findViewById(R.id.btnstop);
        play = false;

        map.setBuiltInZoomControls(true);

        //find distance
        int a = (int)distance;
        tvkms.setText(a+" Kms");
        tvspeed.setText("0.0 Kms/Hour");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnplay:
                btnplay.setVisibility(Button.GONE);
                btnpause.setVisibility(Button.VISIBLE);
                play = true;

                if(checkPermission(StartCycling.this)) {
                    requestPermission(StartCycling.this,1);
                    requestLocationUpdates();
                    gpsTracker = new GpsTracker(getApplicationContext());
                    Log.i("aaaaaaaaaaa"," "+gpsTracker.latitude);

                }else{
                    Log.i("vvvvvvvvvv"," vv");

                }

                break;
            case R.id.btnpause:
                btnpause.setVisibility(Button.GONE);
                btnplay.setVisibility(Button.VISIBLE);
                play = false;
                break;

            case  R.id.btnstop:
                btnpause.setVisibility(Button.GONE);
                btnplay.setVisibility(Button.VISIBLE);
                currentTime = Calendar.getInstance().getTime();
                endTime = currentTime.toString();
                play = false;
                if(startTime != null && endTime != null){
                    sendDuration();
                }
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //4

    public void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = "location";

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null && play) {
                        currentTime = Calendar.getInstance().getTime();
                        startTime = currentTime.toString();
                        tvspeed.setText(new DecimalFormat("##.##").format(location.getSpeed())+" Kms/Hour");
                        Log.i("speed"," "+location.getSpeed());
                    }
                }
            }, null);
        }else {
        }
    }

    public boolean checkPermission(Activity activity){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    public void requestPermission(Activity activity, final int code){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)){

            Toast.makeText(activity,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},code);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        startActivity(new Intent(StartCycling.this,CyclingTracks.class));

        return super.onSupportNavigateUp();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(StartCycling.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    //b
    //0. Using the Marker and Polyline overlays - advanced options
    class OnMarkerDragListenerDrawer implements Marker.OnMarkerDragListener {
        ArrayList<GeoPoint> mTrace;
        org.osmdroid.views.overlay.Polyline mPolyline;

        OnMarkerDragListenerDrawer() {
            mTrace = new ArrayList<GeoPoint>(100);
            mPolyline = new org.osmdroid.views.overlay.Polyline();
            mPolyline.setColor(0xAA0000FF);
            mPolyline.setWidth(2.0f);
            mPolyline.setGeodesic(true);
            map.getOverlays().add(mPolyline);
        }

        @Override public void onMarkerDrag(Marker marker) {
            //mTrace.add(marker.getPosition());
        }

        @Override public void onMarkerDragEnd(Marker marker) {
            mTrace.add(marker.getPosition());
            mPolyline.setPoints(mTrace);
            map.invalidate();
        }

        @Override public void onMarkerDragStart(Marker marker) {
            //mTrace.add(marker.getPosition());
        }
    }
    private void sendDuration() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Completing Journey ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/users/tracks/"+trackid+"/duration",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i("rrrrrrrrr","W response: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");
                            if (status.equals("200")){
                                Toast.makeText(getApplicationContext(), "Journey completed ", Toast.LENGTH_LONG).show();

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Failed "+msg, Toast.LENGTH_LONG).show();
                            }
                             progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Failed sending "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("catch block "," "+e.getMessage());
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed  "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("start_time", startTime);
                params.put("end_time", endTime);
                params.put("track_id", trackid);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);
    }
}