package com.myapp.bahuzu.UserHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.myapp.bahuzu.R;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapp.bahuzu.MenuHandles.IletisimPage;
import com.myapp.bahuzu.MenuHandles.IptalTalebiPage;
import com.myapp.bahuzu.MainActivity;
import com.myapp.bahuzu.MenuHandles.PasswordChangePage;
import com.myapp.bahuzu.Root.Doctor;
import com.myapp.bahuzu.Root.Randevu;
import com.myapp.bahuzu.Sozlesme;
import com.myapp.bahuzu.StreamingEnterPopUp;
import com.myapp.bahuzu.StreamingPage;
import com.onesignal.OneSignal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class RandevuAraPage extends AppCompatActivity {

    ListView listView;
    AHBottomNavigation bottomNavigation;
    private int cosmioCategory;
    TextView headerText,emptyText,titleText;
    SwipeRefreshLayout swipeRefresh;
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;
    private StorageReference mDatabaseRef;
    ArrayList<Doctor> myList = new ArrayList<>();
    ArrayList<Randevu> randevuList = new ArrayList<>();
    int lk = 0;

    private RecyclerView mRecyclerView;
    private DoctorListAdapter mAdapter;
    private RandevularListAdapter randAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randevu_ara_page);


        try{
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();
        } catch(Exception e){

        }

        mRecyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);
        titleText = findViewById(R.id.titleText);
        //listView = findViewById(R.id.listView);
        mFirebaseAuth = FirebaseAuth.getInstance();
        swipeRefresh = findViewById(R.id.refreshView);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    retrieveRandevuData();
                    swipeRefresh.setRefreshing(false);
            }
        });
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        retrieveDoctorData();
        retrieveRandevuData();
        bottomNavigation=findViewById(R.id.bottom_navigation);
        headerText=findViewById(R.id.headerLabel);
        this.createNavigation();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout_button){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(RandevuAraPage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            RandevuAraPage.this.startActivity(intent);
            ActivityCompat.finishAffinity(RandevuAraPage.this);

        } else if(id == R.id.iletisim_button){
            Intent intent = new Intent(RandevuAraPage.this, IletisimPage.class);
            RandevuAraPage.this.startActivity(intent);

        } else if(id == R.id.password_change_button){
            Intent intent = new Intent(RandevuAraPage.this, PasswordChangePage.class);
            intent.putExtra("USER_IDENTITY","user");
            RandevuAraPage.this.startActivity(intent);
        } else if(id == R.id.iptal_button){
            Intent intent = new Intent(RandevuAraPage.this, IptalTalebiPage.class);
            RandevuAraPage.this.startActivity(intent);
        } else if(id == R.id.sozlesme_button){
            Intent intent = new Intent(RandevuAraPage.this, Sozlesme.class);
            RandevuAraPage.this.startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(), "Başarısız işlem.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_navigation,menu);
        return true;
    }

    private ArrayList<String> getCosmicBodies(){

        ArrayList<String> data = new ArrayList<>();

        data.clear();

        switch(cosmioCategory){
            case 0:
                /*
                titleText.setText("Danışmanlar");
                final ArrayAdapter myArrayAdapter = new RandevuAraAdapter(this,R.layout.user_doctors_listview,myList);
                emptyText.setVisibility(View.INVISIBLE);
                emptyText.setAlpha(0.0f);
                myArrayAdapter.notifyDataSetChanged();
                listView.setAdapter(myArrayAdapter);

                 */

                swipeRefresh.setRefreshing(false);
                swipeRefresh.setEnabled(false);
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.INVISIBLE);
                emptyText.setAlpha(0.0f);

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(this);
                mAdapter = new DoctorListAdapter(myList);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new DoctorListAdapter.OnItemClickListener(){
                    @Override
                    public void onItemClick(int position) {
                        String doctorUid = myList.get(position).getUid();
                        Intent intent = new Intent(getBaseContext(), DoctorInformationPage.class);
                        intent.putExtra("DOCTOR_UID",doctorUid);
                        startActivity(intent);
                    }
                });
                break;
            case 1:
                /*
                titleText.setText("Randevularım");
                final ArrayAdapter randevuAdap = new RandevularAdapter(this,R.layout.randevu_card,randevuList);
                listView.setDivider(null);
                listView.setDividerHeight(50);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setAlpha(1.0f);
                listView.setEmptyView(emptyText);
                listView.setClipToPadding(false);
                randevuAdap.notifyDataSetChanged();
                listView.setAdapter(randevuAdap);

                 */
                if(randevuList.size() == 0) {
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setAlpha(1.0f);
                } else {
                    emptyText.setVisibility(View.INVISIBLE);
                    emptyText.setAlpha(0.0f);
                }
                //mRecyclerView.setVisibility(View.INVISIBLE);

                swipeRefresh.setEnabled(true);
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(this);
                randAdapter = new RandevularListAdapter(randevuList);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(randAdapter);

                randAdapter.setOnItemClickListener(new RandevularListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if(randevuGreenChecker(randevuList.get(position).getDate(),randevuList.get(position).getHour())){
                            Intent intent = new Intent(getBaseContext(), StreamingPage.class);
                            intent.putExtra("USER_IDENTITY","user");
                            intent.putExtra("RANDEVU_ID",randevuList.get(position).getRandevuId());
                            intent.putExtra("DOCTOR_UID",randevuList.get(position).getDoctorUid());
                            startActivity(intent);
                        } else {
                            // Randevuya daha var pop up
                            Intent intent = new Intent(RandevuAraPage.this, StreamingEnterPopUp.class);
                            RandevuAraPage.this.startActivity(intent);
                        }
                    }
                });

                break;
            default:
                break;
        }

        return data;
    }



    private void createNavigation(){
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab1, R.drawable.appointment_icon,R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab2, R.drawable.appointmentsa3x, R.color.color_tab_2);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

        //bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#000000"));
        //bottomNavigation.setInactiveColor(Color.parseColor("#000000"));

        bottomNavigation.setForceTint(true);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        bottomNavigation.setColored(false);

        bottomNavigation.setCurrentItem(0);
        getCosmicBodies();

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                cosmioCategory = position;
                getCosmicBodies();
                return true;
            }
        });

    }

    public void createList(){
        //final ArrayAdapter myArrayAdapter = new RandevuAraAdapter(this,R.layout.user_doctors_listview,myList);
        //listView.setAdapter(myArrayAdapter);
        //mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DoctorListAdapter(myList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DoctorListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                String doctorUid = myList.get(position).getUid();
                Intent intent = new Intent(getBaseContext(), DoctorInformationPage.class);
                intent.putExtra("DOCTOR_UID",doctorUid);
                startActivity(intent);
            }
        });

    }

    public void createRandevuList(){
        if(randevuList.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setAlpha(1.0f);
        } else {
            emptyText.setVisibility(View.INVISIBLE);
            emptyText.setAlpha(0.0f);
        }
        //mRecyclerView.setVisibility(View.INVISIBLE);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        randAdapter = new RandevularListAdapter(randevuList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(randAdapter);

        randAdapter.setOnItemClickListener(new RandevularListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(randevuGreenChecker(randevuList.get(position).getDate(),randevuList.get(position).getHour())){
                    Intent intent = new Intent(getBaseContext(), StreamingPage.class);
                    intent.putExtra("USER_IDENTITY","user");
                    intent.putExtra("RANDEVU_ID",randevuList.get(position).getRandevuId());
                    intent.putExtra("DOCTOR_UID",randevuList.get(position).getDoctorUid());
                    startActivity(intent);
                } else {
                    // Randevuya daha var pop up
                    Intent intent = new Intent(RandevuAraPage.this, StreamingEnterPopUp.class);
                    RandevuAraPage.this.startActivity(intent);
                }
            }
        });
    }



    public void retrieveDoctorData(){

        db.collection("doctors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("doctorRestriction").equals("true")){
                            Doctor myDoc = new Doctor(document.getId(),document.getString("name"));
                            myDoc.setSurname(document.getString("surname"));
                            myDoc.setProfession(document.getString("profession"));
                            myDoc.setPriority(document.getLong("priority").intValue());
                            myList.add(myDoc);
                        } else {
                            // false ise ekleme
                        }

                    }
                    //downloadPhotoUrl();
                    Collections.sort(myList);
                    createList();
                } else {
                    System.out.println("Bos");
                }
            }
        });
    }



    public void retrieveRandevuData(){
        randevuList.clear();
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

                        } else {
                            // Do Nothing
                        }
                    }

                    Collections.sort(randevuList);
                    if(cosmioCategory == 1){
                        if(randevuList.size() == 0) {
                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setAlpha(1.0f);
                        } else {
                            emptyText.setVisibility(View.INVISIBLE);
                            emptyText.setAlpha(0.0f);
                        }
                        createRandevuList();
                    }

                } else {
                    // Boş
                }
            }
        });
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


                if(hour1.compareTo(hour2) <= 0 && hour2.compareTo(hour3) <= 0){
                    // Randevu saati sinirlarindayiz
                    return true;

                } else if(hour2.compareTo(hour1) <= 0){
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

    public boolean randevuGreenChecker(String date, String hour){

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


                if(hour1.compareTo(hour2) <= 0 && hour2.compareTo(hour3) <= 0){
                    // Randevu saati sinirlarindayiz
                    return true;

                } else if(hour2.compareTo(hour1) <= 0){
                    // Randevu saatine daha var
                    return false;

                } else {
                    return false;

                }


            } else if(date2.compareTo(date1) < 0){
                // Ayni gun degil ama tarih daha erken
                return false;
            } else {
                return false;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }




}
