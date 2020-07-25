package zfani.assaf.radiokahollavan.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Calendar;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.model.Broadcast;
import zfani.assaf.radiokahollavan.model.BroadcastDataBase;

public class LiveBroadcastViewModel extends AndroidViewModel {

    public LiveBroadcastViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Broadcast> getBroadcast() {
        return BroadcastDataBase.getDatabase(getApplication()).getBroadcastDao().getBroadcast(App.daysArray[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1], System.currentTimeMillis());
    }
}