package com.myapp.bahuzu.MenuHandles;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.DoctorHandles.DoctorRandevuSaatleri;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.ErtelemeTalebi;
import com.myapp.bahuzu.Root.Randevu;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IptalTalebiAdapter extends ArrayAdapter<Randevu> {

    private Context mContext;
    int mResource;
    public static ArrayList<Randevu> myArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    String uIdentity = "Gec";

    public IptalTalebiAdapter(Context context, int resource, ArrayList<Randevu> objects){
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        myArrayList = objects;
    }

    private class ViewHolder{
        TextView doctorFullName;
        TextView hour;
        TextView date;
        Button iptal;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        getUserIdentity();

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,null,true);

            holder.doctorFullName = (TextView) convertView.findViewById(R.id.doctorText);
            holder.date = (TextView) convertView.findViewById(R.id.dateText);
            holder.hour = (TextView) convertView.findViewById(R.id.hourText);
            holder.iptal = (Button) convertView.findViewById(R.id.checkBoxIptal);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.doctorFullName.setText("Danışman: " +  myArrayList.get(position).getDoctorFullName());
        holder.date.setText("Randevu Tarihi: "  + myArrayList.get(position).getDate());
        holder.hour.setText("Randevu Saati: " +  myArrayList.get(position).getHour());

        holder.iptal.setTag(position);
        holder.iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.iptal.getTag();

                Calendar c = Calendar.getInstance();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|yyyy");
                c.setTime(currentTime);


                String myCurrentDate = dt1.format(c.getTime());
                int myCurrentHour = c.get(Calendar.HOUR_OF_DAY);
                int myCurrentMinute = c.get(Calendar.MINUTE);
                String currentHour = String.valueOf(myCurrentHour);
                String currentMinute;
                if(myCurrentMinute<10){
                    currentMinute = "0" + String.valueOf(myCurrentMinute);
                } else  {
                    currentMinute = String.valueOf(myCurrentMinute);
                }
                String finalHour = currentHour + ":" + currentMinute;


                ErtelemeTalebi myTalep = new ErtelemeTalebi(myArrayList.get(pos).getDoctorUid(),myArrayList.get(pos).getUserUid(),finalHour,myCurrentDate,uIdentity);

                db.collection("erteleme_talebi").document(myArrayList.get(pos).getRandevuId()).set(myTalep);

                if(uIdentity.equals("doctor")){
                    Intent intent = new Intent(mContext, DoctorRandevuSaatleri.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                }
                else{
                    Intent intent = new Intent(mContext, RandevuAraPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                }
            }
        });

        return convertView;
    }

    public void getUserIdentity(){
        DocumentReference docRef = db.collection("userIdentity").document(mFirebaseAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        if(document.getData().containsValue("doctor")){
                            uIdentity = "doctor";
                        }
                        else{
                            uIdentity = "user";
                        }
                    }
                }
            }
        });
    }


}
