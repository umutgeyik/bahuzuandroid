package com.myapp.bahuzu.UserHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.Doctor;


import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorInformationPage extends AppCompatActivity {

    String doctorUid;
    String doctorInformation;
    Doctor myDoc;
    TextView fullName, profession, description, experiences,price;
    CircleImageView profileImage;
    RatingBar rating;

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_information_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        doctorUid = getIntent().getStringExtra("DOCTOR_UID");

        fullName = findViewById(R.id.fullNameText);
        rating = findViewById(R.id.ratingText);
        profession = findViewById(R.id.professionText);
        description = findViewById(R.id.descriptionText);
        experiences = findViewById(R.id.experiencesText);
        profileImage = findViewById(R.id.profileImage);
        price = findViewById(R.id.priceText);

        getDoctorData(doctorUid);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

    }

    private void getDoctorData(final String doctorUid) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("doctors").document(doctorUid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){

                    myDoc = new Doctor(document.get("name").toString(),document.get("surname").toString(),doctorUid);
                    myDoc.setDescription(document.get("description").toString());
                    myDoc.setExperiences(document.get("experiences").toString());
                    myDoc.setProfession(document.get("profession").toString());
                    myDoc.setPrice(document.get("price").toString());

                    initView();

                }

            }
        });
    }

    public void initView(){

        int foo = Integer.parseInt(myDoc.getPrice());
        foo = foo + 30;
        fullName.setText(myDoc.getName());
        profession.setText(myDoc.getProfession());
        rating.setRating(5);
        description.setText(myDoc.getDescription());
        experiences.setText(myDoc.getExperiences());
        price.setText( String.valueOf(foo) + " TL (KDV dahil)");

        try {
            mDatabaseRef = FirebaseStorage.getInstance().getReference("media/" + doctorUid + ".jpg");
            mDatabaseRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    System.out.println(uri.toString());

                    Glide.with(getApplicationContext()).load(uri).into(profileImage);
                }
            });

        } catch(Exception e)
        {
            // GG
        }
    }

    public void randevuAraClicked(View target){

        Intent intent = new Intent(getBaseContext(), DoctorTimesPage.class);
        intent.putExtra("DOCTOR_UID",doctorUid);
        intent.putExtra("DOCTOR_INFORMATION",fullName.getText().toString());
        intent.putExtra("PRICE",myDoc.getPrice());
        startActivity(intent);

    }


}
