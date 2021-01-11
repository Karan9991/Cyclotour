package com.example.admincyclotourhub.adminusers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admincyclotourhub.MainActivity;
import com.example.admincyclotourhub.R;
import com.example.admincyclotourhub.model.Tracks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveUsers extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListNombres;
    private HashMap<String, UsersContacto> listaContactos;
    private int lastExpandedPosition = -1;
    String noOfTracks, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_users);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Active Users");

        gettingToken();

        this.expandableListView = findViewById(R.id.expandableListView);

    }

    private HashMap<String, UsersContacto> getContactos() {
        HashMap<String, UsersContacto> listaC = new HashMap<>();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Users...");
        progressDialog.show();
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.Forutube.com/public/api/admin/home",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        // progressBar.setVisibility(View.INVISIBLE);
                        progressDialog.dismiss();
                        Log.i("Usersssssss","W response: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");
                            // if (status.equals("success")){
                            JSONObject jsondata = jsonObject.getJSONObject("data");
                            JSONArray tracks = (JSONArray)jsondata.get("tracks");
                            Log.i("Tracksss","W response: "+tracks);

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
                                //tracksList.add(users);
                                Log.i("i valiuvvvv","W "+i);
                                noOfTracks = String.valueOf(i+1);
                            }

                            JSONArray array = (JSONArray)jsondata.get("users");
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject childObject = array.getJSONObject(i);
                                String usernamee = childObject.getString("username");
                                Log.i("usersn","W "+usernamee);
                                if (childObject.getString("email_verified").equals("1")) {
                                    listaC.put(childObject.getString("username"), new UsersContacto(childObject.getString("email"),
                                            noOfTracks, childObject.getString("email_verified"), R.drawable.img_11, childObject.getString("id")));

                                    expandableListNombres = new ArrayList<>(listaContactos.keySet());
                                    expandableListAdapter = new ActiveUsersAdapter(getApplicationContext(),
                                            expandableListNombres, listaContactos);
                                    expandableListView.setAdapter(expandableListAdapter);

                                    expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                        @Override
                                        public void onGroupExpand(int groupPosition) {
                                            if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                                                expandableListView.collapseGroup(lastExpandedPosition);
                                            }
                                            lastExpandedPosition = groupPosition;
                                        }
                                    });
                                }
                            }
                            progressDialog.dismiss();

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

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(ActiveUsers.this, MainActivity.class));
        return super.onSupportNavigateUp();
    }

    private void gettingToken() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Users...");
        progressDialog.show();
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/admin/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (status.equals("200")){
                                token = data.getString("token");
                                listaContactos = getContactos();
                            }else{
                                Toast.makeText(getApplicationContext(), " Failed"+data.getString("token"),Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //displaying the error in toast if occurrs
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
}