package com.myapp.bahuzu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.bahuzu.Root.Details;
import com.myapp.bahuzu.Root.RandevuHolder;
import com.myapp.bahuzu.UserHandles.RandevuAlPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SecurePage extends AppCompatActivity implements Serializable {
    private static final double PIC_WID = 360;
    public static Activity myActivity;
    WebView myWebView;
    Details myDetails;

    String date,doctorUid,doctorDate,hour,doctorFullName;
    int hourPosition;

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed",true);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_page);
        myWebView = findViewById(R.id.myWeb);
        myActivity = this;

        myDetails = (Details) getIntent().getSerializableExtra("DetailsObject");
        date = getIntent().getStringExtra("DATE");
        hourPosition = getIntent().getIntExtra("HOURPOSITION",hourPosition);
        doctorUid = getIntent().getStringExtra("DOCTORUID");
        hour = getIntent().getStringExtra("HOUR");
        doctorFullName = getIntent().getStringExtra("DOCTOR_FULLNAME");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*1),(int)(height*.8));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getWindow().setElevation(20);
        }

        connect();

    }

    public void connect() {

        DocumentReference docRef = db.collection("doctors_dates").document(doctorUid);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    doctorDate = document.getString(date);
                    if (doctorDate.charAt(hourPosition * 2) == 'f' || doctorDate.charAt(hourPosition * 2) == 's') {

                        Toast.makeText(SecurePage.this, "Üzgünüz saat musait değildir.", Toast.LENGTH_SHORT).show();
                        finish();
                        // SATILMIS OLDUGU ICIN BASKA SAAT SECME SAYFASINA GERI DONDUR>
                    } else {
                        randevuHolder();

                    }
                }
            }
        });


    }

    public void jsonConnect(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String URL = "https://bahuzuapi.herokuapp.com/cardDetails";

        int foo = Integer.parseInt(myDetails.getPrice());
        foo = foo + 30;

        Map<String,String> params = new HashMap<>();
        params.put("cardHolderName",myDetails.getCardHolderName());
        params.put("cardNumber",myDetails.getCardNumber());
        params.put("cvc",myDetails.getCvc());
        params.put("expirationMonth",myDetails.getExpirationMonth());
        params.put("expirationYear",myDetails.getExpirationYear());
        params.put("price",String.valueOf(foo));
        params.put("userUid",mFirebaseAuth.getCurrentUser().getUid());
        params.put("doctorUid",doctorUid);
        myWebView.loadDataWithBaseURL("", "Lütfen Bekleyiniz...", "text/html", "UTF-8", "");

        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                parameters,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @SuppressLint("JavascriptInterface")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Rest Response", response.toString());
                        try {
                            String statRes = response.get("status").toString();
                            if(statRes.equals("success")) {


                                String data = response.get("threeDSHtmlContent").toString();


                                byte[] actualByte = Base64.getDecoder().decode(data);
                                String actualString = new String(actualByte);

                                myWebView.getSettings().setJavaScriptEnabled(true);
                                //myWebView.getSettings().setLoadWithOverviewMode(true);
                                myWebView.getSettings().setUseWideViewPort(true);
                                myWebView.getSettings().setBuiltInZoomControls(true);
                                myWebView.getSettings().setDisplayZoomControls(false);
                                myWebView.getSettings().setSupportZoom(true);
                                myWebView.setVerticalScrollBarEnabled(true);
                                myWebView.setHorizontalScrollBarEnabled(true);
                                //myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                                myWebView.setScrollbarFadingEnabled(true);




                                //myWebView.getSettings().setJavaScriptEnabled(true);
                                myWebView.addJavascriptInterface(new WebAppInterface(getApplicationContext()), "HTMLOUT");
                                myWebView.addJavascriptInterface(new WebAppInterface(getApplicationContext()), "Android");

                                myWebView.loadDataWithBaseURL("", actualString, "text/html", "UTF-8", "");


                                myWebView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                                    }
                                });


                            } else {
                                Toast.makeText(SecurePage.this, "Girilen bilgilerde eksiklik vardir.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jsonConnect();
                    }
                }
        );

        requestQueue.add(objectRequest);


    }

    public void randevuHolder(){
        RandevuHolder myHolder = new RandevuHolder(date,hourPosition,doctorUid,hour,doctorFullName);

        DocumentReference docRef = db.collection("randevuHolder").document(mFirebaseAuth.getCurrentUser().getUid());
        docRef.set(myHolder);
        jsonConnect();
    }

}
