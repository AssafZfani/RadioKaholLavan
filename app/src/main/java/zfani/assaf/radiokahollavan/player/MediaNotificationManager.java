package zfani.assaf.radiokahollavan.player;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.text.HtmlCompat;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.ui.activities.MainActivity;

public class MediaNotificationManager {

    public static final int NOTIFICATION_ID = 555;
    private static final String CHANNEL_ID = "player_service";
    private final RadioService service;
    private final NotificationManagerCompat notificationManager;

    public MediaNotificationManager(RadioService service) {
        this.service = service;
        notificationManager = NotificationManagerCompat.from(service);
    }

    public void startNotify(RadioManager.PlaybackStatus playbackStatus) {
        Bitmap largeIcon = BitmapFactory.decodeResource(service.getResources(), R.drawable.notification);
        int icon = R.drawable.ic_pause_notification;

        Intent playbackAction = new Intent(service, RadioService.class);
        playbackAction.setAction(RadioService.ACTION_PAUSE);
        PendingIntent action = PendingIntent.getService(service, 1, playbackAction, FLAG_IMMUTABLE);

        if (playbackStatus.equals(RadioManager.PlaybackStatus.PAUSED) || playbackStatus.equals(RadioManager.PlaybackStatus.IDLE)) {
            icon = R.drawable.ic_play_notification;
            playbackAction.setAction(RadioService.ACTION_PLAY);
            action = PendingIntent.getService(service, 2, playbackAction, FLAG_IMMUTABLE);
        }

        Intent stopIntent = new Intent(service, RadioService.class);
        stopIntent.setAction(RadioService.ACTION_STOP);
        PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, FLAG_IMMUTABLE);

        Intent intent = new Intent(service, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, FLAG_IMMUTABLE);

        notificationManager.cancel(NOTIFICATION_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, service.getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, CHANNEL_ID)
                .setAutoCancel(false)
                .setContentTitle(App.songTitle.getValue())
                .setSubText(HtmlCompat.fromHtml("<font color=\"" + service.getResources().getColor(R.color.colorPrimary) + "\">" + service.getString(R.string.music_is_the_answer) + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_music)
                .addAction(icon, "pause", action)
                .addAction(R.drawable.ic_stop_notification, "stop", stopAction)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1));
        service.startForeground(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotify() {
        service.stopForeground(true);
    }
}
