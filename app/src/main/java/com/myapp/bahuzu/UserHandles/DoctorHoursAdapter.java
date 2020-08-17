package com.myapp.bahuzu.UserHandles;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.DoctorRandevu;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.myapp.bahuzu.R.color.myTextColor;

public class DoctorHoursAdapter extends ArrayAdapter<DoctorRandevu> {
    private Context mContext;
    int mResource;
    public static ArrayList<DoctorRandevu> myArrayList;

    public DoctorHoursAdapter(Context context, int resource, ArrayList<DoctorRandevu> objects){
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        myArrayList = objects;
    }

    private class ViewHolder{
        TextView hour;
        ImageView redView;
        ImageView greenView;
        String doctorUid;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,null,true);

            holder.hour = (TextView) convertView.findViewById(R.id.textView1);
            holder.redView = (ImageView) convertView.findViewById(R.id.imageviewRed);
            holder.greenView = (ImageView) convertView.findViewById(R.id.imageViewGreen);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.hour.setText(myArrayList.get(position).getHour());
        if(myArrayList.get(position).getStatus() == "Dolu"){
            convertView.setEnabled(true);
            convertView.setClickable(true);
            convertView.setFocusable(true);
            holder.hour.setTextColor(Color.GRAY);
            holder.redView.setVisibility(convertView.VISIBLE);
            holder.greenView.setVisibility(convertView.INVISIBLE);
        } else {
            convertView.setEnabled(false);
            convertView.setClickable(false);
            convertView.setFocusable(false);
            holder.hour.setTextColor(myTextColor);
            holder.greenView.setVisibility(convertView.VISIBLE);
            holder.redView.setVisibility(convertView.INVISIBLE);
        }


        return convertView;
    }


}
