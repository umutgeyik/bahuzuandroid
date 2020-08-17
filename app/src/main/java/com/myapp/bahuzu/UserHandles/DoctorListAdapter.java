package com.myapp.bahuzu.UserHandles;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapp.bahuzu.Root.Doctor;
import com.myapp.bahuzu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorListViewHolder> {
    private ArrayList<Doctor> mDocList;
    private OnItemClickListener mListener;

    private StorageReference mDatabaseRef;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class DoctorListViewHolder extends RecyclerView.ViewHolder {
        public TextView doctorFullName;
        public TextView doctorProfession;
        public RatingBar doctorRating;
        public CircleImageView profileImage;

        public DoctorListViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            doctorFullName = (TextView) itemView.findViewById(R.id.textView1);
            doctorProfession = (TextView) itemView.findViewById(R.id.textView2);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profileImage);
            doctorRating = (RatingBar) itemView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public Context getContext(){return itemView.getContext();}
    }

    public DoctorListAdapter(ArrayList<Doctor> docList){
        mDocList = docList;
    }

    @NonNull
    @Override
    public DoctorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_doctors_listview,parent,false);
        DoctorListViewHolder evh = new DoctorListViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListViewHolder holder, int position) {
        Doctor currentDoctor = mDocList.get(position);

        holder.doctorFullName.setText(currentDoctor.getName());
        holder.doctorProfession.setText(currentDoctor.getProfession());
        holder.doctorRating.setRating(5);

        try{
            mDatabaseRef = FirebaseStorage.getInstance().getReference("media/" + currentDoctor.getUid() + ".jpg");
            mDatabaseRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(holder.getContext()).load(uri).into(holder.profileImage);
                }

            });
        } catch (Exception e){
            //CAN NOT DOWNLOAD MEDIA
        }
    }

    @Override
    public int getItemCount() {
        return mDocList.size();
    }
}
