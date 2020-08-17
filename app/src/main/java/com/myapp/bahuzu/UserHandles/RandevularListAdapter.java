package com.myapp.bahuzu.UserHandles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.Randevu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RandevularListAdapter extends RecyclerView.Adapter<RandevularListAdapter.RandevularListViewHolder> {
    private ArrayList<Randevu> mRandList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {mListener = listener;}



    public static class RandevularListViewHolder extends RecyclerView.ViewHolder {
        public TextView doctorFullName;
        public TextView hour;
        public TextView date;
        public CircleImageView greenOrRed;


        public RandevularListViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            doctorFullName = (TextView) itemView.findViewById(R.id.doctorText);
            date = (TextView) itemView.findViewById(R.id.dateText);
            hour = (TextView) itemView.findViewById(R.id.hourText);
            greenOrRed = (CircleImageView) itemView.findViewById(R.id.imageGreenOrRed);

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

    public RandevularListAdapter(ArrayList<Randevu> randList) {mRandList = randList;}

    @NonNull
    @Override
    public RandevularListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.randevu_card,parent,false);
        RandevularListViewHolder evh = new RandevularListViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull RandevularListViewHolder holder, int position) {
        Randevu currentRandevu = mRandList.get(position);

        String date = currentRandevu.getDate();
        String exactDate = date.substring(0,2) + "." + date.substring(3,5) + "." + date.substring(6,10);

        holder.doctorFullName.setText(currentRandevu.getDoctorFullName());
        holder.date.setText(exactDate);
        holder.hour.setText(currentRandevu.getHour());

        Calendar c = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|yyyy");
        c.setTime(currentTime);



        DateFormat format = new SimpleDateFormat("dd|MM|yyyy");
        DateFormat datos = new SimpleDateFormat("HH:mm");

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
        String randevuHour = currentRandevu.getHour();

        try {
            Date date1 = format.parse(currentRandevu.getDate());
            Date date2 = format.parse(myCurrentDate);
            if (date2.compareTo(date1) == 0) {
                Date hour1 = datos.parse(randevuHour);
                Date hour2 = datos.parse(finalHour);
                Date hour3 = datos.parse(randevuHour);

                Calendar cal = Calendar.getInstance();
                cal.setTime(hour3);
                cal.add(Calendar.MINUTE,50);
                hour3 = cal.getTime();


                if(hour1.compareTo(hour2) <= 0 && hour2.compareTo(hour3) <= 0){
                    // Randevu saati sinirlarindayiz
                    holder.greenOrRed.setImageResource(R.drawable.ic_green_circle_warning);

                } else if(hour2.compareTo(hour1) <= 0){
                    // Randevu saatine daha var
                    holder.greenOrRed.setImageResource(R.drawable.ic_red_circle_warning);
                } else {
                    // Gecmis randevu
                    holder.greenOrRed.setImageResource(R.drawable.ic_red_circle_warning);
                }


            } else if(date2.compareTo(date1) < 0){
                // Ayni gun degil ama tarih daha erken
                holder.greenOrRed.setImageResource(R.drawable.ic_red_circle_warning);
            } else {
                holder.greenOrRed.setImageResource(R.drawable.ic_red_circle_warning);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mRandList.size();
    }
}
