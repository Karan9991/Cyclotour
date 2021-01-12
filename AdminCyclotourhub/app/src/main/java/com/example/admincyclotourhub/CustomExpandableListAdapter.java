package com.example.admincyclotourhub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admincyclotourhub.authentication.SignIn;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listTitulo;
    private HashMap<String, Contacto> expandableListDetalles;
    AdminTracks adminTracks;
    Activity activity;

    public CustomExpandableListAdapter(Context context, Activity activityy,
                                       List<String> listTitulo,
                                       HashMap<String, Contacto> expandableListDetalles) {
        this.context = context;
        this.listTitulo = listTitulo;
        this.expandableListDetalles = expandableListDetalles;
        this.activity = activityy;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Contacto contacto = (Contacto) getChild(groupPosition, childPosition);

        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.list_item, null);

        }
        TextView tv1 = convertView.findViewById(R.id.tv1);
        TextView tv2 = convertView.findViewById(R.id.tv2);
        Button btnUpdate = convertView.findViewById(R.id.btnUpdate);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        tv1.setText(contacto.getNumero());
        tv2.setText(contacto.getCorreo()+" Kms");

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("testinggggggg"," "+contacto.getDireccion()+" n "+contacto.getNumero()+" c "+contacto.getCorreo());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Delete");
                builder.setMessage("Are you Sure?");
                builder.setCancelable(true);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                          deleteTrack(contacto.getDireccion(), groupPosition);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateTrack.class);
                intent.putExtra("id", contacto.getDireccion());
                intent.putExtra("to", contacto.getExtra());
                intent.putExtra("from", contacto.getBextra());
                intent.putExtra("distance", contacto.getCorreo());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        convertView.startAnimation(animation);

        return convertView;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {


        String nombre = (String) getGroup(groupPosition);
        Contacto contacto = (Contacto) getChild(groupPosition,0);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.admincustomerhelper, null);
        }

        TextView txtNombre = convertView.findViewById(R.id.admintvName);
        TextView txtNumero = convertView.findViewById(R.id.admintvAddress);

        txtNombre.setText(nombre);

        return convertView;
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

    private void deleteTrack(String id, int groupPosition) {

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, "https://www.Forutube.com/public/api/admin/tracks/"+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("rrrrrrrrr","W del: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("message");
                            if (status.equals("200")){
                                listTitulo.remove(listTitulo.get(groupPosition));
                                notifyDataSetChanged();
                                Toast.makeText(context, " "+msg,Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, " Failed",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Delete Operation Failed : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Toast.makeText(context, "Delete Operation Failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

           }
    private String getToken(){
        SharedPreferences sh = context.getSharedPreferences("token", MODE_PRIVATE);
        String login = sh.getString("token", null);
        return login;
    }
}
