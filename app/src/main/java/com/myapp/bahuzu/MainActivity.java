package com.myapp.bahuzu;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.DoctorHandles.DoctorRandevuSaatleri;
import com.myapp.bahuzu.DoctorHandles.DoctorRegisterPage;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;
import com.myapp.bahuzu.UserHandles.RegisterPage;
import com.onesignal.OneSignal;


public class MainActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(user != null){

            // OneSignal Initialization
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();



            DocumentReference docRef = db.collection("userIdentity").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            if(document.getData().containsValue("doctor")){
                                updateNotification(1);
                                Intent intent = new Intent(MainActivity.this, DoctorRandevuSaatleri.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                MainActivity.this.startActivity(intent);
                                ActivityCompat.finishAffinity(MainActivity.this);
                            }
                            else{
                                updateNotification(0);
                                Intent intent = new Intent(MainActivity.this, RandevuAraPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                MainActivity.this.startActivity(intent);
                                ActivityCompat.finishAffinity(MainActivity.this);
                            }
                        }
                    }
                }
            });
        }
    }

    public void updateNotification(Integer a){

        String notificationId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        mFirebaseAuth = FirebaseAuth.getInstance();

        if(a == 1){
            DocumentReference myReference = db.collection("doctors").document(mFirebaseAuth.getCurrentUser().getUid());
            myReference.update("notificationId",notificationId);

        } else {
            DocumentReference myReference = db.collection("users").document(mFirebaseAuth.getCurrentUser().getUid());
            myReference.update("notificationId",notificationId);
        }
    }

    public void myClickHandler(View target){
        Intent intent = new Intent(this, RegisterPage.class);
        MainActivity.this.startActivity(intent);
    }

    public void loginClicked(View target){
        Intent intent = new Intent(this,LoginPage.class);
        MainActivity.this.startActivity(intent);
    }

    public void doctorRegisterClicker(View target){
        Intent intent = new Intent(this, DoctorRegisterPage.class);
        MainActivity.this.startActivity(intent);
    }

}
