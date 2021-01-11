package com.example.admincyclotourhub;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdminCardsAdapter extends ArrayAdapter<AdminCardModel> implements Filterable {

    Context myContext;
    List<AdminCardModel> cardModelList;
    List<AdminCardModel> cardModelListfiltered;

    LayoutInflater inflater;
    AdminCardModel cardModel = new AdminCardModel();
    private static int currentSelectedIndex = -1;

    private SparseBooleanArray mSelectedItemsIds;
    private int mSelectedP = -1;

    AdminCardsAdapter.ViewHolder holder;

    public AdminCardsAdapter(Context context, int resourceId, List<AdminCardModel> cardModelList) {
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

            convertView = inflater.inflate(R.layout.admincustomerhelper, parent, false);
            holder = new AdminCardsAdapter.ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (AdminCardsAdapter.ViewHolder) convertView.getTag();
        }

        AdminCardModel model = getItem(position);

        holder.admintvName.setText(cardModelListfiltered.get(position).getName());
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView admintvName, admintvAddress, tvTest, tvPhone;
        LinearLayout relativeLayout;
        TextView tvSubtitle;

        ViewHolder(View view) {
            admintvName = (TextView) view.findViewById(R.id.admintvName);
            admintvAddress = (TextView) view.findViewById(R.id.admintvAddress);

        }
    }


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
                    List<AdminCardModel> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(AdminCardModel itemsModel:cardModelList){
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
                cardModelListfiltered = (List<AdminCardModel>) results.values;
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
    public AdminCardModel getItem(int position) {
        return cardModelListfiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public void remove(@Nullable AdminCardModel object) {
        cardModelList.remove(object);
        notifyDataSetChanged();
    }

    public List<AdminCardModel> getMyList() {
        return cardModelList;
    }

    public void pos(int position){
        holder.admintvName.setBackgroundResource(R.color.colorAccent);
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
    public void updateRecords(List<AdminCardModel> card){
        this.cardModelList = card;

        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {

        return mSelectedItemsIds;

    }
    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }
}
