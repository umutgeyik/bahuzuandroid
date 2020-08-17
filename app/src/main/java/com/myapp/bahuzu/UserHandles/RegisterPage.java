package com.myapp.bahuzu.UserHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.myapp.bahuzu.R;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.myapp.bahuzu.LoadingDialog;
import com.myapp.bahuzu.Root.Identity;
import com.myapp.bahuzu.Root.User;
import com.myapp.bahuzu.Sozlesme;
import com.onesignal.OneSignal;

import java.util.ArrayList;

public class RegisterPage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth;
    EditText nameText, surnameText, passwordText, passwordAgainText, emailText, phoneText;
    LoadingDialog loadingDialog = new LoadingDialog(RegisterPage.this);
    TextView sozlesme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        setupUI(findViewById(R.id.registerPanel));

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        passwordAgainText = findViewById(R.id.passwordAgainText);
        nameText = findViewById(R.id.nameText);
        surnameText = findViewById(R.id.surnameText);
        phoneText = findViewById(R.id.phoneText);
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
                    hideSoftKeyboard(RegisterPage.this);
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

    public void sozlesmeClicked(View target){
        Intent intent = new Intent(RegisterPage.this, Sozlesme.class);
        startActivity(intent);
    }

    public void writeNewUserIdentity(User myUser){
        Identity myIdentity = new Identity("user");
        db.collection("userIdentity").document(myUser.getUid()).set(myIdentity).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterPage.this, "Bilgilerini kaydederken hata meydana geldi!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void writeNewUser(final User myUser){

        // Add a new document with a generated ID
        db.collection("users").document(myUser.getUid()).set(myUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                writeNewUserIdentity(myUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterPage.this, "Bilgilerini kaydederken hata meydana geldi!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerClicked(View target){

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        final String notificationId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();

        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordAgain = passwordAgainText.getText().toString();
        final String name = nameText.getText().toString();
        final String surname = surnameText.getText().toString();
        final String phone = phoneText.getText().toString();



        if(email.isEmpty()){
            emailText.setError("Lütfen bu alanı doldurunuz!");
            emailText.requestFocus();
        }
        else if(password.isEmpty()){
            passwordText.setError("Lütfen bu alanı doldurunuz!");
        }
        else if(name.isEmpty()){
            nameText.setError("Lütfen bu alanı doldurunuz!");
        }
        else if(surname.isEmpty()){
            surnameText.setError("Lütfen bu alanı doldurunuz!");
        }
        else if(passwordAgain.isEmpty()){
            passwordAgainText.setError("Lütfen bu alanı doldurunuz!");
        }
        else if(!password.equals(passwordAgain)) {

            Toast.makeText(RegisterPage.this, "Şifreler eşleşmiyor!!", Toast.LENGTH_SHORT).show();
        }
        else if(!(email.isEmpty())&&!password.isEmpty()){
            loadingDialog.startLoadingDialog();
            mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(RegisterPage.this, "Kayıt işlemi başarısız. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                    else{
                        String uid = mFirebaseAuth.getCurrentUser().getUid();
                        User myUser = new User(name,surname,uid);
                        myUser.setNotificationId(notificationId);
                        if(!phone.isEmpty()){
                            myUser.setPhone(phone);
                        }
                        writeNewUser(myUser);
                        Intent intent = new Intent(RegisterPage.this, RandevuAraPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        RegisterPage.this.startActivity(intent);
                        ActivityCompat.finishAffinity(RegisterPage.this);

                    }
                }
            });
        }
        else {
            Toast.makeText(RegisterPage.this, "Hata meydana geldi.", Toast.LENGTH_SHORT).show();
        }
    }


}
