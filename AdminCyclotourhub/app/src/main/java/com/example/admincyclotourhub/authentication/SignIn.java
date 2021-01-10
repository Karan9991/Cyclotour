package com.example.admincyclotourhub.authentication;

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

//import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admincyclotourhub.MainActivity;
import com.example.admincyclotourhub.R;
import com.example.admincyclotourhub.Validate;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity implements Validate {

    EditText etEmail, etPass;
    Button signnin;
    Boolean isValid;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkAdminLogin()){
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finish();
        }else {

            setContentView(R.layout.activity_sign_in);

            isValid = false;

            etEmail = findViewById(R.id.etEmailSignin);
            etPass = findViewById(R.id.etPassSignin);
            signnin = findViewById(R.id.btnsignin);

            signnin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validations()){
                        signingIn();
                    }
                }
            });
        }

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

    private void signingIn() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In...");
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
                                 saveCredential();
                                  saveToken(token);
                                 Intent intent = new Intent(SignIn.this, MainActivity.class);
                                 startActivity(intent);
                                  finish();
                             }else{
                                 Toast.makeText(getApplicationContext(), "Login Failed",Toast.LENGTH_SHORT).show();
                             }
                       progressDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Login Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login Failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("email",etEmail.getText().toString());
                param.put("password",etPass.getText().toString());
               param.put("device_name","admins laravel phone");
                return param;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putString("token", token);
        myEdit.commit();
    }

    private void saveCredential(){
        SharedPreferences sharedPreferences = getSharedPreferences("admin", MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putBoolean("login", true);
        myEdit.commit();
    }

    private boolean checkAdminLogin(){
        SharedPreferences sh = getSharedPreferences("admin", MODE_PRIVATE);
        boolean login = sh.getBoolean("login", false);
        return login;
    }

}