package zfani.assaf.radiokahollavan.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface BroadcastDao {

    @Query("Select * from broadcast_table where day = :day order by startDate")
    LiveData<List<Broadcast>> getAllBroadcastsByDay(String day);

    @Query("Select * from broadcast_table where day = :day and startDate <= :now and endDate >= :now")
    LiveData<Broadcast> getBroadcast(String day, Long now);

    @Insert(onConflict = REPLACE)
    void insertBroadcast(Broadcast broadcast);

    @Query("Delete from broadcast_table")
    void deleteAllBroadcasts();
}
