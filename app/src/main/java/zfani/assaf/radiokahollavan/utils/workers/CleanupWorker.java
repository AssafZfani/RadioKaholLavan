package zfani.assaf.radiokahollavan.utils.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import zfani.assaf.radiokahollavan.model.BroadcastDataBase;

public class CleanupWorker extends ListenableWorker {

    public CleanupWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        SettableFuture<Result> future = SettableFuture.create();
        try {
            BroadcastDataBase.databaseWriteExecutor.execute(() -> {
                BroadcastDataBase.getDatabase(getApplicationContext()).getBroadcastDao().deleteAllBroadcasts();
                future.set(Result.success());
            });
        } catch (Exception ex) {
            future.setException(ex);
            future.set(Result.failure());
        }
        return future;
    }
}
