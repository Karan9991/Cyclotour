package com.example.cyclotourhub;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class ResendVerification extends AppCompatActivity implements Validate {
    EditText etEmail;
    Button btnVerify;
    TextView tv;
    boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_verification);

        etEmail = findViewById(R.id.etEmailResend);
        btnVerify = findViewById(R.id.btnResendVeri);
        tv = findViewById(R.id.tvsignResend);

        isValid = false;

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validations()) {
                    sendVerification();
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResendVerification.this, SignIn.class));
            }
        });
    }
    private void sendVerification() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resending Verification ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/users/resend-verification-mail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");

                            if (status.equals("201")){
                                Toast.makeText(getApplicationContext(), " "+msg, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResendVerification.this, VerifyAccount.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Failed "+msg, Toast.LENGTH_LONG).show();
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
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Failed  "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", etEmail.getText().toString());
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
        else {
            isValid = true;
        }
        return isValid;
    }
}