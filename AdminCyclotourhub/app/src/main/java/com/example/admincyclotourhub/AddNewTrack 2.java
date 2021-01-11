package com.example.admincyclotourhub;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

//import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
//import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.cache.DiskBasedCache;
import com.android.volley.cache.NoCache;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.gson.JsonObject;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlLineString;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.maps.android.data.kml.KmlPolygon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
//import org.osmdroid.bonuspack.kml.KmlGeometry;
//import org.osmdroid.bonuspack.kml.KmlPlacemark;
//import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlMultiGeometry;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.w3c.dom.Document;
//import org.w3c.dom.Element;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AddNewTrack extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    SearchView svsource, svdest, svend;
    Button btnuploadkml, btnclearall, btnadd;
    private Uri fileUri;
    private String filePath;
    public static final int PICKFILE_RESULT_CODE = 1;
    String[] addr = new String[2];
    List<String> latitu = new ArrayList<String>();
    List<String> longitu = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_track);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

                if(checkPermission(AddNewTrack.this)) {
            requestPermission(AddNewTrack.this,1);
        }

        svsource = findViewById(R.id.svsource);
        svdest = findViewById(R.id.svdest);
        svend = findViewById(R.id.svend);
        btnuploadkml = findViewById(R.id.btnuploadkml);
        btnclearall = findViewById(R.id.btnclearall);
        btnadd = findViewById(R.id.btnadd);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Track");

        //searchview customization
        svsource.setQueryHint(Html.fromHtml("<font color = #c0c0c0>" + "Search source location" + "</font>"));

        EditText searchEditText = (EditText) svsource.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.tab_indicator_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.tab_indicator_gray));

        ImageView icon = svsource.findViewById(androidx.appcompat.R.id.search_button);
        icon.setColorFilter(getResources().getColor(R.color.tab_indicator_gray));

        svdest.setQueryHint(Html.fromHtml("<font color = #c0c0c0>" + "Search destination location" + "</font>"));

        EditText searchEditText2 = (EditText) svdest.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText2.setTextColor(getResources().getColor(R.color.tab_indicator_gray));
        searchEditText2.setHintTextColor(getResources().getColor(R.color.tab_indicator_gray));

        ImageView icon2 = svsource.findViewById(androidx.appcompat.R.id.search_button);
        icon2.setColorFilter(getResources().getColor(R.color.tab_indicator_gray));

        svend.setQueryHint(Html.fromHtml("<font color = #c0c0c0>" + "Search end location" + "</font>"));

        EditText searchEditText3 = (EditText) svend.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText3.setTextColor(getResources().getColor(R.color.tab_indicator_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.tab_indicator_gray));

        ImageView icon3 = svend.findViewById(androidx.appcompat.R.id.search_button);
        icon3.setColorFilter(getResources().getColor(R.color.tab_indicator_gray));

        //searchview typing
        svsource.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String location = svsource.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(AddNewTrack.this);
                    try
                    {
                        addressList = geocoder.getFromLocationName(location,1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        svdest.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String location = svdest.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(AddNewTrack.this);
                    try
                    {
                        addressList = geocoder.getFromLocationName(location,1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        svend.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String location = svend.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(AddNewTrack.this);
                    try
                    {
                        addressList = geocoder.getFromLocationName(location,1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });
//Upload kml
        btnuploadkml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });
//Clear All fields
       btnclearall.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               svsource.setQuery("", false);
               svsource.clearFocus();
               svdest.setQuery("", false);
               svdest.clearFocus();
               svend.setQuery("", false);
               svend.clearFocus();

                       }
       });
//Add Track Click
       btnadd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (filePath!=null) {
                   Log.i("fp ", " bv" + latitu);
                       if (!latitu.isEmpty()){
                           addTrack();
                       }
               }else {
                   Toast.makeText(getApplicationContext(),"Please Select KML file to Upload",Toast.LENGTH_LONG).show();
               }

           }
       });

    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(AddNewTrack.this, AdminTracks.class));
        return super.onSupportNavigateUp();
    }

    private double findDistance(){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(Double.valueOf(latitu.get(0)));
        startPoint.setLongitude(Double.valueOf(longitu.get(0)));

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(Double.valueOf(latitu.get(latitu.size()-1)));
        endPoint.setLongitude(Double.valueOf(longitu.get(longitu.size()-1)));
        double distance=startPoint.distanceTo(endPoint)/1000;
        return distance;
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

    private void addTrack() {
        double slt,slng,elt,elng,dis;
        slt = Double.valueOf(latitu.get(0));
        slng = Double.valueOf(longitu.get(0));
        elt = Double.valueOf(latitu.get(latitu.size()-1));
        elng = Double.valueOf(longitu.get(longitu.size()-1));
        dis = findDistance();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Track...");
        progressDialog.show();
       try {

           JSONObject jsonObject = new JSONObject("{" +
                   "\"to\":\"" + addr[0] + "\"," +
                   "\"from\":\"" + addr[addr.length-1] + "\"," +
                   "\"starting_lng\":\"" + slng + "\"," +
                   "\"starting_lat\":\"" + slt + "\"," +
                   "\"ending_lat\":\"" + elt + "\"," +
                   "\"ending_lng\":\"" + elng + "\"," +
                   "\"distance\":\"" + dis + "\"}");

           JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"https://www.Forutube.com/public/api/admin/tracks/create-track", jsonObject, new Response.Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject response) {
                   Log.i("rere",""+response);
                   progressDialog.dismiss();
                   Toast.makeText(getApplicationContext(),"Track Successfully Added", Toast.LENGTH_LONG).show();
               }
           }, new Response.ErrorListener() {

               @Override
               public void onErrorResponse(VolleyError error) {
                   progressDialog.dismiss();

                   Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_LONG).show();
               }
           })
           {
               @Override
               public Map<String, String> getHeaders() throws AuthFailureError {
                   HashMap<String, String> headers = new HashMap<>();
                   headers.put("Authorization", "Bearer " + getToken());
                   return headers;
               }

           };
           RequestQueue requestQueue = Volley.newRequestQueue(this);

           requestQueue.add(jsonObjectRequest);
       }catch (Exception e){
           progressDialog.dismiss();

       }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    filePath = FilePath.getFilePath(AddNewTrack.this, fileUri);
                    // new PaintBorder().execute();
                    if (filePath.substring(filePath.length() - 3).equals("kml")) {
                        try {
                            File myFile = new File(filePath);
                            FileInputStream fIn = new FileInputStream(myFile);
                            KmlLayer kmlLayer = new KmlLayer(mMap, fIn, getApplicationContext());
                            kmlLayer.addLayerToMap();
                            moveCameraToKml(kmlLayer, mMap);

                            ArrayList<LatLng> pathPoints = new ArrayList();

                            if (kmlLayer != null && kmlLayer.getContainers() != null) {
                                for (KmlContainer container : kmlLayer.getContainers()) {
                                    container = container.getContainers().iterator().next();
                                    if (container.hasPlacemarks()) {

                                        for (KmlPlacemark placemark : container.getPlacemarks()) {
                                            Geometry geometry = placemark.getGeometry();
                                            if (geometry.getGeometryType().equals("Point")) {

                                                KmlPoint point = (KmlPoint) placemark.getGeometry();
                                                LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                                                pathPoints.add(latLng);
                                                Log.i("e222222222", "xxxxx " + point.getGeometryObject().latitude);

                                                latitu.add(String.valueOf(point.getGeometryObject().latitude));
                                                longitu.add(String.valueOf(point.getGeometryObject().longitude));

                                            } else if (geometry.getGeometryType().equals("LineString")) {
                                                KmlLineString kmlLineString = (KmlLineString) geometry;
                                                ArrayList<LatLng> coords = kmlLineString.getGeometryObject();
                                                for (LatLng latLng : coords) {

                                                    pathPoints.add(latLng);
                                                }
                                            }
                                        }
                                    }
                                }

                                for (LatLng latLng : pathPoints) {
                                    mMap.addMarker(new MarkerOptions().position(latLng));
                                }
                            }

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                            Log.i("Error", " " + e.getMessage());
                        } catch (IOException e) {
                            Log.i("Error", " " + e.getMessage());

                            e.printStackTrace();
                        }

                        List<Address> addressList = null;
                        Geocoder geocoder = new Geocoder(AddNewTrack.this);
                        try {
                            addressList = geocoder.getFromLocation(Double.valueOf(latitu.get(0)), Double.valueOf(longitu.get(0)), 1);

                            String address = addressList.get(0).getAddressLine(0);
                            addr[0] = address;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        List<Address> addressList2 = null;
                        Geocoder geocoder2 = new Geocoder(AddNewTrack.this);
                        try {
                            addressList2 = geocoder2.getFromLocation(Double.valueOf(latitu.get(latitu.size() - 1)), Double.valueOf(longitu.get(longitu.size() - 1)), 1);

                            String address = addressList2.get(0).getAddressLine(0);
                            addr[1] = address;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"This File not Supported Please Select KML file",Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(AddNewTrack.this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, AddNewTrack.this, 1);
                if (errorDialog != null) errorDialog.show();
            } else
                Toast.makeText(this, "Google Play Services not found", Toast.LENGTH_SHORT).show();
        }
    }
    private String getToken(){
        SharedPreferences sh = getSharedPreferences("token", MODE_PRIVATE);
        String login = sh.getString("token", null);
        return login;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
//                .setListener(new WorkaroundMapFragment.OnTouchListener() {
//                    @Override
//                    public void onTouch()
//                    {
//                        mScrollView.requestDisallowInterceptTouchEvent(true);
//                    }
//                });
    }

    public static void moveCameraToKml(KmlLayer kmlLayer, GoogleMap googleMap) {
        //TODO fixed error with some kml file https://developers.google.com/maps/documentation/android-api/utility/kml
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        KmlContainer container = kmlLayer.getContainers().iterator().next();
        container = container.getContainers().iterator().next();

        for (KmlPlacemark placemark : container.getPlacemarks()) {
            Geometry geometry = placemark.getGeometry();
            if (geometry.getGeometryType().equals("Point")) {

                KmlPoint point = (KmlPoint) placemark.getGeometry();
                LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                builder.include(latLng);

            } else if (geometry.getGeometryType().equals("LineString")) {
                KmlLineString kmlLineString = (KmlLineString) geometry;
                ArrayList<LatLng> coords = kmlLineString.getGeometryObject();
            }
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
    }
}