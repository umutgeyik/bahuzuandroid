package com.myapp.bahuzu.DoctorHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;


import android.app.Activity;
import android.content.Intent;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.LoginPage;
import com.myapp.bahuzu.R;
import com.myapp.bahuzu.Root.Doctor;
import com.myapp.bahuzu.Root.Identity;
import com.myapp.bahuzu.Sozlesme;

public class DoctorRegisterPage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;
    EditText nameText, surnameText, passwordText, passwordAgainText, emailText;
    TextView sozlesme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register_page);
        setupUI(findViewById(R.id.doctorRegisterPanel));

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        passwordAgainText = findViewById(R.id.passwordAgainText);
        nameText = findViewById(R.id.nameText);
        surnameText = findViewById(R.id.surnameText);
        sozlesme = findViewById(R.id.sozlemeText);

        String data="Kaydet butonuna basarak kullanıcı sözleşmesini kabul etmiş olursunuz.";
        SpannableString content = new SpannableString(data);
        content.setSpan(new UnderlineSpan(), 24, 46, 0);
        sozlesme.setText(content);
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
                    hideSoftKeyboard(DoctorRegisterPage.this);
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

    public void writeNewUserIdentity(Doctor myDoctor){
        Identity myIdentity = new Identity("doctor");
        db.collection("userIdentity").document(myDoctor.getUid()).set(myIdentity).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorRegisterPage.this, "Bilgilerini kayıt ederken bir sorun yaşadık!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void sozlesmeClicked(View target){
        Intent intent = new Intent(DoctorRegisterPage.this, Sozlesme.class);
        startActivity(intent);
    }

    public void writeNewUser(final Doctor myDoctor){
        db.collection("doctors").document(myDoctor.getUid()).set(myDoctor).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                writeNewUserIdentity(myDoctor);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorRegisterPage.this, "Bilgilerini kayıt ederken bir sorun yaşadık!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void continueClicked(View target){

        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordAgain = passwordAgainText.getText().toString();
        final String name = nameText.getText().toString();
        final String surname = surnameText.getText().toString();


        if(email.isEmpty()){
            emailText.setError("Lütfen bu alanı doldurunuz!");
            emailText.requestFocus();
        }
        else if(password.isEmpty()){
            passwordText.setError("Lütfen bu alanı doldurunuz!");
        }
        else if(passwordAgain.isEmpty()){
            passwordAgainText.setError("Lutfen bu alanı doldurunuz!");
        }
        else if(name.isEmpty()){
            nameText.setError("Lütfen bu alanı doldurunuz!");
        }
        else if(surname.isEmpty()){
            surnameText.setError("Lütfen bu alanı doldurunuz!");
        }
        else if(!password.equals(passwordAgain)) {
            Toast.makeText(DoctorRegisterPage.this, "Şifreler eşleşmiyor!!", Toast.LENGTH_SHORT).show();
        }
        else if(!(email.isEmpty())&&!password.isEmpty()){
            mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(DoctorRegisterPage.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(DoctorRegisterPage.this, "Kayıt başarısız. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String uid = mFirebaseAuth.getCurrentUser().getUid();
                        Doctor myDoctor = new Doctor(name,surname,uid);

                        writeNewUser(myDoctor);
                        startActivity(new Intent(DoctorRegisterPage.this, DoctorSozlesmelerPage.class));
                    }
                }
            });
        }
        else {
            Toast.makeText(DoctorRegisterPage.this, "Hata meydana geldi.", Toast.LENGTH_SHORT).show();
        }
    }


}
