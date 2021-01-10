package com.example.admincyclotourhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admincyclotourhub.adminusers.ActiveUsers;
import com.example.admincyclotourhub.authentication.SignIn;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    TextView tvhamburgerName,tvhamburgerEmail, tvAddTracks, tvViewTracks, tvRegisteredUsers, tvActiveUsers, tvInActiveUSers;

    List<AdminCardModel> myList;
    AdminCardsAdapter adapter;
    AdminCardModel cardModel;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListNombres;
    private HashMap<String, Contacto> listaContactos;
    private int lastExpandedPosition = -1;
    AlertDialog.Builder builder;

    private List<Users> usersList;
    private RecyclerView.Adapter useradapter;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAddTracks = findViewById(R.id.tvAddTracks);
        tvViewTracks = findViewById(R.id.tvViewTracks);
        tvRegisteredUsers = findViewById(R.id.tvRegisteredUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvInActiveUSers = findViewById(R.id.tvDeActiveUsers);

        builder = new AlertDialog.Builder(this);

        token = getToken();
                //gettingToken();
        BottomNavigationView navView = findViewById(R.id.adminnav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.adminnav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        dl = (DrawerLayout)findViewById(R.id.adminactivity_mainseller);
        t = new ActionBarDrawerToggle(this, dl,R.string.app_name, R.string.nav_app_bar_open_drawer_description);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cycling Tracks");
        getSupportActionBar().setSubtitle("Select your track");

        nv = (NavigationView)findViewById(R.id.adminnvseller);
        nv.setItemIconTintList(null);
        View hView =  nv.getHeaderView(0);
        tvhamburgerName = (TextView)hView.findViewById(R.id.tvhamburgersellername);
        tvhamburgerEmail = (TextView)hView.findViewById(R.id.tvhamburgerselleremail);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)

                {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), AddNewTrack.class));
                        break;
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), AdminTracks.class));
                        break;
                        case R.id.navigation_notifications:
                    startActivity(new Intent(getApplicationContext(), com.example.admincyclotourhub.adminusers.Users.class));
                    break;
                    default:
                        return true;
                }

                return true;
            }
        });

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id)
                {
                    case R.id.sellerLogout:
                        loadHeoList();
                        break;
                    default:
                        return true;
                }

                return true;

            }
        });

        tvAddTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminTracks.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        tvRegisteredUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.admincyclotourhub.adminusers.Users.class);
                intent.putExtra("token", token);
                startActivity(intent);            }
        });

        tvActiveUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.admincyclotourhub.adminusers.ActiveUsers.class);
                intent.putExtra("token", token);
                startActivity(intent);            }
        });

        tvInActiveUSers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.admincyclotourhub.adminusers.InActiveUsers.class);
                intent.putExtra("token", token);
                startActivity(intent);            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences("admin", MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putBoolean("login", false);
        myEdit.commit();
    }

    private void loadHeoList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loging Out...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/admin/logout",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("200")){
                                logout();
                                startActivity(new Intent(MainActivity.this, SignIn.class));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Logout Failed",Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Lgout Failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void gettingToken() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
            progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/admin/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (status.equals("200")){
                                token = data.getString("token");
                                Log.i("tokeeeeeeeennnnn"," "+token);
                            }else{
                                Toast.makeText(getApplicationContext(), " Failed"+data.getString("token"),Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), " Failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("email","admin@gmail.com");
                param.put("password","password");
                param.put("device_name","admins laravel phone");
                Log.d("tagbn", param.toString());
                return param;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private String getToken(){
        SharedPreferences sh = getSharedPreferences("token", MODE_PRIVATE);
        String login = sh.getString("token", null);
        return login;
    }

}