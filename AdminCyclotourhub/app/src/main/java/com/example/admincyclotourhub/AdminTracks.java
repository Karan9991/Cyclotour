package com.example.admincyclotourhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


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
import com.example.admincyclotourhub.adminusers.UsersContacto;
import com.example.admincyclotourhub.adminusers.UsersCusExpAdapter;
import com.example.admincyclotourhub.model.Tracks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminTracks extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListNombres;
    private HashMap<String, Contacto> listaContactos;
    private int lastExpandedPosition = -1;

    Button btnAddnewtrack;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tracks);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Tracks");

        gettingToken();

        btnAddnewtrack = findViewById(R.id.btnAddnewtrack);

        this.expandableListView = findViewById(R.id.expandableListView);

        btnAddnewtrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminTracks.this, AddNewTrack.class));
            }
        });
    }

private HashMap<String, Contacto> getContactos() {
    HashMap<String, Contacto> listaC = new HashMap<>();

    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Loading...");
    progressDialog.show();

    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.Forutube.com/public/api/admin/home",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.i("tracks","W response: "+response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("message");
                        // if (status.equals("success")){
                        JSONObject jsondata = jsonObject.getJSONObject("data");
                        JSONArray tracks = (JSONArray)jsondata.get("tracks");
                        for (int i = 0; i < tracks.length(); i++) {
                            JSONObject childObject = tracks.getJSONObject(i);
                            String id = childObject.getString("name");
                            Tracks users = new Tracks();
                            users.setId(childObject.getString("id"));
                            users.setName(childObject.getString("name"));
                            users.setStarting_lat(childObject.getString("starting_lat"));
                            users.setStarting_lng(childObject.getString("starting_lng"));
                            users.setFrom(childObject.getString("from"));
                            users.setEnding_lat(childObject.getString("ending_lat"));
                            users.setEnding_lng(childObject.getString("ending_lng"));
                            users.setDistance(childObject.getString("distance"));
                            users.setKml_file(childObject.getString("kml_file"));
                            users.setStatus(childObject.getString("status"));
                            users.setCreated_at(childObject.getString("created_at"));
                            users.setUpdated_at(childObject.getString("updated_at"));
                            Log.i("i valiuvvvv","W "+i);

                            listaC.put(childObject.getString("name"), new Contacto(childObject.getString("from")+
                                    "  -   "+childObject.getString("to"),
                                    childObject.getString("distance"), childObject.getString("id"), R.drawable.img_11,childObject.getString("to"),childObject.getString("from")));
                            expandableListNombres = new ArrayList<>(listaContactos.keySet());
                            expandableListAdapter = new CustomExpandableListAdapter(getApplicationContext(),
                                    expandableListNombres, listaContactos);
                            expandableListView.setAdapter(expandableListAdapter);

                            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                @Override
                                public void onGroupExpand(int groupPosition) {
                                    if(lastExpandedPosition != -1 && groupPosition != lastExpandedPosition){
                                        expandableListView.collapseGroup(lastExpandedPosition);
                                    }
                                    lastExpandedPosition = groupPosition;
                                }
                            });
                        }
                        JSONArray array = (JSONArray)jsondata.get("users");
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject childObject = array.getJSONObject(i);
                            String usernamee = childObject.getString("username");
                            Log.i("usersn","W "+usernamee);

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
                    Toast.makeText(getApplicationContext(), "n "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    return listaC;
}
    private void gettingToken() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/admin/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        // progressBar.setVisibility(View.INVISIBLE);
                        progressDialog.dismiss();
                        Log.i("rrrrrrrrr","W response: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (status.equals("200")){
                                token = data.getString("token");
                                saveToken(token);
                           listaContactos = getContactos();

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
                        //displaying the error in toast if occurrs
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
                return param;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onSupportNavigateUp() {

        startActivity(new Intent(AdminTracks.this, MainActivity.class));

        return super.onSupportNavigateUp();
    }
    private void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putString("token", token);
        myEdit.commit();
    }
}