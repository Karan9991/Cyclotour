package com.example.cyclotourhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.widget.ImageView;
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
import com.example.cyclotourhub.model.User;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.overlay.FolderOverlay;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements Validate, GoogleApiClient.OnConnectionFailedListener{

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

    EditText etusername, etEmail, etPass, etName;
    TextView tvsignin, tvSignUpVerify, tvSignupResendEmail;
    Boolean isValid;
    ImageView googlesignin, fbsignin;
    Button signup;

    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etusername = findViewById(R.id.etUsernamesignup);
        etName = findViewById(R.id.etNamesignup);
        etEmail = findViewById(R.id.etEmailSignup);
        etPass = findViewById(R.id.etPasssignup);
        googlesignin = findViewById(R.id.imggooglesignin);
        fbsignin = findViewById(R.id.imgfbsignin);
        signup = findViewById(R.id.btnSignup);
        tvsignin = findViewById(R.id.tvsignin);
        tvSignUpVerify = findViewById(R.id.tvSignupVerify);
        tvSignupResendEmail = findViewById(R.id.tvSignUpResendEmail);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn){
            startActivity(new Intent(SignUp.this, CyclingTracks.class));
            finish();
        }

        if (checkUserLogin()){
            startActivity(new Intent(SignUp.this, CyclingTracks.class));
            finish();
        }

        isValid = false;

        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });

        tvSignUpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, VerifyAccount.class));
            }
        });

        tvSignupResendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, ResendVerification.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validations()){
singin();
                }
            }
        });

        //fb login
        fbsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, FacebookLogin.class));
            }
        });


        //google login
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            startActivity(new Intent(SignUp.this, CyclingTracks.class));
            finish();
        }

        googlesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
    }

    @Override
    public boolean validations() {
        if (TextUtils.isEmpty(etusername.getText())) {
            etusername.setError("Username is required!");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etEmail.getText())) {
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
        else {
            isValid = true;
        }
        return isValid;
    }

    //google login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            gotoProfile();

        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }

    private void gotoProfile(){
        Intent intent=new Intent(SignUp.this,CyclingTracks.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void singin() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.Forutube.com/public/api/users/signup",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");
                            Log.i("response",""+response);
                            if (status.equals("200")){
                                Toast.makeText(getApplicationContext(), " "+msg, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUp.this, VerifyAccount.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "SignUp Failed "+msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "n "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", etName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("password", etPass.getText().toString());
                params.put("username", etusername.getText().toString());
                Log.i("ffffff"," "+params);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private boolean checkUserLogin(){
        SharedPreferences sh = getSharedPreferences("user", MODE_PRIVATE);
        boolean login = sh.getBoolean("login", false);
        return login;
    }
}