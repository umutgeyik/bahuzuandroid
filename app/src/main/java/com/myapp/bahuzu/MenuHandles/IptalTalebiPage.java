package com.myapp.bahuzu.MenuHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.Randevu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class IptalTalebiPage extends AppCompatActivity {

    ListView listView;
    ArrayList<Randevu> randevuList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;
    TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iptal_talebi_page);
        mFirebaseAuth = FirebaseAuth.getInstance();

        emptyText = findViewById(R.id.empty);
        listView = findViewById(R.id.listView);
        listView.setEmptyView(emptyText);
        retrieveRandevuData();
    }



    public void retrieveRandevuData(){
        db.collection("randevular").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("userUid").equals(mFirebaseAuth.getCurrentUser().getUid()) ){
                            Randevu myDoc = new Randevu(document.getString("doctorUid"),document.getString("userUid"),document.getString("date"),document.getString("hour"),document.getString("doctorFullName"),document.getString("userFullName"));
                            myDoc.setRandevuId(document.getString("randevuId"));
                            if(randevuChecker(myDoc.getDate(),myDoc.getHour())){
                                randevuList.add(myDoc);
                            }
                            //randevuList.add(myDoc);
                        } else {
                            // Do Nothing
                        }

                    }
                    Collections.sort(randevuList);
                    createRandevuList();

                } else {
                    System.out.println("Bos");
                }
            }
        });
    }

    public void createRandevuList(){

        final ArrayAdapter myArrayAdapter = new IptalTalebiAdapter(this,R.layout.iptal_talebi_checkbox,randevuList);
        listView.setDivider(null);
        listView.setDividerHeight(50);
        listView.setAdapter(myArrayAdapter);


    }

    public boolean randevuChecker(String date, String hour){

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

        try {
            Date date1 = format.parse(date);
            Date date2 = format.parse(myCurrentDate);
            if (date2.compareTo(date1) == 0) {
                Date hour1 = datos.parse(hour);
                Date hour2 = datos.parse(finalHour);
                Date hour3 = datos.parse(hour);

                Calendar cal = Calendar.getInstance();
                cal.setTime(hour3);
                cal.add(Calendar.MINUTE,50);
                hour3 = cal.getTime();


                if(hour2.compareTo(hour1) <= 0){
                    // Randevu saatine daha var
                    return true;

                } else {
                    return false;

                }


            } else if(date2.compareTo(date1) < 0){
                // Ayni gun degil ama tarih daha erken
                return true;
            } else {
                return false;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }
}
