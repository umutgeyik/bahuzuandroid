package com.myapp.bahuzu.DoctorHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.myapp.bahuzu.R;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.LoginPage;
import com.myapp.bahuzu.MainActivity;
import com.onesignal.OneSignal;

public class DoctorRegisterContPage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;
    EditText professionText, descriptionText, experiencesText, phoneText, priceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register_cont_page);

        setupUI(findViewById(R.id.doctorRegisterContPanel));

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        mFirebaseAuth = FirebaseAuth.getInstance();
        professionText = findViewById(R.id.professionText);
        descriptionText = findViewById(R.id.descriptionText);
        experiencesText = findViewById(R.id.experienceText);
        phoneText = findViewById(R.id.phoneText);
        priceText = findViewById(R.id.priceText);
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(),0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText )) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(DoctorRegisterContPage.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void registerClicked(View target){

        String notificationId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        String profession = professionText.getText().toString();
        String description = descriptionText.getText().toString();
        String experience = experiencesText.getText().toString();
        String phone = phoneText.getText().toString();
        String price = priceText.getText().toString();

        final String uid = mFirebaseAuth.getCurrentUser().getUid();

        if(profession.isEmpty()){
            professionText.setError("Bu alanın doldurulması zorunludur.");
        } else if(description.isEmpty()){
            descriptionText.setError("Bu alanın doldurulması zorunludur.");
        } else if(experience.isEmpty()){
            experiencesText.setError("Bu alanın doldurulması zorunludur.");
        } else if(phone.isEmpty()){
            phoneText.setError("Bu alanın doldurulması zorunludur.");
        } else if(price.isEmpty()){
            priceText.setError("Bu alanın doldurulması zorunludur.");
        } else {
            DocumentReference myReference = db.collection("doctors").document(uid);

            myReference.update(
                    "description",description,
                    "experiences",experience,
                    "profession",profession,
                    "phone",phone,
                    "notificationId",notificationId,
                    "price",price
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        // LOGIN PAGE GONDER
                        mapBuilder(uid);

                    }
                    else{
                        //SICTIN MESAJI
                    }
                }
            });
        }


    }


    public void mapBuilder(String uid){

        String mySTD  = "f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f";
        Calendar c = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd|MM|YYYY");
        String startDate = dt1.format(currentTime);
        c.setTime(currentTime);
        //c.add(Calendar.DATE,1);
        String date;

        Map<String,String> docData = new HashMap<>();
        docData.put(dt1.format(c.getTime()),mySTD);

        for(int i=0;i<365;i++){

            c.add(Calendar.DATE,1);
            date = dt1.format(c.getTime());
            docData.put(date,mySTD);
        }


        db.collection("doctors_dates").document(uid).set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoctorRegisterContPage.this);
                builder.setTitle("Zaman Çizelgen");
                builder.setMessage("Boş zamanlarını ayarlamak ister misin?");
                builder.setNegativeButton("Geç", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DoctorRegisterContPage.this,DoctorRandevuSaatleri.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DoctorRegisterContPage.this.startActivity(intent);
                        ActivityCompat.finishAffinity(DoctorRegisterContPage.this);
                    }
                });
                builder.setPositiveButton("Şimdi Ayarla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(DoctorRegisterContPage.this,DoctorRegisterTimePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DoctorRegisterContPage.this.startActivity(intent);
                        ActivityCompat.finishAffinity(DoctorRegisterContPage.this);

                    }
                });
                builder.show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorRegisterContPage.this, "Bilgilerini kayıt ederken bir sorun yaşadık!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
