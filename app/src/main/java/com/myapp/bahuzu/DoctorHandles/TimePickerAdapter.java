package com.myapp.bahuzu.DoctorHandles;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.DoctorRandevu;

import java.util.ArrayList;

public class TimePickerAdapter extends ArrayAdapter<DoctorRandevu> {

    private static final String TAG = "TimePickerAdapter";

    private Context mContext;
    int mResource;
    public static ArrayList<DoctorRandevu> myArrayList;
    private CheckBoxListener checkedListener;

    public TimePickerAdapter(Context context, int resource, ArrayList<DoctorRandevu> objects){
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        myArrayList = objects;
    }

    private class ViewHolder{
        TextView hour;
        TextView status;
        CheckBox checkBx;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,null,true);

            holder.checkBx = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.hour= (TextView) convertView.findViewById(R.id.textView1);
            holder.status = (TextView) convertView.findViewById(R.id.textView2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //Change for view...
        holder.checkBx.setText("CheckBox" + position);
        holder.hour.setText(myArrayList.get(position).getHour());
        holder.status.setText(myArrayList.get(position).getStatus());

        holder.checkBx.setChecked(myArrayList.get(position).isChecked());



        if(holder.checkBx.isChecked()){
            holder.checkBx.setText("Musait");
            holder.status.setText("Musait");
            myArrayList.get(position).setStatus("Musait");
            holder.checkBx.setClickable(true);
            holder.checkBx.setEnabled(true);
        } else if(myArrayList.get(position).getStatus().equals("Satildi")){
            holder.checkBx.setText("Satildi");
            holder.status.setText("Satildi");
            holder.checkBx.setClickable(false);
            holder.checkBx.setEnabled(false);
            myArrayList.get(position).setStatus("Satildi");
        } else {
            holder.checkBx.setText("Dolu");
            holder.status.setText("Dolu");
            holder.checkBx.setClickable(true);
            holder.checkBx.setEnabled(true);
            myArrayList.get(position).setStatus("Dolu");
        }

        holder.checkBx.setTag(R.integer.btnplusview,convertView);
        holder.checkBx.setTag(position);
        holder.checkBx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                View tempview = (View) holder.checkBx.getTag(R.integer.btnplusview);
                TextView hourText = (TextView) tempview.findViewById(R.id.textView1);
                TextView statusText = (TextView) tempview.findViewById(R.id.textView2);
                TextView patientText = (TextView) tempview.findViewById(R.id.textView3);
                Integer pos = (Integer) holder.checkBx.getTag();
        /*
                if(holder.checkBx.getText().equals("Musait")){
                    holder.checkBx.setText("DOLU");
                } else {
                    holder.checkBx.setText("Musait");
                }*/

                if(holder.checkBx.isChecked()){
                    holder.checkBx.setText("Musait");
                    holder.status.setText("Musait");
                    myArrayList.get(position).setStatus("Musait");
                } else {
                    holder.checkBx.setText("Dolu");
                    holder.status.setText("Dolu");
                    myArrayList.get(position).setStatus("Dolu");
                }





                if(myArrayList.get(pos).isChecked()){
                    myArrayList.get(pos).setChecked(false);
                } else {
                    myArrayList.get(pos).setChecked(true);
                }

            }
        });

        return convertView;


        /*
        String hour = getItem(position).getHour();
        String status = getItem(position).getStatus();
        String patientName = getItem(position).getPatientName();
        Boolean isChecked = getItem(position).isChecked();

        DoctorRandevu myRandevu = new DoctorRandevu(hour,status,patientName,isChecked);


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent , false);
        TextView hourText = (TextView) convertView.findViewById(R.id.textView1);
        TextView statusText = (TextView) convertView.findViewById(R.id.textView2);
        TextView patientText = (TextView) convertView.findViewById(R.id.textView3);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);



        hourText.setText(hour);
        statusText.setText(status);
        patientText.setText(patientName);
        checkBox.setChecked(isChecked);

        return convertView;   */

    }

    public interface CheckBoxListener{

        void getCheckBoxChecked(int position);
    }

    public void setCheckedListener(CheckBoxListener checkedListener){
        this.checkedListener = checkedListener;
    }


}
