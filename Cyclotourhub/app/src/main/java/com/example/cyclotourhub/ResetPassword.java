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
import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity implements Validate{
    EditText etEmail, etToken, etPass;
    Button btnVerify;
    TextView tv;
    String deviceName;
    boolean isValid;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        etEmail = findViewById(R.id.etEmaild);
        etPass = findViewById(R.id.etPassd);
        etToken = findViewById(R.id.etTokend);
        btnVerify = findViewById(R.id.btnChangePas);
        tv = findViewById(R.id.tvResetPassd);

        isValid = false;

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
                startActivity(new Intent(ResetPassword.this, SignIn.class));
            }
        });
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
        else if (TextUtils.isEmpty(etPass.getText())) {
            etPass.setError("Password is required!");
            isValid = false;
        }
        else if(!PASSWORD_PATTERN.matcher(etPass.getText().toString().trim()).matches()){
            etPass.setError( "Password must between 8 and 20 characters; must contain at least one lowercase letter, one uppercase letter, one numeric digit, and one special character, but cannot contain whitespace" );
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
    private void sendVerification() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Changing Password ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/users/reset-password",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i("rrrrrrrrr", "W response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");

                            if (status.equals("200")) {
                                Toast.makeText(getApplicationContext(), " " + msg, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed " + msg, Toast.LENGTH_LONG).show();
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

                        Toast.makeText(getApplicationContext(), "Failed  " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", etEmail.getText().toString());
                params.put("password", etPass.getText().toString());
                params.put("token", etToken.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}