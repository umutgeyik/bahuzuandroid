package com.myapp.bahuzu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyLemubit")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Randevunuz Hakkinda...")
                .setContentText("Randevunuzun baslamasina 10 dakika kalmıştır. Lutfen 10 dakika sonra uygulamaya giriş yaparak, randevu odası yeşil renk olunca tıklayarak seansınıza başlayınız.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200,builder.build());
    }
}
