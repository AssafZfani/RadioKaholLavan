package zfani.assaf.radiokahollavan.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import zfani.assaf.radiokahollavan.model.Broadcast;
import zfani.assaf.radiokahollavan.model.BroadcastDataBase;

public class BroadcastScheduleViewModel extends AndroidViewModel {

    public BroadcastScheduleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Broadcast>> getBroadcastByDay(String day) {
        return BroadcastDataBase.getDatabase(getApplication()).getBroadcastDao().getAllBroadcastsByDay(day);
    }
}