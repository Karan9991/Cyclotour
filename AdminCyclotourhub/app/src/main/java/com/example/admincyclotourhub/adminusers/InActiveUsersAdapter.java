package com.example.admincyclotourhub.adminusers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admincyclotourhub.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class InActiveUsersAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listTitulo;
    private HashMap<String, UsersContacto> expandableListDetalles;
    String[] dura;
    String[] name;
    String test= "Duration"+"\n";
    String test2= "Tracks"+"\n";
    String test3="";

    public InActiveUsersAdapter(Context context,
                              List<String> listTitulo,
                              HashMap<String, UsersContacto> expandableListDetalles) {
        this.context = context;
        this.listTitulo = listTitulo;
        this.expandableListDetalles = expandableListDetalles;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final UsersContacto contacto = (UsersContacto) getChild(groupPosition, childPosition);

        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.admin_item_list, null);
        }
        TextView tvemail = convertView.findViewById(R.id.tvUseremail);
        TextView tvnooftracks = convertView.findViewById(R.id.tvusernooftracks);
        TextView tvDuration = convertView.findViewById(R.id.tvUserDuration);
        TextView tvTrack = convertView.findViewById(R.id.tvuserTrack);

        Log.i("idddddd"," "+contacto.getDireccion());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.Forutube.com/public/api/admin/users/"+contacto.getDireccion(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("durationnnn","W response: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsondata = jsonObject.getJSONObject("data");
                            JSONObject jsonuser = jsondata.getJSONObject("user");

                            JSONArray tracks = (JSONArray)jsonuser.get("tracks");
                            for (int i = 0; i < tracks.length(); i++) {
                                JSONObject childObject = tracks.getJSONObject(i);
                                JSONArray tr = (JSONArray)childObject.get("spent_time");
                                name = new String[tracks.length()];
                                name[i] = childObject.get("name").toString();
                                test2 = test2.concat(childObject.get("name").toString()+"\n");
                                test3 = test3.concat(childObject.get("name").toString()+"\n");
                                Log.i("name    ","ccc"+name[i]);
                                for (int j=0;j<tr.length(); j++){
                                    JSONObject one = tr.getJSONObject(j);
                                    Log.i("i start tiome","W "+one.getString("start_time"));
                                    Log.i("i end tiome","W "+one.getString("end_time"));
                                    Date date1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(one.getString("start_time"));
                                    Date date2=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(one.getString("end_time"));

                                    Log.i("conversionnnnn","ccc"+printDifference(date1, date2));
                                    dura = new String[tr.length()];
                                    dura[j] = printDifference(date1, date2);
                                    test = test.concat(printDifference(date1, date2)+"\n");
                                    test3 = test3.concat(printDifference(date1, date2)+"\n");
                                    Log.i("dura    ","ccc"+dura[j]);
                                }
                            }
                            Log.i("testingggggggg","g "+test);
                            tvDuration.setText(test3);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+getToken());
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        tvemail.setText(contacto.getNumero());
        tvnooftracks.setText(contacto.getCorreo());
        test3 = null;
        test3 = "";
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        convertView.startAnimation(animation);

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String nombre = (String) getGroup(groupPosition);
        UsersContacto contacto = (UsersContacto) getChild(groupPosition,0);

        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.adminuserhelper, null);

        }

        TextView txtNombre = convertView.findViewById(R.id.tvuserUsername);
        CircleImageView circleImageView = convertView.findViewById(R.id.imgActive);

        //userActive = true;
        if (contacto.getDireccion().equals("0")){
            txtNombre.setText(nombre);
            circleImageView.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_baseline_brightnessred_1_24));

        }
        return convertView;
    }

    public String printDifference(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;
        return "Days "+elapsedDays+ " Hours "+elapsedHours+" Minutes "+elapsedMinutes+" Seconds "+elapsedSeconds;
    }

    private String getToken(){
        SharedPreferences sh = context.getSharedPreferences("token", MODE_PRIVATE);
        String login = sh.getString("token", null);
        return login;
    }

    @Override
    public int getGroupCount() {
        return this.listTitulo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTitulo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListDetalles.get(this.listTitulo.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
