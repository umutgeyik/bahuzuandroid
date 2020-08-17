package com.myapp.bahuzu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapp.bahuzu.Root.Randevu;
import com.myapp.bahuzu.Root.Rating;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;

public class UserRatingPopUp extends AppCompatActivity {

    TextView skipText,rateText;
    RatingBar ratingBar;
    private String doctorUid;
    private String randevuId;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LoadingDialog loadingDialog = new LoadingDialog(UserRatingPopUp.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rating_pop_up);
        mFirebaseAuth = FirebaseAuth.getInstance();
        loadingDialog.startLoadingDialog();

        skipText = findViewById(R.id.skipText);
        rateText = findViewById(R.id.rateText);
        ratingBar = findViewById(R.id.ratingBar);

        doctorUid = getIntent().getStringExtra("DOCTOR_UID");
        randevuId = getIntent().getStringExtra("RANDEVU_ID");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().setLayout((int)(width*.5),(int)(height*.51));
        } else {
            getWindow().setLayout((int)(width*.8),(int)(height*.31));
        }




        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
        loadingDialog.dismissDialog();

        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRatingPopUp.this, RandevuAraPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                UserRatingPopUp.this.startActivity(intent);
                ActivityCompat.finishAffinity(UserRatingPopUp.this);
            }
        });
        rateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rating userRating = new Rating(doctorUid,mFirebaseAuth.getCurrentUser().getUid(),String.valueOf(ratingBar.getRating()));
                db.collection("ratings").document(randevuId).set(userRating);
                Intent intent = new Intent(UserRatingPopUp.this, RandevuAraPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                UserRatingPopUp.this.startActivity(intent);
                ActivityCompat.finishAffinity(UserRatingPopUp.this);
            }
        });

    }
}
