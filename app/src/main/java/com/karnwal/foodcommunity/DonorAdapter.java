package com.karnwal.foodcommunity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<FoodDrive> listData;
    private OnEditListener onEditListener;

    public DonorAdapter(Context context, ArrayList<FoodDrive> listData, OnEditListener onEditListener) {
        this.context = context;
        this.listData = listData;
        this.onEditListener = onEditListener;
    }

    public void setTaskModelList(List<FoodDrive> subTrialList) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DonorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent,false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorAdapter.MyViewHolder holder, int position) {
        FoodDrive dataObj = listData.get(position);
        holder.nameTxt.setText(dataObj.getName());
        holder.addressTxt.setText(dataObj.getAddress());
        holder.foodListTxt.setText(dataObj.getFoodList());
        holder.dateTxt.setText(dataObj.getDate());
        holder.additionalInformationTxt.setText(dataObj.getAdditionalInformation());
        holder.imgDelete.setOnClickListener(v -> {
            listData.remove(position);
            notifyDataSetChanged();
        });
        holder.imgEdit.setOnClickListener(v -> {
            onEditListener.onEditClick(listData.get(position), position);
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, addressTxt, foodListTxt, dateTxt, additionalInformationTxt;
        ImageView imgEdit, imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxtId);
            addressTxt = itemView.findViewById(R.id.addressTxtId);
            foodListTxt = itemView.findViewById(R.id.foodListId);
            dateTxt = itemView.findViewById(R.id.dateId);
            additionalInformationTxt = itemView.findViewById(R.id.additionalInformationID);
        }
    }

    public void editData(FoodDrive listDataObj, int currentPosition) {
        listData.get(currentPosition).setName(listDataObj.getName());
        listData.get(currentPosition).setAddress(listDataObj.getAddress());
        listData.get(currentPosition).setFoodList(listDataObj.getFoodList());
        listData.get(currentPosition).setCalendar(listDataObj.getCalendar());
        listData.get(currentPosition).setAdditionalInformation(listDataObj.getAdditionalInformation());
        notifyDataSetChanged();
    }

    public interface OnEditListener{
        void onEditClick(FoodDrive listCurrentData, int CurrentPosition);
    }
}
