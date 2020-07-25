package zfani.assaf.radiokahollavan.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import zfani.assaf.radiokahollavan.App;

public class RadioManager {

    private static RadioManager instance = null;
    private RadioService service;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            service = ((RadioService.LocalBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public static RadioManager getInstance() {
        if (instance == null) {
            instance = new RadioManager();
        }
        return instance;
    }

    public void playOrStop() {
        service.playOrStop();
    }

    public void bind(Context context) {
        Intent intent = new Intent(context, RadioService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (service != null) {
            App.status.postValue(service.getStatus());
        }
    }

    public void unbind(Context context) {
        context.unbindService(serviceConnection);
    }

    public enum PlaybackStatus {
        IDLE,
        LOADING,
        PLAYING,
        PAUSED,
        STOPPED
    }
}
