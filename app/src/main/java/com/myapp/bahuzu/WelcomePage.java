package com.myapp.bahuzu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.DoctorHandles.DoctorRandevuSaatleri;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;
import com.onesignal.OneSignal;

public class WelcomePage extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);


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
                                Intent intent = new Intent(WelcomePage.this, DoctorRandevuSaatleri.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                WelcomePage.this.startActivity(intent);
                                ActivityCompat.finishAffinity(WelcomePage.this);
                            }
                            else{
                                updateNotification(0);
                                Intent intent = new Intent(WelcomePage.this, RandevuAraPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                WelcomePage.this.startActivity(intent);
                                ActivityCompat.finishAffinity(WelcomePage.this);
                            }
                        } else {
                            Toast.makeText(WelcomePage.this, "Sunucularda calisma vardir.", Toast.LENGTH_SHORT).show();
                            mFirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(WelcomePage.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            WelcomePage.this.startActivity(intent);
                            ActivityCompat.finishAffinity(WelcomePage.this);

                        }
                    }
                }
            });
        }

        else {
            Intent intent = new Intent(WelcomePage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            WelcomePage.this.startActivity(intent);
            ActivityCompat.finishAffinity(WelcomePage.this);
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
}
