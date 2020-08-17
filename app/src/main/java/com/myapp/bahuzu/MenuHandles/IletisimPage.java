package com.myapp.bahuzu.MenuHandles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.myapp.bahuzu.R;

public class IletisimPage extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iletisim_page);

    }

    public void facebookClicked(View target){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Bahuzu-101961264830503")));
    }

    public void twitterClicked(View target){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/bahuzuofficial")));
    }

    public void youtubeClicked(View target){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCFtDm1HkVIcHg7H_Gs-l83w")));
    }

    public void instagramClicked(View target){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/bahuzu")));
    }

    public void websiteClicked(View target){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bahuzu.com")));
    }

}
