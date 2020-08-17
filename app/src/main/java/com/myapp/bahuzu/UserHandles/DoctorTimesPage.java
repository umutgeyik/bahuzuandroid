package com.myapp.bahuzu.UserHandles;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.DoctorDates;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DoctorTimesPage extends AppCompatActivity {

    ListView listView;
    String doctorTimes;
    String doctorUid;
    String doctorInformation;
    String price;
    FirebaseAuth mFirebaseAuth;
    int myStatus;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> data = new ArrayList<>();
    ArrayList<DoctorDates> myArrayList = new ArrayList<>();
    ArrayAdapter<DoctorDates> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_times_page);

        mFirebaseAuth = FirebaseAuth.getInstance();
        doctorUid = getIntent().getStringExtra("DOCTOR_UID");
        doctorInformation = getIntent().getStringExtra("DOCTOR_INFORMATION");
        price = getIntent().getStringExtra("PRICE");
        listView= findViewById(R.id.listView);
        retrieveData();


    }

    public void createList(){

        adapter = new DoctorTimesAdapter(this,R.layout.doctor_dates_listview,myArrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String doctorDate = myArrayList.get(position).getDate();
                Intent intent = new Intent(getBaseContext(),DoctorHoursPage.class);
                intent.putExtra("DATE_INFORMATION",doctorDate);
                intent.putExtra("DOCTOR_UID",doctorUid);
                intent.putExtra("DOCTOR_INFORMATION",doctorInformation);
                intent.putExtra("PRICE",price);
                startActivity(intent);
            }
        });

    }

    public void retrieveData(){

        Calendar c = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|YYYY");
        String startDate = dt1.format(currentTime);
        c.setTime(currentTime);
        String date;


        Map<String,String> docData = new HashMap<>();

        for(int i=0;i<7;i++){
            DoctorDates temp = new DoctorDates();
            date = dt1.format(c.getTime());
            retrieveOkStatus(date);
            temp.setDate(date);
            temp.setOkStatus(0);
            myArrayList.add(temp);
            c.add(Calendar.DATE,1);
        }


    }

    public void retrieveOkStatus(String doctorDate){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("doctors_dates").document(doctorUid);
        myStatus = 0;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){



                    for(int k= 0; k<myArrayList.size();k++){
                        doctorTimes = document.getString(myArrayList.get(k).getDate());
                        for(int i = 0; i < doctorTimes.length(); i+=2){
                            if(doctorTimes.charAt(i) == 'f' || doctorTimes.charAt(i) == 's'){
                                //Dolu
                            } else {
                                myStatus++;

                            }
                        }
                        DoctorDates temp = new DoctorDates();
                        temp.setDate(myArrayList.get(k).getDate());
                        temp.setOkStatus(myStatus);
                        myArrayList.set(k,temp);
                        myStatus = 0;
                    }

                    createList();

                }
                else {
                }
            }
        });

    }


}
