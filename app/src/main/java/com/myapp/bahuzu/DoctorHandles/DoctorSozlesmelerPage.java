package com.myapp.bahuzu.DoctorHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DoctorSozlesmelerPage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_sozlesmeler_page);
    }

    public void onayClicked(View target){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        HashMap<String,String> logs = new HashMap<>();
        logs.put("log","id si bu dökümanın adı olan danışman onay butonuna basmıştır.Bastığı andaki telefonun ya da bilgisayarının tarih ve saati: " + formatter.format(date));

        db.collection("doctors_logs").document(mFirebaseAuth.getCurrentUser().getUid()).set(logs);
        startActivity(new Intent(DoctorSozlesmelerPage.this, DoctorRegisterContPage.class));
    }
}
