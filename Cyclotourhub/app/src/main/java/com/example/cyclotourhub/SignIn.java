package com.example.cyclotourhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity implements Validate{

    EditText etEmail, etPass;
    TextView tvsignup, tvforgotpass;
    Button signnin;
    Boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        isValid = false;

        etEmail = findViewById(R.id.etEmailSignin);
        etPass = findViewById(R.id.etPassSignin);
        signnin = findViewById(R.id.btnsignin);
        tvsignup = findViewById(R.id.tvsignup);
        tvforgotpass = findViewById(R.id.tvforgotpass);

        if (checkUserLogin()){
            startActivity(new Intent(SignIn.this, CyclingTracks.class));
            finish();
        }

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn){
            startActivity(new Intent(SignIn.this, CyclingTracks.class));
            finish();
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            startActivity(new Intent(SignIn.this, CyclingTracks.class));
            finish();
        }

        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });

        tvforgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, RequestResetPass.class));
            }
        });

        signnin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (validations()){
                        singin();
                }
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
        else {
            isValid = true;
        }
        return isValid;
    }

    private void singin() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/users/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i("response",": "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");
                            Toast.makeText(getApplicationContext(), " "+msg, Toast.LENGTH_LONG).show();

                            if (status.equals("200")){
                                Toast.makeText(getApplicationContext(), " "+msg, Toast.LENGTH_LONG).show();
                                saveCredential(data.getString("token"));
                                Intent intent = new Intent(SignIn.this, CyclingTracks.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "SignIn Failed "+msg, Toast.LENGTH_LONG).show();
                            }
                            // progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "SignIn Failed: Incorrect Credentials or User doesn't exist", Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "SignIn Failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", etEmail.getText().toString());
                params.put("password", etPass.getText().toString());
                params.put("device_name", "pixel 4");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);
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