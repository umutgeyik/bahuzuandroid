package com.myapp.bahuzu.MenuHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.myapp.bahuzu.R;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.myapp.bahuzu.Root.Doctor;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorInformationUpdatePage extends AppCompatActivity {

    EditText priceText,descriptionText,experienceText,professionText;
    CircleImageView profileImage;
    private FirebaseAuth mAuth;
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
    Doctor myDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_information_update_page);

        setupUI(findViewById(R.id.doctorInfoUpdatePanel));

        profileImage = findViewById(R.id.profile_image);
        priceText = findViewById(R.id.priceText);
        descriptionText = findViewById(R.id.descriptionText);
        experienceText = findViewById(R.id.experienceText);
        professionText = findViewById(R.id.professionText);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseStorage.getInstance().getReference("media/" + mAuth.getCurrentUser().getUid() + ".jpg");

        mStorage = FirebaseStorage.getInstance().getReference();


        mDatabaseRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(profileImage);
            }
        });

        getDoctorData();
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
                    hideSoftKeyboard(DoctorInformationUpdatePage.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference filepath = mStorage.child("media").child(uid + ".jpg");

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    getDownloadUrl(filepath);
                }
            });
        }
        else if(requestCode == PICk_IMAGE_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();
            profileImage.setImageURI(mImageUri);

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference filepath = mStorage.child("media").child(uid + ".jpg");

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    getDownloadUrl(filepath);
                }
            });
        }
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfile(uri);
            }
        });
    }

    public void setUserProfile(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public void handleImageClick(View target){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICk_IMAGE_REQUEST);
    }

    public void getDoctorData(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("doctors").document(mAuth.getCurrentUser().getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    professionText.setText(document.get("profession").toString());
                    descriptionText.setText(document.get("description").toString());
                    experienceText.setText(document.get("experiences").toString());
                    priceText.setText(document.get("price").toString());
                }

            }
        });
    }

    public void updateClicked(View target){
        String profession = professionText.getText().toString();
        String description = descriptionText.getText().toString();
        String experience = experienceText.getText().toString();
        String price = priceText.getText().toString();

        final String uid = mAuth.getCurrentUser().getUid();

        if(profession.isEmpty()){
            professionText.setError("Bu alanın doldurulması zorunludur.");
        } else if(description.isEmpty()){
            descriptionText.setError("Bu alanın doldurulması zorunludur.");
        } else if(experience.isEmpty()){
            experienceText.setError("Bu alanın doldurulması zorunludur.");
        }  else if(price.isEmpty()){
            priceText.setError("Bu alanın doldurulması zorunludur.");
        } else {
            DocumentReference myReference = db.collection("doctors").document(uid);

            myReference.update(
                    "description",description,
                    "experiences",experience,
                    "profession",profession,
                    "price",price
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DoctorInformationUpdatePage.this, "Başarıyla Güncellenmiştir.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}
