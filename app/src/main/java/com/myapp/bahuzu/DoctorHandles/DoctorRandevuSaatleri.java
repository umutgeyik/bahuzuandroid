package com.myapp.bahuzu.DoctorHandles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapp.bahuzu.MainActivity;
import com.myapp.bahuzu.MenuHandles.DoctorInformationUpdatePage;
import com.myapp.bahuzu.MenuHandles.DoctorOtherSozlesmeler;
import com.myapp.bahuzu.MenuHandles.DoktorIptalPage;
import com.myapp.bahuzu.MenuHandles.IletisimPage;
import com.myapp.bahuzu.MenuHandles.PasswordChangePage;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.DoctorDates;
import com.myapp.bahuzu.Root.DoctorRandevu;
import com.myapp.bahuzu.Root.Randevu;
import com.myapp.bahuzu.Sozlesme;
import com.myapp.bahuzu.StreamingEnterPopUp;
import com.myapp.bahuzu.StreamingPage;
import com.myapp.bahuzu.UserHandles.RandevularAdapter;
import com.onesignal.OneSignal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DoctorRandevuSaatleri extends AppCompatActivity {

    ListView listView;
    ListView listViewRandevu;
    AHBottomNavigation bottomNavigation;
    private int cosmioCategory;
    TextView headerText, emptyText, mainText;
    String doctorTimes;
    int myStatus;
    ArrayAdapter<String> adapter;
    ArrayList<String> data = new ArrayList<>();
    SwipeRefreshLayout docRefresh;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;
    ArrayList<DoctorDates> myArrayList = new ArrayList<>();
    ArrayList<Randevu> randevuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_randevu_saatleri);

        emptyText = findViewById(R.id.emptyText);
        mainText = findViewById(R.id.mainText);

        docRefresh = findViewById(R.id.doctorRefreshView);
        docRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveRandevuData();
                docRefresh.setRefreshing(false);
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        String notificationId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        System.out.println("NOTIFICATION ID " + notificationId);

        DocumentReference myReference = db.collection("doctors").document(mFirebaseAuth.getCurrentUser().getUid());

        myReference.update("notificationId",notificationId);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        retrieveRandevuData();
        retrieveData();

        this.initiliazeVies();
        this.createNavigation();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout_button){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(DoctorRandevuSaatleri.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            DoctorRandevuSaatleri.this.startActivity(intent);
            ActivityCompat.finishAffinity(DoctorRandevuSaatleri.this);

        }  else if(id == R.id.iletisim_button){
            Intent intent = new Intent(DoctorRandevuSaatleri.this, IletisimPage.class);
            DoctorRandevuSaatleri.this.startActivity(intent);

        } else if(id == R.id.password_change_button){
            Intent intent = new Intent(DoctorRandevuSaatleri.this, PasswordChangePage.class);
            intent.putExtra("USER_IDENTITY","doctor");
            DoctorRandevuSaatleri.this.startActivity(intent);
        } else if(id == R.id.iptal_button){
            Intent intent = new Intent(DoctorRandevuSaatleri.this, DoktorIptalPage.class);
            DoctorRandevuSaatleri.this.startActivity(intent);
        }  else if(id == R.id.profil_info_change){
            Intent intent = new Intent(DoctorRandevuSaatleri.this, DoctorInformationUpdatePage.class);
            DoctorRandevuSaatleri.this.startActivity(intent);
        }else if(id == R.id.sozlesme_button){
            Intent intent = new Intent(DoctorRandevuSaatleri.this, Sozlesme.class);
            DoctorRandevuSaatleri.this.startActivity(intent);
        }
        else if(id == R.id.other_sozleme_button){
            Intent intent = new Intent(DoctorRandevuSaatleri.this, DoctorOtherSozlesmeler.class);
            DoctorRandevuSaatleri.this.startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(), "Başarısız İşlem.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_navigation_doctor,menu);
        return true;
    }

    private ArrayList<String> getCosmicBodies(){


        data.clear();


        switch(cosmioCategory){
            case 0:

                docRefresh.setRefreshing(false);
                docRefresh.setEnabled(false);
                doctorDatesRetrieve();
                final ArrayAdapter adapter = new DoctorRandevuSaatleriAdapter(this,R.layout.doctor_dates_listview,myArrayList);
                emptyText.setVisibility(View.INVISIBLE);
                mainText.setText("Saatleri Düzenle");
                listView.setAdapter(adapter);

                break;
            case 1:

                docRefresh.setEnabled(true);
                final ArrayAdapter randevuAdap = new RandevularAdapter(this,R.layout.randevu_card_doctor,randevuList);
                listView.setDivider(null);
                listView.setDividerHeight(50);
                listView.setClipToPadding(false);
                mainText.setText("Randevularım");
                emptyText.setVisibility(View.VISIBLE);
                listView.setEmptyView(emptyText);
                listView.setAdapter(randevuAdap);
                break;
            default:
                break;
        }

        return data;
    }



    private void createNavigation(){
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Saatleri Düzenle", R.drawable.appointment_icon,R.color.color_tab1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab2, R.drawable.appointmentsa3x, R.color.color_tab2);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

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

    private void initiliazeVies(){
        bottomNavigation=findViewById(R.id.bottom_navigation);
        headerText=findViewById(R.id.headerLabel);
        listView=findViewById(R.id.listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(bottomNavigation.getCurrentItem() == 0) {
                    String doctorDate = getCosmicBodies().get(position);
                    Intent intent = new Intent(getBaseContext(),TimePickerPage.class);
                    intent.putExtra("TIME_INFORMATION",doctorDate);
                    startActivity(intent);

                } else {

                    if(randevuGreenChecker(randevuList.get(position).getDate(),randevuList.get(position).getHour())){
                        Intent intent = new Intent(getBaseContext(), StreamingPage.class);
                        intent.putExtra("USER_IDENTITY","doctor");
                        intent.putExtra("RANDEVU_ID",randevuList.get(position).getRandevuId());
                        intent.putExtra("DOCTOR_UID",randevuList.get(position).getDoctorUid());
                        startActivity(intent);
                    } else {
                        // Randevuya daha var pop up
                        Intent intent = new Intent(DoctorRandevuSaatleri.this, StreamingEnterPopUp.class);
                        DoctorRandevuSaatleri.this.startActivity(intent);

                    }
                }

            }
        });


    }


    public void doctorDatesRetrieve(){

        Calendar c = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|YYYY");
        String startDate = dt1.format(currentTime);
        c.setTime(currentTime);
        //  c.add(Calendar.DATE,1);
        String date;

        Map<String,String> docData = new HashMap<>();
        data.add(dt1.format(c.getTime()));

        for(int i=0;i<365;i++){

            c.add(Calendar.DATE,1);
            date = dt1.format(c.getTime());
            data.add(date);
        }
    }

    public void retrieveRandevuData(){

        randevuList.clear();
        db.collection("randevular").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getString("doctorUid").equals(mFirebaseAuth.getCurrentUser().getUid()) ){
                            Randevu myDoc = new Randevu(document.getString("doctorUid"),document.getString("userUid"),document.getString("date"),document.getString("hour"),document.getString("userFullName"),document.getString("userFullName"));
                            myDoc.setRandevuId(document.getId());
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
                    System.out.println("Bos");
                }
            }
        });

    }

    private void createRandevuList() {
        if(randevuList.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setAlpha(1.0f);
        } else {
            emptyText.setVisibility(View.INVISIBLE);
            emptyText.setAlpha(0.0f);
        }

        final ArrayAdapter randevuAdap = new RandevularAdapter(this,R.layout.randevu_card_doctor,randevuList);
        listView.setDivider(null);
        listView.setDividerHeight(50);
        listView.setClipToPadding(false);
        mainText.setText("Randevularım");
        emptyText.setVisibility(View.VISIBLE);
        listView.setEmptyView(emptyText);
        listView.setAdapter(randevuAdap);
    }

    public void retrieveData(){

        Calendar c = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|YYYY");
        String startDate = dt1.format(currentTime);
        c.setTime(currentTime);
        String date;


        Map<String,String> docData = new HashMap<>();

        for(int i=0;i<100;i++){
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
        DocumentReference docRef = db.collection("doctors_dates").document(mFirebaseAuth.getCurrentUser().getUid());
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

                    getCosmicBodies();

                }
                else {
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
