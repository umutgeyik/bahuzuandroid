package com.myapp.bahuzu.MenuHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapp.bahuzu.DoctorHandles.DoctorRandevuSaatleri;
import com.myapp.bahuzu.LoginPage;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;
import io.opencensus.tags.Tag;

public class PasswordChangePage extends AppCompatActivity {

    EditText oldPasswordText,passwordText,passwordAgainText,phoneText;
    CircleImageView profileImage;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference mDatabaseRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uIdentity;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_INTENT = 2;
    private static final int PICk_IMAGE_REQUEST = 2;
    String PROFILE_IMAGE_RUL = null;
    int TAKE_IMAGE_CODE = 10001;
    private StorageReference mStorage;
    Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change_page);

        setupUI(findViewById(R.id.passwordChangePanel));

        uIdentity = getIntent().getStringExtra("USER_IDENTITY");
        phoneText = findViewById(R.id.phoneText);



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
                    hideSoftKeyboard(PasswordChangePage.this);
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


    public void passwordChangeClicked(View target){

        String email = mAuth.getCurrentUser().getEmail();
        mAuth.sendPasswordResetEmail(email);

        if(uIdentity.equals("doctor")){
            Toast.makeText(this, "Şifre güncelleme maili gönderilmiştir.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PasswordChangePage.this, DoctorRandevuSaatleri.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PasswordChangePage.this.startActivity(intent);
            ActivityCompat.finishAffinity(PasswordChangePage.this);
        } else {
            Toast.makeText(this, "Şifre güncelleme maili gönderilmiştir.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PasswordChangePage.this, RandevuAraPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PasswordChangePage.this.startActivity(intent);
            ActivityCompat.finishAffinity(PasswordChangePage.this);
        }

    }

    public void phoneChangeClicked(View target){

        if(uIdentity.equals("doctor")){
            DocumentReference myReference = db.collection("doctors").document(mAuth.getCurrentUser().getUid());
            myReference.update(

                    "phone",phoneText.getText().toString()

            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        Toast.makeText(PasswordChangePage.this, "Telefonunuz başarıyla güncellenmiitir.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PasswordChangePage.this, DoctorRandevuSaatleri.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PasswordChangePage.this.startActivity(intent);
                        ActivityCompat.finishAffinity(PasswordChangePage.this);
                    }
                    else{
                        Toast.makeText(PasswordChangePage.this, "Hata meydana geldi.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            DocumentReference myReference = db.collection("users").document(mAuth.getCurrentUser().getUid());
            myReference.update(

                    "phone",phoneText.getText().toString()

            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(PasswordChangePage.this, "Telefonunuz başarıyla güncellenmiştir.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PasswordChangePage.this, RandevuAraPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PasswordChangePage.this.startActivity(intent);
                        ActivityCompat.finishAffinity(PasswordChangePage.this);
                    }
                    else{
                        //Fail Message
                        Toast.makeText(PasswordChangePage.this, "Hata meydana geldi.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }




}
