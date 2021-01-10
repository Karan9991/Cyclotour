package com.example.cyclotourhub;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyAccount extends AppCompatActivity implements Validate{

    EditText etEmail, etToken;
    Button btnVerify;
    TextView tv;
    String deviceName;
    boolean isValid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        etEmail = findViewById(R.id.etEmailVerify);
        etToken = findViewById(R.id.etTokenVerify);
        btnVerify = findViewById(R.id.btnVerifyAccount);
        tv = findViewById(R.id.tvsigninverify);

        isValid = false;

        if (checkUserLogin()){
            startActivity(new Intent(VerifyAccount.this, CyclingTracks.class));
            finish();
        }

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceName = android.os.Build.MODEL;

                if (validations()){
                    sendVerification();
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(VerifyAccount.this, SignIn.class));
            }
        });
    }

    private void sendVerification() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying Account ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/users/verify-account",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                             JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                             String msg = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");
                             if (status.equals("200")){
                              Toast.makeText(getApplicationContext(), " "+msg, Toast.LENGTH_LONG).show();
                                 saveCredential(data.getString("token"));
                                 Intent intent = new Intent(VerifyAccount.this, CyclingTracks.class);
                                 intent.putExtra("token", data.getString("token"));
                                 startActivity(intent);
                                 finish();
                              }
                             else {
                               Toast.makeText(getApplicationContext(), "Failed "+msg, Toast.LENGTH_LONG).show();
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

                        Toast.makeText(getApplicationContext(), "Failed  "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", etEmail.getText().toString());
                params.put("token", etToken.getText().toString());
                params.put("device_name", deviceName);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean validations() {
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("E-Mail is required!");
            isValid = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etToken.getText())) {
            etToken.setError("Token is required!");
            isValid = false;
        }
        else {
            isValid = true;
        }
        return isValid;
    }
    private void saveCredential(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putBoolean("login", true);
        myEdit.putString("token", token);
        myEdit.commit();
    }
    private boolean checkUserLogin(){
        SharedPreferences sh = getSharedPreferences("user", MODE_PRIVATE);
        boolean login = sh.getBoolean("login", false);
        return login;
    }
}