package com.example.admincyclotourhub;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admincyclotourhub.authentication.SignIn;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateTrack extends AppCompatActivity implements Validate{
    EditText etTO, etFrom,etDistance;
    Button update;
    Boolean isValid;
    String to, from, distance, id;
    double slat,slng,elat,elng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_track);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update");

        isValid = false;

        etTO = findViewById(R.id.etUpdateTo);
        etFrom = findViewById(R.id.etUpdateFrom);
        etDistance = findViewById(R.id.etUpdateDistance);
        update = findViewById(R.id.btnUpdateTrack);

        Intent bb = getIntent();
        if (bb!=null)
        {
            id = bb.getStringExtra("id");
            to = bb.getStringExtra("to");
            from = bb.getStringExtra("from");
            distance = bb.getStringExtra("distance");
            Log.i("dissssss"," "+distance+id+to+from);
            etTO.setText(from);
            etFrom.setText(to);
            etDistance.setText(distance);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validations()){
                    updateTrack();
                }
            }
        });
    }

    private void updateTrack() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Track...");
        progressDialog.show();

        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(UpdateTrack.this);
        try
        {
            addressList = geocoder.getFromLocationName(etTO.getText().toString(),1);
            Address locationn = addressList.get(0);
            slat = locationn.getLatitude();
            slng = locationn.getLongitude();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        List<Address> addressList2 = null;
        Geocoder geocoder2 = new Geocoder(UpdateTrack.this);
        try
        {
            addressList2 = geocoder2.getFromLocationName(etFrom.getText().toString(),1);
            Address locationn2 = addressList2.get(0);
            elat = locationn2.getLatitude();
            elng = locationn2.getLongitude();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try {

            JSONObject jsonObject = new JSONObject("{" +
                    "\"to\":\"" + etTO.getText().toString() + "\"," +
                    "\"from\":\"" + etFrom.getText().toString() + "\"," +
                    "\"starting_lat\":\"" + slat + "\"," +
                    "\"starting_lng\":\"" + slng + "\"," +
                    "\"ending_lat\":\"" + elat + "\"," +
                    "\"ending_lng\":\"" + elng + "\"," +

                    "\"distance\":\"" + Double.valueOf(etDistance.getText().toString()) + "\"}");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,"https://www.Forutube.com/public/api/admin/tracks/update/"+id, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Track Successfully Updated", Toast.LENGTH_LONG).show();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Update Operation Failed", Toast.LENGTH_LONG).show();
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            progressDialog.dismiss();
            Log.i("excep0000  ",e.getMessage());

        }
    }

    private String getToken(){
        SharedPreferences sh = getSharedPreferences("token", MODE_PRIVATE);
        String login = sh.getString("token", null);
        return login;
    }

    @Override
    public boolean validations() {
        if (TextUtils.isEmpty(etTO.getText())) {
            etTO.setError("Source is required!");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etFrom.getText())) {
            etFrom.setError("Destination is required!");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etDistance.getText())) {
            etDistance.setError("Distance is required!");
            isValid = false;
        }
        else {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public boolean onSupportNavigateUp() {

        startActivity(new Intent(UpdateTrack.this, AdminTracks.class));

        return super.onSupportNavigateUp();
    }
}