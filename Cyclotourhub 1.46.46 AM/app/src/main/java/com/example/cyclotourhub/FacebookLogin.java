package com.example.cyclotourhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.appevents.UserDataStore.EMAIL;
import static org.osmdroid.bonuspack.utils.BonusPackHelper.LOG_TAG;

public class FacebookLogin extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private static final String EMAIL = "email";


    String status, message, deviceName, updatedAt,email, firstname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Facebook Login");

        //uncomment below code after Facebook API integration

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_facebook_login);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFbInfo();
                        status = "404";
                        message = "Facebook signed in success";
                        deviceName = android.os.Build.MODEL;

                        startActivity(new Intent(FacebookLogin.this, CyclingTracks.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });

    }

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
}
    @Override
    public boolean onSupportNavigateUp() {

        startActivity(new Intent(FacebookLogin.this,SignUp.class));

        return super.onSupportNavigateUp();
    }

    private void getFbInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            Log.d(LOG_TAG, "fb json object: " + object);
                            Log.d(LOG_TAG, "fb graph response: " + response);

                            String id = object.getString("id");
                             firstname = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String gender = object.getString("gender");
                            String birthday = object.getString("birthday");
                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";


                            if (object.has("email")) {
                                email = object.getString("email");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }
}