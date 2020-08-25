package zfani.assaf.radiokahollavan.model;

import java.util.Date;

public class Broadcast implements Comparable<Broadcast> {

    public String broadcasterImageUrl, broadcasterName, name, description;
    public Date startDate, endDate;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Broadcast other) {
        return this.startDate.compareTo(other.startDate);
    }
}
