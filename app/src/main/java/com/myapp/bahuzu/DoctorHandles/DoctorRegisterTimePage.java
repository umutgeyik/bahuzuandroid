package com.myapp.bahuzu.DoctorHandles;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.DoctorRandevu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DoctorRegisterTimePage extends AppCompatActivity {

    ListView listView;
    TimePickerAdapter myAdapter;
    ArrayList<DoctorRandevu> modelArrayList;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayList<DoctorRandevu> doctListHi = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register_time_page);


        mFirebaseAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listView);
        modelArrayList = getModel(false);
        createList();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.save_button){

            modelArrayList = getDoctorRandevuModel();
            myAdapter = new TimePickerAdapter(DoctorRegisterTimePage.this,R.layout.timepicker_listview, TimePickerAdapter.myArrayList);
            listView.setAdapter(myAdapter);
            saveDaysToDb();
        } else {
            Toast.makeText(getApplicationContext(), "Hata meydana geldi.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void lastSave(String hours){
        String uid = mFirebaseAuth.getCurrentUser().getUid();


        Calendar c = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|YYYY");
        String startDate = dt1.format(currentTime);
        c.setTime(currentTime);
        //c.add(Calendar.DATE,1);
        String date;

        Map<String,String> docData = new HashMap<>();
        docData.put(dt1.format(c.getTime()),hours);

        for(int i=0;i<365;i++){

            c.add(Calendar.DATE,1);
            date = dt1.format(c.getTime());
            docData.put(date,hours);
        }

        db.collection("doctors_dates").document(uid).set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Doktor Tarihlerinin kaydi basarili
                Intent intent = new Intent(DoctorRegisterTimePage.this,DoctorRandevuSaatleri.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                DoctorRegisterTimePage.this.startActivity(intent);
                ActivityCompat.finishAffinity(DoctorRegisterTimePage.this);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorRegisterTimePage.this, "Bilgilerini kayıt ederken hata meydana geldi!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveDaysToDb() {

        String uid = mFirebaseAuth.getCurrentUser().getUid();
        DocumentReference myReference = db.collection("doctors_dates").document(uid);
        final String hours = getHours();

        lastSave(hours);
    }

    private String getHours() {
        String hours = "";
        ArrayList<DoctorRandevu> denemeList = TimePickerAdapter.myArrayList;
        for(int i = 0;i<denemeList.size();i++){
            if(denemeList.get(i).getStatus().equals("Dolu")){
                hours += "f-";
            } else if(denemeList.get(i).getStatus().equals("Musait")){
                hours += "t-";
            } else {
                hours += "s-";
            }
        }
        return hours.substring(0,hours.length()-1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return true;
    }

    public ArrayList<DoctorRandevu> getModel(boolean isSelect){
        ArrayList<DoctorRandevu> list = new ArrayList<>();
        for(int i = 0;i< doctListHi.size();i++){
            DoctorRandevu doc = new DoctorRandevu();
            doc.setChecked(isSelect);
            doc.setHour(doctListHi.get(i).getHour());
            doc.setStatus(doctListHi.get(i).getStatus());
            doc.setPatientName(doctListHi.get(i).getPatientName());
            list.add(doc);
        }
        return list;
    }

    public ArrayList<DoctorRandevu> getDoctorRandevuModel(){
        ArrayList<DoctorRandevu> list = new ArrayList<>();for(int i = 0;i< doctListHi.size();i++){
            DoctorRandevu doc = new DoctorRandevu();
            doc.setChecked(doctListHi.get(i).isChecked());
            doc.setHour(doctListHi.get(i).getHour());
            doc.setStatus(doctListHi.get(i).getStatus());
            doc.setPatientName(doctListHi.get(i).getPatientName());
            list.add(doc);
        }
        return list;
    }

    public void createList(){

        hourBuilder();
        lastShot();
        //final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice,myArrayList);
        final ArrayAdapter arrayAdapter = new TimePickerAdapter(this,R.layout.timepicker_listview,doctListHi);
        listView.setClipToPadding(false);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }



    public void lastShot(){

        for(int i = 0;i<23;i++){
            DoctorRandevu doc = new DoctorRandevu();
            doc.setStatus("Dolu");
            doc.setHour(myArrayList.get(i));
            if(doc.getStatus().equals("Dolu")){
                doc.setChecked(false);
            } else if(doc.getStatus().equals("Musait")) {
                doc.setChecked(true);
            } else {
                doc.setChecked(false);
            }
            doctListHi.add(doc);
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
