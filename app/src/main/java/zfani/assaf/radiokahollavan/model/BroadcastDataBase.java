package zfani.assaf.radiokahollavan.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Broadcast.class}, version = 1, exportSchema = false)
public abstract class BroadcastDataBase extends RoomDatabase {

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);
    private static BroadcastDataBase INSTANCE;

    public static BroadcastDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (BroadcastDataBase.class) {
                INSTANCE = Room.databaseBuilder(context, BroadcastDataBase.class, "broadcast.db").fallbackToDestructiveMigration().build();
            }
        }
        return INSTANCE;
    }

    public abstract BroadcastDao getBroadcastDao();
}
