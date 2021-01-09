package com.example.cyclotourhub;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cyclotourhub.model.AddTracks;
import com.example.cyclotourhub.model.CardModel;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CyclingTracks extends AppCompatActivity{
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    TextView tvhamburgerName,tvhamburgerEmail;

    List<CardModel> myList;
    CardsAdapter adapter;
    CardModel cardModel;
   public static String status, message;
   public static double slat,slng, elat, elng;

    AlertDialog.Builder builder;
    GoogleApiClient googleApiClient;
    GoogleSignInClient googleSignInClient;

     ProgressDialog progressDialog;
     String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);

        setContentView(R.layout.activity_cycling_tracks);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        builder = new AlertDialog.Builder(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        dl = (DrawerLayout)findViewById(R.id.activity_mainseller);
        t = new ActionBarDrawerToggle(this, dl,R.string.app_name, R.string.nav_app_bar_open_drawer_description);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cycling Tracks");
        getSupportActionBar().setSubtitle("Select your track");

        nv = (NavigationView)findViewById(R.id.nvseller);
        nv.setItemIconTintList(null);
        View hView =  nv.getHeaderView(0);
        tvhamburgerName = (TextView)hView.findViewById(R.id.tvhamburgersellername);
        tvhamburgerEmail = (TextView)hView.findViewById(R.id.tvhamburgerselleremail);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id)
                {
                    case R.id.sellerChat:
                        startActivity(new Intent(CyclingTracks.this, RequestResetPass.class));
                        break;
                    case R.id.sellerLogout:
                        logout();
                        break;
                    default:
                        return true;
                }

                return true;

            }
        });

        //listview
        myList = new ArrayList<CardModel>();

        ListView lvCards = (ListView) findViewById(R.id.list_cards);

        lvCards.setSaveEnabled(true);

        adapter = new CardsAdapter(getApplicationContext(),R.layout.customerhelper, myList);
        lvCards.setAdapter(adapter);

            SharedPreferences sh = getSharedPreferences("user", MODE_PRIVATE);
             token = sh.getString("token", null);

           getTracks();


        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressDialog.setMessage("Loading ...");
                progressDialog.show();

                new Thread( new Runnable() { @Override public void run() {
                    String name = ((TextView) view.findViewById(R.id.tvName)).getText().toString();
                    String addr = ((TextView) view.findViewById(R.id.tvAddress)).getText().toString();
                    String trackid = ((TextView) view.findViewById(R.id.tvtrackid)).getText().toString();
                    String trackname = ((TextView) view.findViewById(R.id.tvtrackname)).getText().toString();
                    Geocoder coder = new Geocoder(getApplicationContext());
                    List<Address> address;
                    //GeoPoint p1 = null;
                   Log.i("track iddddddddd "," "+trackid);
                    try {
                        address = coder.getFromLocationName(name,5);
                        Address location=address.get(0);
                        slat = location.getLatitude();
                        slng =  location.getLongitude();
                        Log.i("cccccc"," "+slat);
                    }catch (Exception e){
                    }

                    //
                    Geocoder coder2 = new Geocoder(getApplicationContext());
                    List<Address> address2;
                    //GeoPoint p1 = null;

                    try {
                        address2 = coder2.getFromLocationName(addr,5);
                        Address location=address2.get(0);
                        elat = location.getLatitude();
                        elng =  location.getLongitude();
                        Log.i("ddddd"," "+new DecimalFormat("##.##").format(findDistance())+" Kms");
                    }catch (Exception e){

                    }
                    Intent intent = new Intent(CyclingTracks.this, MapsActivity.class);
                    intent.putExtra("trackid", trackid);
                    intent.putExtra("trackname", trackname);
                    intent.putExtra("name", name);
                    intent.putExtra("address", addr);
                    intent.putExtra("slat", slat);
                    intent.putExtra("slng", slng);
                    intent.putExtra("elat", elat);
                    intent.putExtra("elng", elng);
                    intent.putExtra("distance", findDistance());
                    startActivity(intent);
                } } ).start();

            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private double findDistance(){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(slat);
        startPoint.setLongitude(slng);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(elat);
        endPoint.setLongitude(elng);

        double distance=startPoint.distanceTo(endPoint)/1000;
        return distance;
    }

    private void logout(){
        builder.setTitle("Logout");
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      //  FirebaseAuth.getInstance().signOut();
                        googleLogout();
                        facebookLogout();
                        simpleLogout();
                        Intent i = new Intent(CyclingTracks.this, SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void googleLogout() {
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

       googleSignInClient = GoogleSignIn.getClient(this, gso);

       googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

           }
       });
    }

    private void facebookLogout() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();
                Intent i = new Intent(CyclingTracks.this, SignIn.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        }).executeAsync();
    }
    private void getTracks() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Tracks...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.Forutube.com/public/api/users/tracks",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");
                            if (status.equals("200")){
                                Toast.makeText(getApplicationContext(), " "+msg, Toast.LENGTH_LONG).show();
                                JSONArray array = (JSONArray)jsonObject.get("data");
                                for (int i = 0; i < array.length(); i++) {

                                     JSONObject childObject = array.getJSONObject(i);
                                    adapter.add(new CardModel(childObject.getString("id"),"track1", childObject.getString("from"), childObject.getString("to")));
                                    Log.i("usersnl","W "+childObject.getString("to"));
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), " Failed "+msg, Toast.LENGTH_LONG).show();
                            }
                            // progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), " Failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);
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

    private void simpleLogout(){
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putBoolean("login", false);
        myEdit.commit();
    }

}