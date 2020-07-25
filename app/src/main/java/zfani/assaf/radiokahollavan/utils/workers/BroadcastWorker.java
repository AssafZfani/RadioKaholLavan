package zfani.assaf.radiokahollavan.utils.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.model.Broadcast;
import zfani.assaf.radiokahollavan.model.BroadcastDataBase;

public class BroadcastWorker extends ListenableWorker {

    public BroadcastWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        SettableFuture<Result> future = SettableFuture.create();
        try {
            for (String day : App.daysArray) {
                FirebaseFirestore.getInstance().collection("BroadcastSchedule").document("MainBroadcastSchedule").collection(day + "Shows").addSnapshotListener((queryDocumentSnapshot, e) -> {
                    if (queryDocumentSnapshot != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshot) {
                            Broadcast broadcast = document.toObject(Broadcast.class);
                            broadcast.setId(document.getId());
                            broadcast.setDay(day);
                            BroadcastDataBase.databaseWriteExecutor.execute(() -> {
                                BroadcastDataBase.getDatabase(getApplicationContext()).getBroadcastDao().insertBroadcast(broadcast);
                                future.set(Result.success());
                            });
                        }
                    }
                });
            }
        } catch (Exception ex) {
            future.setException(ex);
            future.set(Result.failure());
        }
        return future;
    }
}
