package zfani.assaf.radiokahollavan.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
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

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;

public class RadioService extends Service implements Player.Listener {

    public static final String ACTION_PLAY = "zfani.assaf.radiokahollavan.player.ACTION_PLAY";
    public static final String ACTION_PAUSE = "zfani.assaf.radiokahollavan.player.ACTION_PAUSE";
    public static final String ACTION_STOP = "zfani.assaf.radiokahollavan.player.ACTION_STOP";
    public static final String MAIN = "MAIN";
    public static final String YEMENI = "YEMENI";
    private static final String streamingUrl = "https://radiokahollavan.radioca.st/stream";
    private static final String yemeniStreamingUrl = "https://radiokahollavan.com/yemenstream";
    private final IBinder iBinder = new LocalBinder();
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    private WifiManager.WifiLock wifiLock;
    private MediaNotificationManager notificationManager;
    private RadioManager.PlaybackStatus status;
    private boolean isMainStreaming = true;
    private boolean isPlaying = false;
    private AudioManager audioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = new MediaNotificationManager(this);
        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, getPackageName());
        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setCallback(mediasSessionCallback);
        exoPlayer = new SimpleExoPlayer.Builder(this)
                .setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(this))
                .setTrackSelector(new DefaultTrackSelector(this, new AdaptiveTrackSelection.Factory()))
                .build();
        exoPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), false);
        exoPlayer.addListener(this);
        exoPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onMetadata(@NonNull EventTime eventTime, @NonNull Metadata metadata) {
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
        status = RadioManager.PlaybackStatus.PLAYING;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        play();
    }

    @Override
    public void onDestroy() {
        stop();
        exoPlayer.release();
        exoPlayer.removeListener(this);
        notificationManager.cancelNotify();
        mediaSession.release();
        audioManager.abandonAudioFocus(audioFocusChangeListener);
        super.onDestroy();
    }    private final MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPause() {
            super.onPause();
            stop();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return START_NOT_STICKY;
        }
        switch (action) {
            case ACTION_PLAY:
                transportControls.play();
                break;
            case ACTION_PAUSE:
                transportControls.pause();
                break;
            case ACTION_STOP:
                transportControls.stop();
                break;
            case MAIN:
                if (isMainStreaming) break;
                isMainStreaming = true;
                reset();
                break;
            case YEMENI:
                if (!isMainStreaming) break;
                isMainStreaming = false;
                reset();
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }

    public void play() {
        if (wifiLock != null && !wifiLock.isHeld()) {
            wifiLock.acquire();
        }
        int result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            exoPlayer.prepare(new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), DefaultBandwidthMeter.getSingletonInstance(this)))
                    .createMediaSource(MediaItem.fromUri(Uri.parse(isMainStreaming ? getApplicationContext()
                            .getSharedPreferences(getPackageName(), MODE_PRIVATE).getString("StreamingUrl", streamingUrl) : yemeniStreamingUrl))));
            exoPlayer.setPlayWhenReady(true);
            isPlaying = true;
        }
    }    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (!isPlaying) {
                    play();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying) {
                    stop();
                }
                break;
        }
    };

    public void stop() {
        exoPlayer.stop();
        isPlaying = false;
        wifiLockRelease();
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
        if (!status.equals(RadioManager.PlaybackStatus.LOADING)) {
            notificationManager.startNotify(status);
        }
        App.status.postValue(status);
    }

    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        Toast.makeText(getApplicationContext(), "ההשמעה נכשלה, אנא נסו שוב מאוחר יותר", Toast.LENGTH_LONG).show();
    }

    public boolean isNotPlaying() {
        return !status.equals(RadioManager.PlaybackStatus.PLAYING);
    }





    public void playOrStop() {
        if (isNotPlaying()) {
            play();
        } else {
            stop();
            notificationManager.cancelNotify();
        }
    }

    private void reset() {
        exoPlayer.prepare(new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), DefaultBandwidthMeter.getSingletonInstance(this)))
                .createMediaSource(MediaItem.fromUri(Uri.parse(isMainStreaming ? getApplicationContext()
                        .getSharedPreferences(getPackageName(), MODE_PRIVATE).getString("StreamingUrl", streamingUrl) : yemeniStreamingUrl))));
        exoPlayer.seekTo(0);
        exoPlayer.setPlayWhenReady(true);
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
