package com.myapp.bahuzu.UserHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.DoctorHandles.TimePickerAdapter;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.DoctorRandevu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DoctorHoursPage extends AppCompatActivity {

    ListView listView;
    String doctorDate;
    String doctorUid;
    String doctorTimes;
    String doctorFullName;
    String price;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayList<DoctorRandevu> doctListHi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_hours_page);

        doctorUid = getIntent().getStringExtra("DOCTOR_UID");
        doctorDate = getIntent().getStringExtra("DATE_INFORMATION");
        doctorFullName = getIntent().getStringExtra("DOCTOR_INFORMATION");
        price = getIntent().getStringExtra("PRICE");

        mFirebaseAuth = FirebaseAuth.getInstance();
        listView = findViewById(R.id.listView);
        retrieveDate();

    }


    public void retrieveDate(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("doctors_dates").document(doctorUid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){

                    doctorTimes = document.getString(doctorDate);
                    for(int i = 0; i < doctorTimes.length(); i+=2){
                        if(doctorTimes.charAt(i) == 'f' || doctorTimes.charAt(i) == 's'){

                            arrayList.add("Dolu");
                        } else {

                            arrayList.add("Musait");
                        }
                    }
                    createList();
                }
                else {
                }
            }
        });

    }

    public void createList(){

        hourBuilder();
        lastShot();

        final ArrayAdapter arrayAdapter = new DoctorHoursAdapter(this,R.layout.hours,doctListHi);
        if(doctListHi.size()==0){
            Toast.makeText(this, "Üzgünüz, bugün için rezervasyon saatleri tükemiştir.", Toast.LENGTH_SHORT).show();
        }
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(DoctorHoursPage.this,RandevuAlPage.class);
                intent.putExtra("DOCTOR_UID",doctListHi.get(position).getDoctorUid());
                intent.putExtra("HOUR",doctListHi.get(position).getHour());
                intent.putExtra("DATE",doctListHi.get(position).getDate());
                intent.putExtra("DOCTOR_FULLNAME",doctListHi.get(position).getDoctorName());
                intent.putExtra("HOUR_POSITION",getHourPosition(doctListHi.get(position).getHour()));
                intent.putExtra("PRICE",price);
                startActivity(intent);
            }
        });

    }

    private int getHourPosition(String hour) {
        for(int i=0;i<myArrayList.size();i++){
            if(myArrayList.get(i).equals(hour)){
                return i;
            }
        }

        return 0;
    }

    public void lastShot(){

        for(int i = 0;i<arrayList.size();i++){
            DoctorRandevu doc = new DoctorRandevu();
            doc.setStatus(arrayList.get(i));
            doc.setHour(myArrayList.get(i));
            doc.setDoctorName(doctorFullName);
            doc.setDoctorUid(doctorUid);
            doc.setDate(doctorDate);
            if(doc.getStatus().equals("Dolu")){
                doc.setChecked(false);
            } else {
                doc.setChecked(true);
            }


            Calendar c = Calendar.getInstance();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|YYYY");
            String startDate = dt1.format(currentTime);
            c.setTime(currentTime);
            String date;

            String myCurrentDate = dt1.format(c.getTime());



            if(myCurrentDate.equals(doctorDate)){
                if(i-1<c.get(Calendar.HOUR_OF_DAY)){

                    // Dont add
                } else {
                    doctListHi.add(doc);
                }
            }
            else {
                doctListHi.add(doc);
            }



        }
    }


    public void hourBuilder(){
        /*
        myList.put("00:45",arrayList.get(0));
        myList.put("01:45",arrayList.get(1));
        myList.put("02:45",arrayList.get(2));
        myList.put("03:45",arrayList.get(3));
        myList.put("04:45",arrayList.get(4));
        myList.put("05:45",arrayList.get(5));
        myList.put("06:45",arrayList.get(6));
        myList.put("07:45",arrayList.get(7));
        myList.put("08:45",arrayList.get(8));
        myList.put("09:45",arrayList.get(9));
        myList.put("10:45",arrayList.get(10));
        myList.put("11:45",arrayList.get(11));
        myList.put("12:45",arrayList.get(12));
        myList.put("13:45",arrayList.get(13));
        myList.put("14:45",arrayList.get(14));
        myList.put("15:45",arrayList.get(15));
        myList.put("16:45",arrayList.get(16));
        myList.put("17:45",arrayList.get(17));
        myList.put("18:45",arrayList.get(18));
        myList.put("19:45",arrayList.get(19));
        myList.put("20:45",arrayList.get(20));
        myList.put("21:45",arrayList.get(21));
        myList.put("22:45",arrayList.get(22));
        myList.put("23:45",arrayList.get(23));
*/

        myArrayList.add("00:45");
        myArrayList.add("01:45");
        myArrayList.add("02:45");
        myArrayList.add("03:45");
        myArrayList.add("04:45");
        myArrayList.add("05:45");
        myArrayList.add("06:45");
        myArrayList.add("07:45");
        myArrayList.add("08:45");
        myArrayList.add("09:45");
        myArrayList.add("10:45");
        myArrayList.add("11:45");
        myArrayList.add("12:45");
        myArrayList.add("13:45");
        myArrayList.add("14:45");
        myArrayList.add("15:45");
        myArrayList.add("16:45");
        myArrayList.add("17:45");
        myArrayList.add("18:45");
        myArrayList.add("19:45");
        myArrayList.add("20:45");
        myArrayList.add("21:45");
        myArrayList.add("22:45");


    }
}
