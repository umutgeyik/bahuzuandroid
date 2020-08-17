package com.myapp.bahuzu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.myapp.bahuzu.DoctorHandles.DoctorRandevuSaatleri;
import com.myapp.bahuzu.UserHandles.RandevuAraPage;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.opengl.GLSurfaceView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.opentok.android.SubscriberKit;

import org.json.JSONException;
import org.json.JSONObject;
import android.provider.Settings;

import java.util.List;


public class StreamingPage extends AppCompatActivity implements Session.SessionListener,PublisherKit.PublisherListener,SubscriberKit.SubscriberListener, EasyPermissions.PermissionCallbacks{

    private static String API_KEY ;
    private static String SESSION_ID ;
    private static String TOKEN ;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private Session mSession;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private String randevuId;
    private String userIdentity;
    private String doctorUid;
    private String roomName;
    private String reasonTag;
    private Boolean onDc = false;
    private String myChoice = null;
    //ImageButton myButton;
    ImageButton swapBttn, microphoneBttn, cameraBttn, disconnectBttn;
    LoadingDialog loadingDialog = new LoadingDialog(StreamingPage.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_page);

        //myButton = (ImageButton) findViewById(R.id.button3);
        //myButton.bringToFront();


        if(savedInstanceState == null){

            loadingDialog.startLoadingDialog();
            swapBttn = findViewById(R.id.swapBttn);
            microphoneBttn = findViewById(R.id.microphoneBttn);
            cameraBttn = findViewById(R.id.cameraBttn);
            disconnectBttn = findViewById(R.id.disconnectBttn);



            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


            randevuId = getIntent().getStringExtra("RANDEVU_ID");
            userIdentity = getIntent().getStringExtra("USER_IDENTITY");
            doctorUid = getIntent().getStringExtra("DOCTOR_UID");
            System.out.println("RANDEVU ID" + randevuId);

            try {
                String s = "CHOICE";
                myChoice = getIntent().getStringExtra(s);

            } catch (Exception e){

            }

            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);


            requestPermissions();
        } else {

            loadingDialog.startLoadingDialog();
            swapBttn = findViewById(R.id.swapBttn);
            microphoneBttn = findViewById(R.id.microphoneBttn);
            cameraBttn = findViewById(R.id.cameraBttn);
            disconnectBttn = findViewById(R.id.disconnectBttn);



            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


            randevuId = getIntent().getStringExtra("RANDEVU_ID");
            userIdentity = getIntent().getStringExtra("USER_IDENTITY");
            doctorUid = getIntent().getStringExtra("DOCTOR_UID");
            System.out.println("RANDEVU ID" + randevuId);


            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);
            requestPermissions();
        }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /*
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && myChoice == null) {
            onDc = true;
            //mSession.disconnect();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && myChoice == null){
            //mSession.disconnect();
            onDc = true;
        }
*/
    }

    public void switchClicked(View target){
        mPublisher.cycleCamera();
    }

    public void cameraOpenClose(View target){
        if(mPublisher.getPublishVideo() == true){
            mPublisher.setPublishVideo(false);
        } else {
            mPublisher.setPublishVideo(true);
        }
    }

    public void microphoneOpenCloseClicked(View target){
        if(mPublisher.getPublishAudio() == true){
            mPublisher.setPublishAudio(false);
        } else {
            mPublisher.setPublishAudio(true);
        }
    }

    public void disconnectFromSessionClicked(View target){
        reasonTag = "1";
        mSession.disconnect();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void hideShowButtons(View target){

        if(disconnectBttn.getVisibility() == target.VISIBLE){
            swapBttn.setVisibility(target.INVISIBLE);
            disconnectBttn.setVisibility(target.INVISIBLE);
            microphoneBttn.setVisibility(target.INVISIBLE);
            cameraBttn.setVisibility(target.INVISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mPublisherViewContainer.getLayoutParams();
            params.setMargins(1,2,3,4);
            mPublisherViewContainer.setLayoutParams(params);

        } else {
            swapBttn.setVisibility(target.VISIBLE);
            disconnectBttn.setVisibility(target.VISIBLE);
            microphoneBttn.setVisibility(target.VISIBLE);
            cameraBttn.setVisibility(target.VISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mPublisherViewContainer.getLayoutParams();
            params.setMargins(1,2,3,250);
            mPublisherViewContainer.setLayoutParams(params);
        }

    }


    @Override
    protected void onPause() {

        Log.d(LOG_TAG, "onPause");

        super.onPause();

        if (mSession != null) {
            mSession.onPause();
        }

    }

    @Override
    protected void onResume() {

        Log.d(LOG_TAG, "onResume");

        super.onResume();

        if (mSession != null) {
            mSession.onResume();
        }
    }


    public void fetchSessionConnectionData() {
        RequestQueue reqQueue = Volley.newRequestQueue(this);
        reqQueue.add(new JsonObjectRequest(Request.Method.GET,
                "https://bahuzu.herokuapp.com" + "/room/:"+ randevuId,
                null, new Response.Listener<JSONObject>() {
            //1_MX40NjY2MTUzMn5-MTU4NjUzMDkwMjI0Nn52Uk9tczJ2U0J1ZUZyeUxQYnR0TlNzZmZ-fg
            @Override
            public void onResponse(JSONObject response) {
                try {
                    API_KEY = response.getString("apiKey");
                    SESSION_ID = response.getString("sessionId");
                    TOKEN = response.getString("token");

                    Log.i(LOG_TAG, "API_KEY: " + API_KEY);
                    Log.i(LOG_TAG, "SESSION_ID: " + SESSION_ID);
                    Log.i(LOG_TAG, "TOKEN: " + TOKEN);

                    mSession = new Session.Builder(StreamingPage.this, API_KEY, SESSION_ID).build();
                    mSession.setSessionListener(StreamingPage.this);
                    mSession.connect(TOKEN);


                } catch (JSONException error) {
                    Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
            }
        }));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout


            // initialize and connect to the session
            fetchSessionConnectionData();

        } else {
            EasyPermissions.requestPermissions(this, "Bu uygulamanın video görüşmesi özelliğini kullanmak için mikrofon ve kamera kullanımına izin vermelisiniz.", RC_VIDEO_APP_PERM, perms);
        }
    }


    // SessionListener methods

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        mPublisherViewContainer.addView(mPublisher.getView());


        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
        loadingDialog.dismissDialog();
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
        if(onDc == true){
            Intent intent = getIntent();
            intent.putExtra("CHOICE","1");
            finish();
            startActivity(intent);
        } else {
            if(userIdentity.equals("user")){
                Intent intent = new Intent(StreamingPage.this, UserRatingPopUp.class);
                intent.putExtra("DOCTOR_UID",doctorUid);
                intent.putExtra("RANDEVU_ID",randevuId);
                StreamingPage.this.startActivity(intent);
            } else {
                Intent intent = new Intent(StreamingPage.this, DoctorRandevuSaatleri.class);
                StreamingPage.this.startActivity(intent);
            }
        }



    }


    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            mSubscriber.setSubscriberListener(this);
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
            //myButton.bringToFront();
            //myButton.setColorFilter(getResources().getColor(R.color.design_default_color_primary));

        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
            reasonTag = "0";
            mSession.disconnect();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }

    // PublisherListener methods

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }


    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        Log.d(LOG_TAG, "onConnected: Subscriber connected. Stream: "+subscriberKit.getStream().getStreamId());
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.d(LOG_TAG, "onDisconnected: Subscriber disconnected. Stream: "+subscriberKit.getStream().getStreamId());
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());

        showOpenTokError(opentokError);
    }

    private void showOpenTokError(OpentokError opentokError) {

        Toast.makeText(this, opentokError.getErrorDomain().name() +": " +opentokError.getMessage() + " Please, see the logcat.", Toast.LENGTH_LONG).show();
        finish();
    }

}
