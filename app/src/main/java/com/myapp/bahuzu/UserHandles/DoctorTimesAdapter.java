package com.myapp.bahuzu.UserHandles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.DoctorDates;
import java.util.ArrayList;


public class DoctorTimesAdapter extends ArrayAdapter<DoctorDates> {

    private Context mContext;
    int mResource;
    public static ArrayList<DoctorDates> myArrayList;

    public DoctorTimesAdapter(Context context, int resource, ArrayList<DoctorDates> objects){
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        myArrayList = objects;
    }

    private class ViewHolder{
        TextView dateOf;
        TextView okStatus;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,null,true);

            holder.dateOf = (TextView) convertView.findViewById(R.id.dateText);
            holder.okStatus = (TextView) convertView.findViewById(R.id.okText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String date = myArrayList.get(position).getDate();
        String exactDate = date.substring(0,2) + "." + date.substring(3,5) + "." + date.substring(6,10);

        holder.dateOf.setText(exactDate);
        //holder.dateOf.setTextColor(R.color.lightBlueColor);
        holder.okStatus.setText(String.valueOf(myArrayList.get(position).getOkStatus()) + " müsait zaman");
        //holder.okStatus.setTextColor(R.color.lightBlueColor);

        return convertView;
    }


}