package zfani.assaf.radiokahollavan;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import zfani.assaf.radiokahollavan.player.RadioManager;
import zfani.assaf.radiokahollavan.player.RadioService;
import zfani.assaf.radiokahollavan.utils.workers.BroadcastWorker;
import zfani.assaf.radiokahollavan.utils.workers.CleanupWorker;

public class App extends Application {

    public static final String[] daysArray = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static LiveData<WorkInfo> broadcastWorkStatus;
    public static MutableLiveData<RadioManager.PlaybackStatus> status;
    public static MutableLiveData<String> appInfo, songTitle;

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
        WorkManager workManager = WorkManager.getInstance(this);
        WorkContinuation workContinuation = workManager.beginWith(new OneTimeWorkRequest.Builder(CleanupWorker.class).build());
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(BroadcastWorker.class).setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build();
        workContinuation = workContinuation.then(oneTimeWorkRequest);
        workContinuation.enqueue();
        broadcastWorkStatus = workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId());
    }

    private void initAppInfo() {
        appInfo = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("AppInfo").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documentSnapshots = task.getResult();
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
