package com.myapp.bahuzu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.DoctorHandles.DoctorRandevuSaatleri;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;

public class LoginPage extends AppCompatActivity {

    EditText emailText,passwordText;
    TextView passwordForget;
    String email,password;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LoadingDialog loadingDialog = new LoadingDialog(LoginPage.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        setupUI(findViewById(R.id.loginMainPanel));
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        passwordForget = findViewById(R.id.passwordForget);

        passwordForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, PasswordForgetPage.class));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //loadingDialog.dismissDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //loadingDialog.dismissDialog();
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
                    hideSoftKeyboard(LoginPage.this);
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

    public void loginClicked(View target){


        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Bütün alanların doldurulması zorunludur.", Toast.LENGTH_SHORT).show();
        } else {

            loadingDialog.startLoadingDialog();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();

                                DocumentReference docRef = db.collection("userIdentity").document(user.getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document.exists()){
                                                if(document.getData().containsValue("doctor")){
                                                    Intent intent = new Intent(LoginPage.this, DoctorRandevuSaatleri.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    loadingDialog.dismissDialog();
                                                    LoginPage.this.startActivity(intent);
                                                    ActivityCompat.finishAffinity(LoginPage.this);
                                                }
                                                else{
                                                    Intent intent = new Intent(LoginPage.this, RandevuAraPage.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    loadingDialog.dismissDialog();
                                                    LoginPage.this.startActivity(intent);
                                                    ActivityCompat.finishAffinity(LoginPage.this);
                                                }
                                            }
                                        }
                                    }


                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginPage.this, "Sunucuya iletişim sağlanamıyor. Lütfen daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                
                                //Update UI here...
                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(LoginPage.this, "Kullanıcı adı veya şifre geçersiz.",Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);

                                // Update UI here..
                            }

                            // ...
                        }
                    });

        }
    }
}
