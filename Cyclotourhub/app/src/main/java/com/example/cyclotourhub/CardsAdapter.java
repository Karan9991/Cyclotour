package com.example.cyclotourhub;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cyclotourhub.model.CardModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

//import android.support.annotation.NonNull;

/**
 * @author Alhaytham Alfeel on 10/10/2016.
 */

public class CardsAdapter extends ArrayAdapter<CardModel> implements Filterable {

    Context myContext;
List<CardModel> cardModelList;
    List<CardModel> cardModelListfiltered;

    LayoutInflater inflater;
CardModel cardModel = new CardModel();
    private static int currentSelectedIndex = -1;

    private SparseBooleanArray mSelectedItemsIds;
    private int mSelectedP = -1;
    public static String status, message, createdat, updatedat;
    public static double slat,slng, elat, elng, sln, slt, elt, eln;
    String distance;
    Date currentTime;
    ProgressDialog progressDialog;

    ViewHolder holder;

    public CardsAdapter(Context context, int resourceId, List<CardModel> cardModelList) {
        super(context,resourceId, cardModelList);
        this.cardModelList = cardModelList;
        this.cardModelListfiltered = cardModelList;
        mSelectedItemsIds = new SparseBooleanArray();

        this.myContext = context;

        inflater =  LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.customerhelper, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        progressDialog = new ProgressDialog(this.myContext);
        CardModel model = getItem(position);

        holder.tvName.setText(cardModelListfiltered.get(position).getName());
        holder.tvAddress.setText("  -  "+cardModelListfiltered.get(position).getAddress());
        holder.tvslat.setText(cardModelListfiltered.get(position).getSlat());
        holder.tvslng.setText(cardModelListfiltered.get(position).getSlng());
        holder.tvelat.setText(cardModelListfiltered.get(position).getElat());
        holder.tvelng.setText(cardModelListfiltered.get(position).getElng());
        holder.tvtrackid.setText(String.valueOf(cardModelListfiltered.get(position).getId()));
        holder.tvtrackname.setText(cardModelListfiltered.get(position).getTrackname());

        currentTime = Calendar.getInstance().getTime();
        createdat = currentTime.toString();
        updatedat = currentTime.toString();

        //find lat lng
        new Thread( new Runnable() { @Override public void run() {
            Geocoder coder = new Geocoder(getApplicationContext());
            List<Address> address;
            try {
                address = coder.getFromLocationName(cardModelListfiltered.get(position).getName(),5);
                Address location=address.get(0);
                slat = location.getLatitude();
                slng =  location.getLongitude();
                Log.i("cccccc"," "+slat);
            }catch (Exception e){
            }
            //
            Geocoder coder2 = new Geocoder(getApplicationContext());
            List<Address> address2;
            try {
                address2 = coder2.getFromLocationName(cardModelListfiltered.get(position).getAddress(),5);
                Address location=address2.get(0);
                elat = location.getLatitude();
                elng =  location.getLongitude();
                distance = " "+new DecimalFormat("##.##").format(findDistance())+" Kms";
                Log.i("ddddd"," "+new DecimalFormat("##.##").format(findDistance())+" Kms");
            }catch (Exception e){

            }
            Log.i("oooooo", "o "+cardModelListfiltered.get(position).getTrackid());
            sln = (int)slat;
            slt = (int)slng;
            elt = (int)elat;
            eln = (int)elng;        } } ).start();
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // update the ui from here
//                        Geocoder coder = new Geocoder(getApplicationContext());
//        List<Address> address;
//        try {
//            address = coder.getFromLocationName(cardModelListfiltered.get(position).getName(),5);
//            Address location=address.get(0);
//            slat = location.getLatitude();
//            slng =  location.getLongitude();
//            Log.i("cccccc"," "+slat);
//        }catch (Exception e){
//        }
//        //
//        Geocoder coder2 = new Geocoder(getApplicationContext());
//        List<Address> address2;
//        try {
//            address2 = coder2.getFromLocationName(cardModelListfiltered.get(position).getAddress(),5);
//            Address location=address2.get(0);
//            elat = location.getLatitude();
//            elng =  location.getLongitude();
//            distance = " "+new DecimalFormat("##.##").format(findDistance())+" Kms";
//            Log.i("ddddd"," "+new DecimalFormat("##.##").format(findDistance())+" Kms");
//        }catch (Exception e){
//
//        }
//        Log.i("oooooo", "o "+cardModelListfiltered.get(position).getTrackid());
//        sln = (int)slat;
//        slt = (int)slng;
//        elt = (int)elat;
//        eln = (int)elng;
//            }
//        },1000);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView tvName, tvAddress, tvslat, tvslng, tvelat, tvelng, tvtrackid, tvtrackname;
        LinearLayout relativeLayout;
        TextView tvSubtitle;

        ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            tvslat = (TextView) view.findViewById(R.id.tvslat);
            tvslng = (TextView) view.findViewById(R.id.tvslng);
            tvelat = (TextView) view.findViewById(R.id.tvelat);
            tvelng = (TextView) view.findViewById(R.id.tvelng);
            tvtrackid = (TextView) view.findViewById(R.id.tvtrackid);
            tvtrackname = (TextView) view.findViewById(R.id.tvtrackname);
        }
    }

//Filter for search
    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = cardModelList.size();
                    filterResults.values = cardModelList;

                }else{
                    List<CardModel> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(CardModel itemsModel:cardModelList){
                        if(itemsModel.getAddress().toLowerCase().contains(searchStr)||itemsModel.getName().toLowerCase().contains(searchStr)){
                            resultsModel.add(itemsModel);
                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                cardModelListfiltered = (List<CardModel>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;    }
    @Override
    public int getCount() {
        return cardModelListfiltered.size();
    }

    @Nullable
    @Override
    public CardModel getItem(int position) {
        return cardModelListfiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public void remove(@Nullable CardModel object) {
        cardModelList.remove(object);
        notifyDataSetChanged();
    }

    public List<CardModel> getMyList() {
        return cardModelList;
    }

public void pos(int position){
        holder.tvName.setBackgroundResource(R.color.colorAccent);
        holder.relativeLayout.setBackgroundResource(R.color.colorAccent);

}
    public void  toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));

    }

    // Remove selection after unchecked

    public void  removeSelection() {

        mSelectedItemsIds = new SparseBooleanArray();

        notifyDataSetChanged();

    }

    public void selectView(int position, boolean value) {

        if (value) {
            currentSelectedIndex = position;
            mSelectedItemsIds.put(position, value);
        }else {

            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();

    }

    public void setSelectedItem(int itemPosition) {
        mSelectedP = itemPosition;
        notifyDataSetChanged();
    }
    // Get number of selected item

    public int  getSelectedCount() {

        return mSelectedItemsIds.size();

    }
    public void updateRecords(List<CardModel> card){
        this.cardModelList = card;

        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {

        return mSelectedItemsIds;

    }
    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    private double findDistance(){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(slat);
        startPoint.setLongitude(slng);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(elat);
        endPoint.setLongitude(elng);

        double distance=startPoint.distanceTo(endPoint)/1000;
        return distance;
    }
}

