package com.karnwal.foodcommunity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<FoodDrive> listData;
    private OnEditListener onEditListener;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference;
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(firebaseUser.getUid());
    private String zipcode;
//    private boolean isDeleteClicked;

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
        userReference.child(Constants.ZIPCODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    zipcode = snapshot.getValue(String.class);
                    reference = FirebaseDatabase.getInstance().getReference().child(zipcode);
                }
                catch (Exception exception) {
                    Log.e("Exception ", "" + exception.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        try {
            if(!listData.get(position).getOwnerUID().equals(firebaseUser.getUid())) {
                    holder.imgDelete.setVisibility(View.GONE);
                    holder.imgEdit.setVisibility(View.GONE);
                }
            if (listData.get(position).getOwnerUID().equals(firebaseUser.getUid())) {
                    holder.imgDelete.setVisibility(View.VISIBLE);
                    holder.imgEdit.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception exception) {
            Log.e("Exception ", "" + exception.getMessage());
        }

        FoodDrive dataObj = listData.get(position);
        holder.nameTxt.setText(dataObj.getName());
        holder.addressTxt.setText(dataObj.getAddress());
        holder.foodListTxt.setText(dataObj.getFoodList());
        holder.dateTxt.setText(dataObj.getCalendar());
        holder.additionalInformationTxt.setText(dataObj.getAdditionalInformation());
        holder.imgDelete.setOnClickListener(v -> {
            FoodDrive foodDrive = listData.remove(position);
            reference.child(foodDrive.getUUID()).removeValue();
//            isDeleteClicked = true;
            notifyDataSetChanged();
        });
        holder.imgEdit.setOnClickListener(v -> {
            onEditListener.onEditClick(listData.get(position), position);
        });
    }

//    protected Boolean getDeleteStatus() {
//        if (isDeleteClicked) {
//            isDeleteClicked = false;
//            return true;
//        }
//        return false;
//    }

    @Override
    public int getItemCount() {
        return listData.size();
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
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }

    public void editData(FoodDrive listDataObj, int currentPosition) {
        FoodDrive foodDrive = listData.get(currentPosition);
        listData.get(currentPosition).setName(listDataObj.getName());
        reference.child(foodDrive.getUUID()).child(Constants.NAME).setValue(listDataObj.getName());
        listData.get(currentPosition).setAddress(listDataObj.getAddress());
        reference.child(foodDrive.getUUID()).child(Constants.ADDRESS).setValue(listDataObj.getAddress());
        listData.get(currentPosition).setFoodList(listDataObj.getFoodList());
        reference.child(foodDrive.getUUID()).child(Constants.FOODLIST).setValue(listDataObj.getFoodList());
        listData.get(currentPosition).setCalendar(listDataObj.getCalendar());
        reference.child(foodDrive.getUUID()).child(Constants.CALENDAR).setValue(listDataObj.getCalendar());
        listData.get(currentPosition).setAdditionalInformation(listDataObj.getAdditionalInformation());
        reference.child(foodDrive.getUUID()).child(Constants.ADDITIONALINFORMATION).setValue(listDataObj.getAdditionalInformation());
        notifyDataSetChanged();
    }

    public interface OnEditListener{
        void onEditClick(FoodDrive listCurrentData, int CurrentPosition);
    }
}