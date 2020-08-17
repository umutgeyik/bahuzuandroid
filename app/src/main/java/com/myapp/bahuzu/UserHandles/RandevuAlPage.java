package com.myapp.bahuzu.UserHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.myapp.bahuzu.R;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.myapp.bahuzu.LoadingDialog;
import com.myapp.bahuzu.Root.Details;
import com.myapp.bahuzu.Root.Randevu;
import com.myapp.bahuzu.SecurePage;
import com.onesignal.OneSignal;
import java.io.Serializable;


public class RandevuAlPage extends AppCompatActivity {

    String date,hour,doctorUid,doctorFullName,userUid,userFullName,priceInfo;
    TextView dateText,hourText,doctorNameText, priceText,kdvPriceText;
    int hourPosition;

    CardForm cardForm;
    RelativeLayout myLayout;

    Button satinAlBttn;

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String doctorDate;
    Randevu myRandevu;
    Boolean button2;
    String externalUserId;
    int kdvPriceInfo;
    int realPrice;
    String finalPrice;
    Boolean buyer = false;
    String notifId;
    LoadingDialog loadingDialog = new LoadingDialog(RandevuAlPage.this);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                satinAlBttn.setEnabled(true);
            }
            if(resultCode == Activity.RESULT_CANCELED){
                satinAlBttn.setEnabled(true);
            }
        } else {
            satinAlBttn.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randevu_al_page);

        loadingDialog.startLoadingDialog();
        doctorUid = getIntent().getStringExtra("DOCTOR_UID");
        date = getIntent().getStringExtra("DATE");
        hour = getIntent().getStringExtra("HOUR");
        doctorFullName = getIntent().getStringExtra("DOCTOR_FULLNAME");
        hourPosition = getIntent().getIntExtra("HOUR_POSITION",hourPosition);
        priceInfo = getIntent().getStringExtra("PRICE");

        realPrice = Integer.parseInt(priceInfo);
        realPrice = realPrice + 30;
        //kdvPriceInfo = ((realPrice * 18) / 100) + realPrice;
        finalPrice = String.valueOf(realPrice);

        mFirebaseAuth = FirebaseAuth.getInstance();
        externalUserId = mFirebaseAuth.getCurrentUser().getUid();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        // OneSignal Initialization
        OneSignal.setExternalUserId(externalUserId);

        dateText = findViewById(R.id.dateText);
        hourText = findViewById(R.id.hourText);
        priceText = findViewById(R.id.priceText);
        doctorNameText = findViewById(R.id.doctorNameText);
        kdvPriceText = findViewById(R.id.kdvPriceText);
        satinAlBttn = findViewById(R.id.satinAlBttn);


        cardForm = findViewById(R.id.card_form);
        myLayout = findViewById(R.id.myRelativeLayout);


        cardForm.getCardEditText().setFieldHint("Kart Numarası");
        cardForm.setCardNumberError("Kart numarası geçersiz.");
        cardForm.getExpirationDateEditText().setFieldHint("Son Kullanma Tarihi");
        cardForm.setCvvError("CVV geçersiz.");
        cardForm.setCardholderNameError("Geçersiz kart sahibi.");
        cardForm.getCardholderNameEditText().setFieldHint("Kart Sahibi Adı ve Soyadı");
        cardForm.cardRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(RandevuAlPage.this);



        doctorNameText.setText(doctorFullName);
        dateText.setText(date);
        hourText.setText(hour);
        kdvPriceText.setText(finalPrice + " TL");

        loadingDialog.dismissDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();

        satinAlBttn.setClickable(true);
        DocumentReference docRef = db.collection("doctors_dates").document(doctorUid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    //Listen fail
                    return;
                }
                if(documentSnapshot != null && documentSnapshot.exists()){
                    String bdoctorDate = documentSnapshot.getString(date);
                    if((bdoctorDate.charAt(hourPosition*2) == 'f' || bdoctorDate.charAt(hourPosition*2) == 's')&&buyer == false){
                        /*
                        Toast.makeText(RandevuAlPage.this, "Üzgünüz, istediğinz saat musait degildir.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RandevuAlPage.this,RandevuAraPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        RandevuAlPage.this.startActivity(intent);
                        ActivityCompat.finishAffinity(RandevuAlPage.this);

                         */
                    }
                } else {
                    //NULL DATA
                }
            }
        });

    }

    public void hideClicked(View target){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myLayout.getWindowToken(), 0);
    }

    public void randevuControl(View target){

        satinAlBttn.setEnabled(false);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myLayout.getWindowToken(), 0);

        if(cardForm.getCardholderName().equals("") ||  cardForm.getCardNumber().equals("") || cardForm.getExpirationMonth().equals("") || cardForm.getExpirationYear().equals("")){
            Toast.makeText(this, "Bütün alanların doldurulması zorunludur.", Toast.LENGTH_SHORT).show();
            satinAlBttn.setEnabled(true);
        }
        else {
            if(cardForm.isValid()){

                Details cardDetails = new Details();
                cardDetails.setCardHolderName(cardForm.getCardholderName());
                cardDetails.setCardNumber(cardForm.getCardNumber());
                cardDetails.setCvc(cardForm.getCvv());
                cardDetails.setExpirationMonth(cardForm.getExpirationMonth());
                cardDetails.setExpirationYear(cardForm.getExpirationYear());
                cardDetails.setPrice(finalPrice);

                loadingDialog.startLoadingDialog();
                DocumentReference docRef = db.collection("doctors_dates").document(doctorUid);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){

                            doctorDate = document.getString(date);

                            if(doctorDate.charAt(hourPosition*2) == 'f' || doctorDate.charAt(hourPosition*2) == 's'){
                                loadingDialog.dismissDialog();
                                Toast.makeText(RandevuAlPage.this, "Üzgünüz, istediğinz saat musait degildir.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RandevuAlPage.this,RandevuAraPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                RandevuAlPage.this.startActivity(intent);
                                ActivityCompat.finishAffinity(RandevuAlPage.this);
                            } else {
                                loadingDialog.dismissDialog();
                                Intent intent = new Intent(RandevuAlPage.this, SecurePage.class);
                                intent.putExtra("DATE",date);
                                intent.putExtra("HOURPOSITION",hourPosition);
                                intent.putExtra("DetailsObject", (Serializable) cardDetails);
                                intent.putExtra("DOCTORUID",doctorUid);
                                intent.putExtra("HOUR",hour);
                                intent.putExtra("DOCTOR_FULLNAME",doctorFullName);
                                RandevuAlPage.this.startActivityForResult(intent,1);
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Girilen bilgileri tekrar kontrol ediniz.", Toast.LENGTH_SHORT).show();
                satinAlBttn.setEnabled(true);
                cardForm.validate();
            }
        }
    }

}
