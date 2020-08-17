package com.myapp.bahuzu.UserHandles;


import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.Randevu;
import com.myapp.bahuzu.StreamingPage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RandevularAdapter extends ArrayAdapter<Randevu> {

    private Context mContext;
    int mResource;
    public static ArrayList<Randevu> myArrayList;

    public RandevularAdapter(Context context, int resource, ArrayList<Randevu> objects){
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        myArrayList = objects;
    }

    private class ViewHolder{
        TextView doctorFullName;
        TextView hour;
        TextView date;
        CircleImageView greenOrRed;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;


        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,null,true);

            holder.doctorFullName = (TextView) convertView.findViewById(R.id.doctorText);
            holder.date = (TextView) convertView.findViewById(R.id.dateText);
            holder.hour = (TextView) convertView.findViewById(R.id.hourText);
            holder.greenOrRed = (CircleImageView) convertView.findViewById(R.id.imageGreenOrRed);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String date = myArrayList.get(position).getDate();
        String exactDate = date.substring(0,2) + "." + date.substring(3,5) + "." + date.substring(6,10);

        holder.doctorFullName.setText(myArrayList.get(position).getDoctorFullName());
        holder.date.setText(exactDate);
        holder.hour.setText(myArrayList.get(position).getHour());

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
        String randevuHour = myArrayList.get(position).getHour();

        try {
            Date date1 = format.parse(myArrayList.get(position).getDate());
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



        return convertView;
    }


}
