package zfani.assaf.radiokahollavan.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import zfani.assaf.radiokahollavan.utils.TimestampConverter;

@Entity(tableName = "broadcast_table")
public class Broadcast {

    @PrimaryKey
    @NonNull
    private String id = "";
    private String broadcasterImageUrl, broadcasterName, name, description, day;
    @TypeConverters(TimestampConverter.class)
    private Date startDate, endDate;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getBroadcasterImageUrl() {
        return broadcasterImageUrl;
    }

    void setBroadcasterImageUrl(String broadcasterImageUrl) {
        this.broadcasterImageUrl = broadcasterImageUrl;
    }

    public String getBroadcasterName() {
        return broadcasterName;
    }

    void setBroadcasterName(String broadcasterName) {
        this.broadcasterName = broadcasterName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Date getStartDate() {
        return startDate;
    }

    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
