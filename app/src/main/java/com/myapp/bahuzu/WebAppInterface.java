package com.myapp.bahuzu;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.Root.Randevu;
import com.myapp.bahuzu.UserHandles.DoctorHoursPage;
import com.myapp.bahuzu.UserHandles.RandevuAlPage;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WebAppInterface extends RandevuAlPage {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        externalUserId = mFirebaseAuth.getCurrentUser().getUid();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        // OneSignal Initialization
        OneSignal.setExternalUserId(externalUserId);
    }

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userFullName,userUid,date,doctorUid,doctorDate,doctorFullName,hour;
    int hourPosition;

    String notifId;
    String externalUserId;
    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {

   /*
        System.out.println(toast);
        if(toast.equals("0")){
            randevuDetailsGetter();

            Intent i = new Intent(mContext, RandevuAraPage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);


        } else {
            SecurePage.myActivity.finish();
        }
        */
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void processHTML(String html)
    {

        //Log.e("result",html);
        String den = Html.fromHtml(html).toString();
        String results[] = den.split("\n");

        if(results[0].equals("Basarisiz")){
            SecurePage.myActivity.finish();
        } else if(results[0].equals("Basarili")){
            randevuDetailsGetter();
        }
    }



    public void randevuDetailsGetter(){

        DocumentReference docRef = db.collection("randevuHolder").document(mFirebaseAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    date = document.getString("date");
                    doctorUid = document.getString("doctorUid");
                    hourPosition = document.getLong("hourPosition").intValue();
                    hour = document.getString("hour");
                    doctorFullName = document.getString("doctorFullName");
                    updateDB();
                } else {
                    // HATA
                }
            }
        });
    }

    public void updateDB(){
        DocumentReference docRef = db.collection("doctors_dates").document(doctorUid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    doctorDate = document.getString(date);
                    if(doctorDate.charAt(hourPosition*2) == 'f' || doctorDate.charAt(hourPosition*2) == 's'){

                        SecurePage.myActivity.finish();
                        // SATILMIS VE ODEME ALINDI SIKINTI
                    } else {
                        char[] ch = doctorDate.toCharArray();
                        ch[hourPosition * 2] = 's';
                        doctorDate = String.valueOf(ch);
                        docRef.update(date,doctorDate
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    userGetter();

                                }
                                else{
                                    //SICTIN MESAJI
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void userGetter() {


        DocumentReference docRef = db.collection("users").document(mFirebaseAuth.getCurrentUser().getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){

                    userFullName = document.getString("name");
                    userUid = mFirebaseAuth.getCurrentUser().getUid();
                    createRandevu();

                }
            }
        });
    }

    public void createRandevu(){
        Randevu myRandevu = new Randevu(doctorUid,userUid, date,hour,doctorFullName,userFullName);



        // Add a new document with a generated ID
        DocumentReference docRef = db.collection("randevular").document();
        String randevuId = docRef.getId();
        myRandevu.setRandevuId(randevuId);
        docRef.set(myRandevu);
        sendNotifToDoc();
        setNotification();


        Intent intent = new Intent(mContext,AfterBuyPopUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        ActivityCompat.finishAffinity(SecurePage.myActivity);

    }




    private void sendNotifToDoc() {

        DocumentReference docRef = db.collection("doctors").document(doctorUid);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){

                    notifId = document.getString("notificationId");
                    System.out.println("NOTIF ID " + notifId);
                    try {
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+ date +" "+ hour + " için randevu satın alınmıştır. Lütfen bu tarih ve saatte uygulamadaki randevularım sekmesinden randevuya katılınız.'}, 'include_player_ids': ['" + notifId + "']}"),
                                new OneSignal.PostNotificationResponseHandler() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                                    }

                                    @Override
                                    public void onFailure(JSONObject response) {
                                        Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void setNotification() {


        createNotificationChannel();
        DateFormat format = new SimpleDateFormat("dd|MM|yyyy HH:mm");


        Date date1 = null;
        try {
            date1 = format.parse(date + " " + hour);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        cal.add(Calendar.MINUTE, -10);


        //createNotificationChannel();

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,0,intent,0);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);


    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ReminderChannel";
            String description = "Channel for UG";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyLemubit",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }



}