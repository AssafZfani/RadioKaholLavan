package zfani.assaf.radiokahollavan.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.icy.IcyInfo;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;

public class RadioService extends Service implements Player.EventListener {

    public static final String ACTION_PLAY = "zfani.assaf.radiokahollavan.player.ACTION_PLAY";
    public static final String ACTION_PAUSE = "zfani.assaf.radiokahollavan.player.ACTION_PAUSE";
    public static final String ACTION_STOP = "zfani.assaf.radiokahollavan.player.ACTION_STOP";
    private static final String streamingUrl = "https://radiokahollavan.radioca.st/stream";
    private final IBinder iBinder = new LocalBinder();
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    private boolean onGoingCall = false;
    private TelephonyManager telephonyManager;
    private WifiManager.WifiLock wifiLock;
    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }
    };
    private MediaNotificationManager notificationManager;
    private final MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPause() {
            super.onPause();
            pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            stop();
            notificationManager.cancelNotify();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            play();
        }
    };
    private RadioManager.PlaybackStatus status;
    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING) {
                if (isNotPlaying()) return;
                onGoingCall = true;
                stop();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (!onGoingCall) return;
                onGoingCall = false;
                Executors.newSingleThreadScheduledExecutor().schedule(() -> play(), 1, TimeUnit.SECONDS);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onGoingCall = false;
        notificationManager = new MediaNotificationManager(this);
        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, getPackageName());
        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(mediasSessionCallback);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        exoPlayer = new SimpleExoPlayer.Builder(this)
                .setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(this))
                .setTrackSelector(new DefaultTrackSelector(this, new AdaptiveTrackSelection.Factory()))
                .build();
        exoPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MUSIC).build(), true);
        exoPlayer.addListener(this);
        exoPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onMetadata(EventTime eventTime, Metadata metadata) {
                IcyInfo info = (IcyInfo) metadata.get(0);
                App.songTitle.setValue(info.title);
                mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, info.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, getString(R.string.app_name))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Radio Kahol Lavan")
                        .build());
                notificationManager.startNotify(status);
            }
        });
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        status = RadioManager.PlaybackStatus.PLAYING;
        play();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return START_NOT_STICKY;
        }
        if (action != null) {
            if (action.equalsIgnoreCase(ACTION_PLAY)) {
                transportControls.play();
            } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
                transportControls.pause();
            } else if (action.equalsIgnoreCase(ACTION_STOP)) {
                transportControls.stop();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        pause();
        exoPlayer.release();
        exoPlayer.removeListener(this);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        notificationManager.cancelNotify();
        mediaSession.release();
        unregisterReceiver(becomingNoisyReceiver);
        super.onDestroy();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = RadioManager.PlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED:
                status = RadioManager.PlaybackStatus.STOPPED;
                break;
            case Player.STATE_IDLE:
                status = RadioManager.PlaybackStatus.IDLE;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? RadioManager.PlaybackStatus.PLAYING : RadioManager.PlaybackStatus.PAUSED;
                break;
        }
        notificationManager.startNotify(status);
        App.status.postValue(status);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(getApplicationContext(), "ההשמעה נכשלה, אנא נסו שוב מאוחר יותר", Toast.LENGTH_LONG).show();
    }

    public boolean isNotPlaying() {
        return !status.equals(RadioManager.PlaybackStatus.PLAYING);
    }

    public void play() {
        if (wifiLock != null && !wifiLock.isHeld()) {
            wifiLock.acquire();
        }
        exoPlayer.prepare(new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), DefaultBandwidthMeter.getSingletonInstance(this)))
                .createMediaSource(Uri.parse(getApplicationContext().getSharedPreferences(getPackageName(), MODE_PRIVATE).getString("StreamingUrl", streamingUrl))));
        exoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);
        wifiLockRelease();
    }

    public void stop() {
        exoPlayer.stop();
        wifiLockRelease();
    }

    public void playOrStop() {
        if (isNotPlaying()) {
            play();
        } else {
            stop();
            notificationManager.cancelNotify();
        }
    }

    public RadioManager.PlaybackStatus getStatus() {
        return status;
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    private void wifiLockRelease() {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    public class LocalBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }
}
