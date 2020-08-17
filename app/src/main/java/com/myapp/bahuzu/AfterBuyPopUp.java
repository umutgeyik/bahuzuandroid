package com.myapp.bahuzu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.myapp.bahuzu.UserHandles.RandevuAraPage;

public class AfterBuyPopUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_buy_pop_up);

    }

    public void bitirClicked(View target){
        Intent intent = new Intent(AfterBuyPopUp.this, RandevuAraPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AfterBuyPopUp.this.startActivity(intent);
        ActivityCompat.finishAffinity(AfterBuyPopUp.this);
    }
}
