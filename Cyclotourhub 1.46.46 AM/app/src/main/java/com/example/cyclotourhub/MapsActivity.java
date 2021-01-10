package com.example.cyclotourhub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cyclotourhub.drawRoute.FetchURL;
import com.example.cyclotourhub.drawRoute.GpsTracker;
import com.example.cyclotourhub.drawRoute.TaskLoadedCallback;
import com.example.cyclotourhub.fragment_dialog.BottomSheetFragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button bottomSheetsCoordinatorLayout, bottomSheetsDialogFragment;
    //
    BottomSheetBehavior mBottomSheetBehaviour;
    Button openBottomSheetButton;
    Button closeBottomSheetButton;
    View nestedScrollView;

    Button btn;
    private MarkerOptions place1, place2, place3, place4;
    private Polyline currentPolyline;
    TextView tvdistance, tvnameaddre;
    String name, status, message, createdat, updatedat;
    String address;
    String trackid;
    String trackname;
    double slat;
    double slng;
    double elat;
    double elng;
    double distance;
    int a, slt,sln,elt,eln;
    Date currentTime;

    MapView map;
    MyLocationNewOverlay mLocationOverlay;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

                if(checkPermission(MapsActivity.this)) {
            requestPermission(MapsActivity.this,1);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_maps);

        progressDialog = new ProgressDialog(this);

        Configuration.getInstance().setUserAgentValue("OBP_Tuto/1.0");

        //getting intent
        Intent bb = getIntent();
        if (bb!=null)
        {
            name = bb.getStringExtra("name");
            address = bb.getStringExtra("address");
            trackid = bb.getStringExtra("trackid");
            trackname = bb.getStringExtra("trackname");
            slat = bb.getDoubleExtra("slat", 0);
            slng = bb.getDoubleExtra("slng",0);
            elat = bb.getDoubleExtra("elat",0);
            elng = bb.getDoubleExtra("elng",0);
            distance = bb.getDoubleExtra("distance",0);
            Log.i("dissssss"," "+distance);
            Log.i("Intentttttttttttt"," "+slng);
            currentTime = Calendar.getInstance().getTime();
            createdat = currentTime.toString();
            updatedat = currentTime.toString();
        }

        //a
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

        //1. "Hello, Routing World"
        RoadManager roadManager = new OSRMRoadManager(this);

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

        btn = findViewById(R.id.btnstartcyc);
        tvdistance = findViewById(R.id.tvdistance);
        tvnameaddre = findViewById(R.id.tvnameaddre);


        a = (int)distance;
        sln = (int)slat;
        slt = (int)slng;
        elt = (int)elat;
        eln = (int)elng;

        tvdistance.setText(""+a+ " Kms");
        tvnameaddre.setText(name+" > "+address);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading ...");
                progressDialog.show();
                new Thread( new Runnable() { @Override public void run() {
                    Intent intent = new Intent(MapsActivity.this, StartCycling.class);
                    intent.putExtra("trackida", trackid);
                    intent.putExtra("tracknamea", trackname);
                    intent.putExtra("namea", name);
                    intent.putExtra("addressa", address);
                    intent.putExtra("slata", slat);
                    intent.putExtra("slnga", slng);
                    intent.putExtra("elata", elat);
                    intent.putExtra("elnga", elng);
                    intent.putExtra("distancea", distance);
                    startActivity(intent);
                } } ).start();

            }
        });
        bind();

        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        //Remove this line to disable peek
        mBottomSheetBehaviour.setPeekHeight(200);

        onOpenSheetClicked();
        onCloseSheetClicked();
        stateCallBacks();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
    }
    private void onOpenSheetClicked() {

    }

    private void onCloseSheetClicked() {

    }

    private void stateCallBacks() {
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                String state = "";

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        state = "DRAGGING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING: {
                        state = "SETTLING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        state = "EXPANDED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        state = "COLLAPSED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        state = "HIDDEN";
                        break;
                    }
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void bind() {

        nestedScrollView = findViewById(R.id.nestedScrollView);
    }

    @Override
    public boolean onSupportNavigateUp() {

        startActivity(new Intent(MapsActivity.this,CyclingTracks.class));

        return super.onSupportNavigateUp();
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
    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        progressDialog.dismiss();
    }
}