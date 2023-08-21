package zfani.assaf.radiokahollavan.player;

import android.os.Build;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.S)
public class TelephonyCallBack extends TelephonyCallback implements TelephonyCallback.CallStateListener {

    private final RadioService radioService;

    public TelephonyCallBack(RadioService radioService) {
        this.radioService = radioService;
    }

    @Override
    public void onCallStateChanged(int state) {
        if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING) {
            if (radioService.isNotPlaying()) return;
            radioService.onGoingCall = true;
            radioService.stop();
        } else if (state == TelephonyManager.CALL_STATE_IDLE) {
            if (!radioService.onGoingCall) return;
            radioService.onGoingCall = false;
            Executors.newSingleThreadScheduledExecutor().schedule(radioService::play, 1, TimeUnit.SECONDS);
        }
    }
}