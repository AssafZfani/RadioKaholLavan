package zfani.assaf.radiokahollavan;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zfani.assaf.radiokahollavan.model.Broadcast;
import zfani.assaf.radiokahollavan.player.RadioManager;
import zfani.assaf.radiokahollavan.player.RadioService;

public class App extends Application {

    public static final String[] daysArray = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static MutableLiveData<RadioManager.PlaybackStatus> status;
    public static MutableLiveData<String> appInfo, songTitle;
    public static MutableLiveData<HashMap<String, List<Broadcast>>> broadcasts;

    @Override
    public void onCreate() {
        super.onCreate();
        initLiveData();
        initAppInfo();
        initBroadcasts();
        initBlueToothReceiver();
    }

    private void initLiveData() {
        status = new MutableLiveData<>();
        songTitle = new MutableLiveData<>();
    }

    private void initBroadcasts() {
        broadcasts = new MutableLiveData<>();
        for (String day : daysArray) {
            FirebaseFirestore.getInstance().collection("BroadcastSchedule").document("MainBroadcastSchedule").collection(day + "Shows").addSnapshotListener((documentSnapshot, e) -> {
                List<Broadcast> broadcastList = new ArrayList<>();
                if (documentSnapshot != null) {
                    for (QueryDocumentSnapshot document : documentSnapshot) {
                        Broadcast broadcast = document.toObject(Broadcast.class);
                        broadcast.setId(document.getId());
                        broadcastList.add(broadcast);
                    }
                    HashMap<String, List<Broadcast>> map = broadcasts.getValue();
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(day, broadcastList);
                    broadcasts.setValue(map);
                }
            });
        }
    }

    private void initAppInfo() {
        appInfo = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("AppInfo").addSnapshotListener((documentSnapshots, error) -> {
            if (documentSnapshots != null) {
                for (QueryDocumentSnapshot document : documentSnapshots) {
                    Object contactDescription = document.getData().get("contactDescription");
                    appInfo.setValue(contactDescription == null ? "" : ((String) contactDescription).replaceAll("\\\\n", "\n"));
                    Object updatedStreamingUrl = document.getData().get("updatedStreamingUrl");
                    if (updatedStreamingUrl != null) {
                        String text = (String) updatedStreamingUrl;
                        if (!text.isEmpty()) {
                            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString("StreamingUrl", text).apply();
                        }
                    }
                    Object updatedRecentlyPlayedUrl = document.getData().get("updatedRecentlyPlayedUrl");
                    if (updatedRecentlyPlayedUrl != null) {
                        String text = (String) updatedRecentlyPlayedUrl;
                        if (!text.isEmpty()) {
                            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString("RecentlyPlayedUrl", text).apply();
                        }
                    }
                }
            }
        });
    }

    private void initBlueToothReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    stopService(new Intent(getApplicationContext(), RadioService.class));
                    System.exit(2);
                }
            }
        }, filter);
    }
}
