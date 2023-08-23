package zfani.assaf.radiokahollavan.player;

import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.S)
public class TelephonyCallBack extends TelephonyCallback implements TelephonyCallback.CallStateListener {

    private static volatile TelephonyCallBack INSTANCE;
    private final RadioService radioService;

    private TelephonyCallBack(RadioService radioService) {
        this.radioService = radioService;
    }

    public static TelephonyCallBack getInstance(RadioService radioService) {
        // Check if the instance is already created
        if (INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (TelephonyCallBack.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new TelephonyCallBack(radioService);
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
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
            new Handler().postDelayed(radioService::play, 500);
        }
    }
}